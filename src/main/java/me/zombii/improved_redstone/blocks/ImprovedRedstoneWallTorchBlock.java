package me.zombii.improved_redstone.blocks;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ImprovedRedstoneWallTorchBlock extends WallRedstoneTorchBlock {

    public static final DirectionProperty FACING;
    public static final BooleanProperty LIT;

    public ImprovedRedstoneWallTorchBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(LIT, true));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (state.get(LIT) == this.shouldUnpower(world, pos, state) && !world.getBlockTickScheduler().isTicking(pos, this)) {
            world.scheduleBlockTick(pos, this, -1);
        }
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(LIT) && state.get(FACING) != direction ? ImprovedRedstoneWireBlock.MaxStrength - 1 : 0;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean bl = this.shouldUnpower(world, pos, state);

        if (state.get(LIT)) {
            if (bl) {
                world.setBlockState(pos, state.with(LIT, false), 3);
            }
        } else world.setBlockState(pos, state.with(LIT, true), 3);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    static {
        FACING = HorizontalFacingBlock.FACING;
        LIT = RedstoneTorchBlock.LIT;
    }
}
