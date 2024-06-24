package me.zombii.improved_redstone.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RGBPanel extends HorizontalFacingBlock {
    public static final BooleanProperty LIT;
    public static final IntProperty POWER;

    public static final MapCodec<RGBPanel> CODEC = createCodec(RGBPanel::new);

    public RGBPanel(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(LIT, false).with(POWER, 0).with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }



    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int power = world.getEmittedRedstonePower(pos.offset(state.get(FACING).getOpposite()), state.get(FACING).getOpposite());
        boolean isGettingPower = power > 0;
        if (power >= 15) power = 15;

        if (state.get(LIT) && isGettingPower) {
            world.setBlockState(pos, state.with(POWER, power), 2);
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int power = world.getEmittedRedstonePower(pos.offset(state.get(FACING).getOpposite()), state.get(FACING).getOpposite());
        boolean isGettingPower = power > 0;
        if (power >= 15) power = 15;

        if (state.get(LIT) && !isGettingPower) {
            world.setBlockState(pos, state.cycle(LIT), 2);
        }
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        int power = ctx.getWorld().getEmittedRedstonePower(ctx.getBlockPos().offset(ctx.getHorizontalPlayerFacing()), ctx.getHorizontalPlayerFacing());
        if (power >= 15) power = 15;
        return this.getDefaultState()
                .with(LIT, ctx.getWorld().isEmittingRedstonePower(ctx.getBlockPos().offset(ctx.getHorizontalPlayerFacing()), ctx.getHorizontalPlayerFacing()))
                .with(POWER, power)
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            int power = world.getEmittedRedstonePower(pos.offset(state.get(FACING).getOpposite()), state.get(FACING).getOpposite());
            boolean isGettingPower = power > 0;
            if (power >= 15) power = 15;

            if (state.get(LIT) && isGettingPower) world.setBlockState(pos, state.with(POWER, power), 2);

            if (state.get(LIT) != isGettingPower) {
                if (state.get(LIT)) {
                    world.scheduleBlockTick(pos, this, -1);
                } else {
                    world.setBlockState(pos, state.with(POWER, power).cycle(LIT), 2);
                }
            }

        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, POWER, FACING);
    }

    static {
        LIT = Properties.LIT;
        POWER = Properties.POWER;
    }
}
