package me.zombii.improved_redstone.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import me.zombii.improved_redstone.ImprovedBlocks;
import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ImprovedRedstoneWireBlock extends Block {
    public static final int MaxStrength = 32;

    public static final MapCodec<ImprovedRedstoneWireBlock> CODEC = createCodec(ImprovedRedstoneWireBlock::new);
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_NORTH;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_EAST;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_SOUTH;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_WEST;
    public static final IntProperty POWER;
    public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY;
    private static final VoxelShape DOT_SHAPE;
    private static final Map<Direction, VoxelShape> DIRECTION_TO_SIDE_SHAPE;
    private static final Map<Direction, VoxelShape> DIRECTION_TO_UP_SHAPE;
    private static final Map<BlockState, VoxelShape> SHAPES;
    private static final int[] COLORS;
    private final BlockState dotState;
    private boolean wiresGivePower = true;

    public MapCodec<ImprovedRedstoneWireBlock> getCodec() {
        return CODEC;
    }

    public ImprovedRedstoneWireBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WIRE_CONNECTION_NORTH, WireConnection.NONE).with(WIRE_CONNECTION_EAST, WireConnection.NONE).with(WIRE_CONNECTION_SOUTH, WireConnection.NONE).with(WIRE_CONNECTION_WEST, WireConnection.NONE).with(POWER, 0));
        this.dotState = this.getDefaultState().with(WIRE_CONNECTION_NORTH, WireConnection.SIDE).with(WIRE_CONNECTION_EAST, WireConnection.SIDE).with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE).with(WIRE_CONNECTION_WEST, WireConnection.SIDE);

        for (BlockState blockState : this.getStateManager().getStates()) {
            if (blockState.get(POWER) == 0) {
                SHAPES.put(blockState, this.getShapeForState(blockState));
            }
        }

    }

    private VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = DOT_SHAPE;

        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            if (wireConnection == WireConnection.SIDE) {
                voxelShape = VoxelShapes.union(voxelShape, DIRECTION_TO_SIDE_SHAPE.get(direction));
            } else if (wireConnection == WireConnection.UP) {
                voxelShape = VoxelShapes.union(voxelShape, DIRECTION_TO_UP_SHAPE.get(direction));
            }
        }

        return voxelShape;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.with(POWER, 0));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getPlacementState(ctx.getWorld(), this.dotState, ctx.getBlockPos());
    }

    private BlockState getPlacementState(BlockView world, BlockState state, BlockPos pos) {
        boolean initialNotConnected = isNotConnected(state);
        state = this.getDefaultWireState(world, this.getDefaultState().with(POWER, state.get(POWER)), pos);

        if (initialNotConnected && isNotConnected(state)) {
            return state;
        }

        boolean northConnected = state.get(WIRE_CONNECTION_NORTH).isConnected();
        boolean southConnected = state.get(WIRE_CONNECTION_SOUTH).isConnected();
        boolean eastConnected = state.get(WIRE_CONNECTION_EAST).isConnected();
        boolean westConnected = state.get(WIRE_CONNECTION_WEST).isConnected();

        boolean noVerticalConnections = !northConnected && !southConnected;
        boolean noHorizontalConnections = !eastConnected && !westConnected;

        if (!westConnected && noVerticalConnections) {
            state = state.with(WIRE_CONNECTION_WEST, WireConnection.SIDE);
        }
        if (!eastConnected && noVerticalConnections) {
            state = state.with(WIRE_CONNECTION_EAST, WireConnection.SIDE);
        }
        if (!northConnected && noHorizontalConnections) {
            state = state.with(WIRE_CONNECTION_NORTH, WireConnection.SIDE);
        }
        if (!southConnected && noHorizontalConnections) {
            state = state.with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE);
        }

        return state;
    }

    private BlockState getDefaultWireState(BlockView world, BlockState state, BlockPos pos) {
        boolean bl = !world.getBlockState(pos.up()).isSolidBlock(world, pos);

        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (!((WireConnection) state.get((Property) DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected()) {
                WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction, bl);
                state = state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection);
            }
        }

        return state;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            return !this.canRunOnTop(world, neighborPos, neighborState) ? Blocks.AIR.getDefaultState() : state;
        } else if (direction == Direction.UP) {
            return this.getPlacementState(world, state, pos);
        } else {
            WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction);
            return wireConnection.isConnected() == ((WireConnection)state.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected() && !isFullyConnected(state) ? state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection) : this.getPlacementState(world, this.dotState.with(POWER, (Integer)state.get(POWER)).with((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection), pos);
        }
    }

    private static boolean isFullyConnected(BlockState state) {
        return state.get(WIRE_CONNECTION_NORTH).isConnected() && state.get(WIRE_CONNECTION_SOUTH).isConnected() && state.get(WIRE_CONNECTION_EAST).isConnected() && state.get(WIRE_CONNECTION_WEST).isConnected();
    }

    private static boolean isNotConnected(BlockState state) {
        return !state.get(WIRE_CONNECTION_NORTH).isConnected() && !state.get(WIRE_CONNECTION_SOUTH).isConnected() && !state.get(WIRE_CONNECTION_EAST).isConnected() && !state.get(WIRE_CONNECTION_WEST).isConnected();
    }

    protected void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            if (wireConnection != WireConnection.NONE) {
                BlockState adjacentState = world.getBlockState(mutable.set(pos, direction));
                if (!adjacentState.isOf(this)) {
                    mutable.move(Direction.DOWN);
                    BlockState blockStateBelow = world.getBlockState(mutable);
                    if (blockStateBelow.isOf(this)) {
                        BlockPos blockPosBelow = mutable.offset(direction.getOpposite());
                        world.replaceWithStateForNeighborUpdate(direction.getOpposite(), blockPosBelow, mutable, world.getBlockState(blockPosBelow), flags, maxUpdateDepth);
                    }

                    mutable.set(pos, direction).move(Direction.UP);
                    BlockState blockStateAbove = world.getBlockState(mutable);
                    if (blockStateAbove.isOf(this)) {
                        BlockPos blockPosAbove = mutable.offset(direction.getOpposite());
                        world.replaceWithStateForNeighborUpdate(direction.getOpposite(), blockPosAbove, mutable, world.getBlockState(blockPosAbove), flags, maxUpdateDepth);
                    }
                }
            }
        }
    }

    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction) {
        return this.getRenderConnectionType(world, pos, direction, !world.getBlockState(pos.up()).isSolidBlock(world, pos));
    }

    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction, boolean bl) {
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (bl) {
            boolean bl2 = blockState.getBlock() instanceof TrapdoorBlock || this.canRunOnTop(world, blockPos, blockState);
            if (bl2 && connectsTo(world.getBlockState(blockPos.up()))) {
                if (blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
                    return WireConnection.UP;
                }

                return WireConnection.SIDE;
            }
        }

        return !connectsTo(blockState, direction) && (blockState.isSolidBlock(world, blockPos) || !connectsTo(world.getBlockState(blockPos.down()))) ? WireConnection.NONE : WireConnection.SIDE;
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return this.canRunOnTop(world, blockPos, blockState);
    }

    private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(Blocks.HOPPER);
    }

    private void update(World world, BlockPos pos, BlockState state) {
        int i = this.getReceivedRedstonePower(world, pos);
        if (state.get(POWER) != i) {
            if (world.getBlockState(pos) == state) {
                world.setBlockState(pos, state.with(POWER, i), 2);
            }

            Set<BlockPos> set = Sets.newHashSet();
            set.add(pos);
            Direction[] var6 = Direction.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                Direction direction = var6[var8];
                set.add(pos.offset(direction));
            }

            Iterator var10 = set.iterator();

            while(var10.hasNext()) {
                BlockPos blockPos = (BlockPos)var10.next();
                world.updateNeighborsAlways(blockPos, this);
            }
        }

    }

    private int getReceivedRedstonePower(World world, BlockPos pos) {
        this.wiresGivePower = false;
        int receivedPower = world.getReceivedRedstonePower(pos);
        this.wiresGivePower = true;

        int maxPower = 0;

        if (receivedPower < MaxStrength - 1) {
            for (Direction direction : Direction.Type.HORIZONTAL) {
                BlockPos offsetPos = pos.offset(direction);
                BlockState offsetState = world.getBlockState(offsetPos);
                maxPower = Math.max(maxPower, this.increasePower(offsetState));

                BlockPos abovePos = pos.up();
                if (offsetState.isSolidBlock(world, offsetPos) && !world.getBlockState(abovePos).isSolidBlock(world, abovePos)) {
                    maxPower = Math.max(maxPower, this.increasePower(world.getBlockState(offsetPos.up())));
                } else if (!offsetState.isSolidBlock(world, offsetPos)) {
                    maxPower = Math.max(maxPower, this.increasePower(world.getBlockState(offsetPos.down())));
                }
            }
        }

        return Math.max(receivedPower, maxPower - 1);
    }

    private int increasePower(BlockState state) {
        return state.isOf(this) ? state.get(POWER) : 0;
    }

    private void updateNeighbors(World world, BlockPos pos) {
        if (world.getBlockState(pos).isOf(this)) {
            world.updateNeighborsAlways(pos, this);
            Direction[] var3 = Direction.values();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Direction direction = var3[var5];
                world.updateNeighborsAlways(pos.offset(direction), this);
            }

        }
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock()) && !world.isClient) {
            this.update(world, pos, state);

            for (Direction direction : Direction.Type.VERTICAL) {
                world.updateNeighborsAlways(pos.offset(direction), this);
            }

            this.updateOffsetNeighbors(world, pos);
        }
    }

    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!moved && !state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            if (!world.isClient) {
                Direction[] var6 = Direction.values();
                int var7 = var6.length;

                for (Direction direction : var6) {
                    world.updateNeighborsAlways(pos.offset(direction), this);
                }

                this.update(world, pos, state);
                this.updateOffsetNeighbors(world, pos);
            }
        }
    }

    private void updateOffsetNeighbors(World world, BlockPos pos) {
        Iterator<Direction> var3 = Direction.Type.HORIZONTAL.iterator();

        Direction direction;
        while(var3.hasNext()) {
            direction = (Direction)var3.next();
            this.updateNeighbors(world, pos.offset(direction));
        }

        var3 = Direction.Type.HORIZONTAL.iterator();

        while(var3.hasNext()) {
            direction = var3.next();
            BlockPos blockPos = pos.offset(direction);
            if (world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
                this.updateNeighbors(world, blockPos.up());
            } else {
                this.updateNeighbors(world, blockPos.down());
            }
        }

    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (!world.isClient) {
            if (state.canPlaceAt(world, pos)) {
                this.update(world, pos, state);
            } else {
                dropStacks(state, world, pos);
                world.removeBlock(pos, false);
            }

        }
    }

    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return !this.wiresGivePower ? 0 : state.getWeakRedstonePower(world, pos, direction);
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (this.wiresGivePower && direction != Direction.DOWN) {
            int i = state.get(POWER);
            if (i == 0) {
                return 0;
            } else {
                return direction != Direction.UP && !((WireConnection)this.getPlacementState(world, state, pos).get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction.getOpposite()))).isConnected() ? 0 : i;
            }
        } else {
            return 0;
        }
    }

    protected static boolean connectsTo(BlockState state) {
        return connectsTo(state, null);
    }

    protected static boolean connectsTo(BlockState state, @Nullable Direction dir) {
        if (state.isOf(ImprovedBlocks.IMPROVED_REDSTONE) || state.isOf(Blocks.REDSTONE_WIRE)) {
            return true;
        } else if (state.isOf(Blocks.REPEATER)) {
            Direction direction = state.get(RepeaterBlock.FACING);
            return direction == dir || direction.getOpposite() == dir;
        } else if (state.isOf(Blocks.OBSERVER)) {
            return dir == state.get(ObserverBlock.FACING);
        } else if (state.isOf(ImprovedBlocks.RGB_PANEL)) {
            return dir == state.get(RGBPanel.FACING);
        } else {
            return state.emitsRedstonePower() && dir != null;
        }
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return this.wiresGivePower;
    }

    public static int getBlockColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        return getWireColor(state.get(POWER));
    }

    public static int getWireColor(int powerLevel) {
        return COLORS[powerLevel];
    }

    private void addPoweredParticles(World world, Random random, BlockPos pos, int color, Direction direction, Direction direction2, float f, float g) {
        float h = g - f;
        if (!(random.nextFloat() >= 0.2F * h)) {
            float j = f + h * random.nextFloat();
            double d = 0.5 + (double)(0.4375F * (float)direction.getOffsetX()) + (double)(j * (float)direction2.getOffsetX());
            double e = 0.5 + (double)(0.4375F * (float)direction.getOffsetY()) + (double)(j * (float)direction2.getOffsetY());
            double k = 0.5 + (double)(0.4375F * (float)direction.getOffsetZ()) + (double)(j * (float)direction2.getOffsetZ());
            world.addParticle(new DustParticleEffect(color, 1.0F), (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + k, 0.0, 0.0, 0.0);
        }
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int i = state.get(POWER);
        if (i != 0) {

            for (Direction direction : Direction.Type.HORIZONTAL) {
                WireConnection wireConnection = (WireConnection) state.get((Property) DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
                switch (wireConnection) {
                    case UP:
                        this.addPoweredParticles(world, random, pos, COLORS[MaxStrength - 1], direction, Direction.UP, -0.5F, 0.5F);
                    case SIDE:
                        this.addPoweredParticles(world, random, pos, COLORS[MaxStrength - 1], Direction.DOWN, direction, 0.0F, 0.5F);
                        break;
                    case NONE:
                    default:
                        this.addPoweredParticles(world, random, pos, COLORS[MaxStrength - 1], Direction.DOWN, direction, 0.0F, 0.3F);
                }
            }

        }
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180 -> {
                return state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH)).with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST)).with(WIRE_CONNECTION_SOUTH, (WireConnection)state.get(WIRE_CONNECTION_NORTH)).with(WIRE_CONNECTION_WEST, (WireConnection)state.get(WIRE_CONNECTION_EAST));
            }
            case COUNTERCLOCKWISE_90 -> {
                return state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_EAST)).with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_SOUTH)).with(WIRE_CONNECTION_SOUTH, (WireConnection)state.get(WIRE_CONNECTION_WEST)).with(WIRE_CONNECTION_WEST, (WireConnection)state.get(WIRE_CONNECTION_NORTH));
            }
            case CLOCKWISE_90 -> {
                return state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_WEST)).with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_NORTH)).with(WIRE_CONNECTION_SOUTH, (WireConnection)state.get(WIRE_CONNECTION_EAST)).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_SOUTH));
            }
            default -> {
                return state;
            }
        }
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT -> {
                return state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH)).with(WIRE_CONNECTION_SOUTH, (WireConnection)state.get(WIRE_CONNECTION_NORTH));
            }
            case FRONT_BACK -> {
                return state.with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST)).with(WIRE_CONNECTION_WEST, (WireConnection)state.get(WIRE_CONNECTION_EAST));
            }
            default -> {
                return super.mirror(state, mirror);
            }
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST, WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST, POWER);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        } else {
            if (isFullyConnected(state) || isNotConnected(state)) {
                BlockState blockState = isFullyConnected(state) ? this.getDefaultState() : this.dotState;
                blockState = blockState.with(POWER, state.get(POWER));
                blockState = this.getPlacementState(world, blockState, pos);
                if (blockState != state) {
                    world.setBlockState(pos, blockState, 3);
                    this.updateForNewState(world, pos, state, blockState);
                    return ActionResult.SUCCESS;
                }
            }

            return ActionResult.PASS;
        }
    }

    private void updateForNewState(World world, BlockPos pos, BlockState oldState, BlockState newState) {
        WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation(world, null, Direction.UP);

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            if (oldState.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected() != ((WireConnection) newState.get((Property) DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected() && world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
                world.updateNeighborsExcept(blockPos, newState.getBlock(), direction.getOpposite(), OrientationHelper.withFrontNullable(wireOrientation, direction));
            }
        }

    }

    static {
        WIRE_CONNECTION_NORTH = Properties.NORTH_WIRE_CONNECTION;
        WIRE_CONNECTION_EAST = Properties.EAST_WIRE_CONNECTION;
        WIRE_CONNECTION_SOUTH = Properties.SOUTH_WIRE_CONNECTION;
        WIRE_CONNECTION_WEST = Properties.WEST_WIRE_CONNECTION;
        POWER = IntProperty.of("power", 0, MaxStrength - 1);
        DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, WIRE_CONNECTION_NORTH, Direction.EAST, WIRE_CONNECTION_EAST, Direction.SOUTH, WIRE_CONNECTION_SOUTH, Direction.WEST, WIRE_CONNECTION_WEST));
        DOT_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
        DIRECTION_TO_SIDE_SHAPE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0), Direction.SOUTH, Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0), Direction.EAST, Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0), Direction.WEST, Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0)));
        DIRECTION_TO_UP_SHAPE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction.NORTH), Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)), Direction.SOUTH, VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction.SOUTH), Block.createCuboidShape(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)), Direction.EAST, VoxelShapes.union((VoxelShape)DIRECTION_TO_SIDE_SHAPE.get(Direction.EAST), Block.createCuboidShape(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)), Direction.WEST, VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction.WEST), Block.createCuboidShape(0.0, 0.0, 3.0, 1.0, 16.0, 13.0))));
        SHAPES = Maps.newHashMap();

        COLORS = Util.make(new int[MaxStrength], (colors) -> {
            for(int i = 0; i <= MaxStrength - 1; ++i) {
                float f = ((float)i / (MaxStrength - 1));
                f = 1 - f;
                float r = 255 * (1 - f);
                float g = 96 * (1 - f);
                float b = 0 * (1 - f);
                colors[i] = ColorHelper.fromFloats(1f, r / 255f, g / 255f, b / 255f);
//                colors[i] = new Vec3d(0, 0.541F, 1);
            }
        });
    }
}
