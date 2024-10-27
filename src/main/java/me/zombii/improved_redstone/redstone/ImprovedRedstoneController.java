package me.zombii.improved_redstone.redstone;

import me.zombii.improved_redstone.blocks.ImprovedRedstoneWireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Type;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public abstract class ImprovedRedstoneController {
    protected final ImprovedRedstoneWireBlock wire;

    protected ImprovedRedstoneController(ImprovedRedstoneWireBlock wire) {
        this.wire = wire;
    }

    public abstract void update(World world, BlockPos pos, BlockState state, @Nullable WireOrientation orientation, boolean blockAdded);

    protected int getStrongPowerAt(World world, BlockPos pos) {
        return this.wire.getStrongPower(world, pos);
    }

    protected int getWirePowerAt(BlockPos world, BlockState pos) {
        return pos.isOf(this.wire) ? pos.get(ImprovedRedstoneWireBlock.POWER) : 0;
    }

    protected int calculateWirePowerAt(World world, BlockPos pos) {
        int i = 0;

        for (Direction direction : Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            i = Math.max(i, this.getWirePowerAt(blockPos, blockState));
            BlockPos blockPos2 = pos.up();
            BlockPos blockPos3;
            if (blockState.isSolidBlock(world, blockPos) && !world.getBlockState(blockPos2).isSolidBlock(world, blockPos2)) {
                blockPos3 = blockPos.up();
                i = Math.max(i, this.getWirePowerAt(blockPos3, world.getBlockState(blockPos3)));
            } else if (!blockState.isSolidBlock(world, blockPos)) {
                blockPos3 = blockPos.down();
                i = Math.max(i, this.getWirePowerAt(blockPos3, world.getBlockState(blockPos3)));
            }
        }

        return Math.max(0, i - 1);
    }
}
