package me.zombii.improved_redstone.blocks;

import com.mojang.serialization.MapCodec;
import me.zombii.improved_redstone.ImprovedBlockEntityTypes;
import me.zombii.improved_redstone.api.IRedstoneBlockEntity;
import me.zombii.improved_redstone.blocks.entities.RGBPanelBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class RGBPanel extends HorizontalFacingBlock implements BlockEntityProvider {
    public static final BooleanProperty LIT;

    public static final MapCodec<RGBPanel> CODEC = createCodec(RGBPanel::new);
    public static final int[] COLORS = new int[ImprovedRedstoneWireBlock.MaxStrength];

    public static int rgb01(float r, float g, float b) {
        return ColorHelper.fromFloats(1F, r, g, b);
    }

    public static int toRGB(String rgb) {
        Color c = Color.decode(rgb);
        return ColorHelper.fromFloats(1, c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
    }

    static {
        COLORS[0] = rgb01(0, 0 ,0);
        COLORS[1] = toRGB("#ff0000");
        COLORS[2] = toRGB("#a00000");
        COLORS[3] = toRGB("#ff8500");
        COLORS[4] = toRGB("#a05400");
        COLORS[5] = toRGB("#ffd900");
        COLORS[6] = toRGB("#927c00");
        COLORS[7] = toRGB("#97ff00");
        COLORS[8] = toRGB("#62a600");
        COLORS[9] = toRGB("#2cff00");
        COLORS[10] = toRGB("#1b9d00");
        COLORS[11] = toRGB("#00ff9d");
        COLORS[12] = toRGB("#00a264");
        COLORS[13] = toRGB("#00faff");
        COLORS[14] = toRGB("#009396");
        COLORS[15] = toRGB("#006cff");
        COLORS[16] = toRGB("#004096");
        COLORS[17] = toRGB("#1300ff");
        COLORS[18] = toRGB("#0b0095");
        COLORS[19] = toRGB("#8900ff");
        COLORS[20] = toRGB("#4d008f");
        COLORS[21] = toRGB("#ff00fb");
        COLORS[22] = toRGB("#8f008d");
        COLORS[23] = toRGB("#ff00c0");
        COLORS[24] = toRGB("#ff00c0");
        COLORS[25] = toRGB("#ff006d");
        COLORS[26] = toRGB("#94003f");
        COLORS[27] = toRGB("#c9c9c9");
        COLORS[28] = toRGB("#949494");
        COLORS[29] = toRGB("#5a5a5a");
        COLORS[30] = toRGB("#3a3a3a");
        COLORS[31] = rgb01(1, 1 ,1);
    }

    public static int getBlockColor(BlockState blockState, BlockRenderView blockRenderView, BlockPos blockPos, int tintIndex) {
        if (blockRenderView.getBlockEntity(blockPos) instanceof IRedstoneBlockEntity entity) {
            return COLORS[entity.getOutputSignal()];
        }
        return 0;
    }

    public RGBPanel(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(LIT, false).with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int power = world.getEmittedRedstonePower(pos.offset(state.get(FACING).getOpposite()), state.get(FACING).getOpposite());
        boolean isGettingPower = power > 0;

        if (state.get(LIT) && isGettingPower) {
            ((IRedstoneBlockEntity)world.getBlockEntity(pos)).setOutputSignal(power);
            world.setBlockState(pos, state, 2);
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int power = world.getEmittedRedstonePower(pos.offset(state.get(FACING).getOpposite()), state.get(FACING).getOpposite());
        boolean isGettingPower = power > 0;

        if (state.get(LIT) && !isGettingPower) {
            ((IRedstoneBlockEntity)world.getBlockEntity(pos)).setOutputSignal(power);
            world.setBlockState(pos, state.cycle(LIT), 2);
        }
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(LIT, ctx.getWorld().isEmittingRedstonePower(ctx.getBlockPos().offset(ctx.getHorizontalPlayerFacing()), ctx.getHorizontalPlayerFacing()))
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (!world.isClient) {
            int power = world.getEmittedRedstonePower(pos.offset(state.get(FACING).getOpposite()), state.get(FACING).getOpposite());
            boolean isGettingPower = power > 0;

            if (state.get(LIT) && isGettingPower) {
                world.setBlockState(pos, state, 2);
            }

            if (state.get(LIT) != isGettingPower) {
                if (state.get(LIT)) {
                    world.scheduleBlockTick(pos, this, -1);
                } else {
                    ((IRedstoneBlockEntity)world.getBlockEntity(pos)).setOutputSignal(power);
                    world.setBlockState(pos, state.cycle(LIT), 2);
                }
            }

        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING);
    }

    static {
        LIT = Properties.LIT;
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> validateTicker(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ImprovedBlockEntityTypes.RGB_PANEL, RGBPanelBlockEntity::tick);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RGBPanelBlockEntity(pos, state);
    }
}
