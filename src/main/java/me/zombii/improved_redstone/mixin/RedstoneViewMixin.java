package me.zombii.improved_redstone.mixin;

import me.zombii.improved_redstone.blocks.ImprovedRedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RedstoneView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(RedstoneView.class)
public interface RedstoneViewMixin extends RedstoneView {

    /**
     * @author Mr_Zombii
     * @reason Make Redstone Go Longer
     */
    @Overwrite
    default int getReceivedStrongRedstonePower(BlockPos pos) {
        int maxPower = 0;
        Direction[] directions = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

        for (Direction direction : directions) {
            maxPower = Math.max(maxPower, this.getStrongRedstonePower(pos.offset(direction), direction));
            if (maxPower >= ImprovedRedstoneWireBlock.MaxStrength - 1) {
                return maxPower;
            }
        }

        return maxPower;
    }


    /**
     * @author Mr_Zombii
     * @reason Make Redstone Go Longer
     */
    @Overwrite
    default int getReceivedRedstonePower(BlockPos pos) {
        int i = 0;

        for (Direction direction : DIRECTIONS) {
            int j = this.getEmittedRedstonePower(pos.offset(direction), direction);
            if (j >= ImprovedRedstoneWireBlock.MaxStrength - 1) {
                return ImprovedRedstoneWireBlock.MaxStrength - 1;
            }

            if (j > i) {
                i = j;
            }
        }

        return i;
    }

}
