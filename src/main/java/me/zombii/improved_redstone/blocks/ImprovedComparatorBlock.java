package me.zombii.improved_redstone.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

/**
 * An improved comparator class that provides the implementation for the Comparator block. This makes comparators
 * nearly instant.
 *
 * @author M4ximumpizza
 */
public class ImprovedComparatorBlock extends ComparatorBlock {

    public static final EnumProperty<ComparatorMode> MODE;

    public ImprovedComparatorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false).with(MODE, ComparatorMode.COMPARE));
    }

    @Override
    public void updatePowered(World world, BlockPos pos, BlockState state) {
        if (!world.getBlockTickScheduler().isTicking(pos, this)) {
            int i = this.calculateOutputSignal(world, pos, state);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            int j = blockEntity instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)blockEntity).getOutputSignal() : 0;
            if (i != j || state.get(POWERED) != this.hasPower(world, pos, state)) {
                TickPriority tickPriority = this.isTargetNotAligned(world, pos, state) ? TickPriority.HIGH : TickPriority.NORMAL;
                world.scheduleBlockTick(pos, this, -1, tickPriority);
            }

        }
    }

    protected int getPower(World world, BlockPos pos, BlockState state) {
        int i = super.getPower(world, pos, state);
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.hasComparatorOutput()) {
            i = blockState.getComparatorOutput(world, blockPos);
        } else if (i < ImprovedRedstoneWireBlock.MaxStrength - 1 && blockState.isSolidBlock(world, blockPos)) {
            blockPos = blockPos.offset(direction);
            blockState = world.getBlockState(blockPos);
            ItemFrameEntity itemFrameEntity = this.getAttachedItemFrame(world, direction, blockPos);
            int j = Math.max(itemFrameEntity == null ? Integer.MIN_VALUE : itemFrameEntity.getComparatorPower(), blockState.hasComparatorOutput() ? blockState.getComparatorOutput(world, blockPos) : Integer.MIN_VALUE);
            if (j != Integer.MIN_VALUE) {
                i = j;
            }
        }

        return i;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, MODE, POWERED);
    }

    static {
        MODE = Properties.COMPARATOR_MODE;
    }
}
