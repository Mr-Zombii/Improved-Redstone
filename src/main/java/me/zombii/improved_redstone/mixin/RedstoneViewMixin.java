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
        int i = 0;
        i = Math.max(i, this.getStrongRedstonePower(pos.down(), Direction.DOWN));
        if (i >= ImprovedRedstoneWireBlock.MaxStrength - 1) {
            return i;
        } else {
            i = Math.max(i, this.getStrongRedstonePower(pos.up(), Direction.UP));
            if (i >= ImprovedRedstoneWireBlock.MaxStrength - 1) {
                return i;
            } else {
                i = Math.max(i, this.getStrongRedstonePower(pos.north(), Direction.NORTH));
                if (i >= ImprovedRedstoneWireBlock.MaxStrength - 1) {
                    return i;
                } else {
                    i = Math.max(i, this.getStrongRedstonePower(pos.south(), Direction.SOUTH));
                    if (i >= ImprovedRedstoneWireBlock.MaxStrength - 1) {
                        return i;
                    } else {
                        i = Math.max(i, this.getStrongRedstonePower(pos.west(), Direction.WEST));
                        if (i >= ImprovedRedstoneWireBlock.MaxStrength - 1) {
                            return i;
                        } else {
                            i = Math.max(i, this.getStrongRedstonePower(pos.east(), Direction.EAST));
                            return i >= (ImprovedRedstoneWireBlock.MaxStrength - 1) ? i : i;
                        }
                    }
                }
            }
        }
    }


    /**
     * @author Mr_Zombii
     * @reason Make Redstone Go Longer
     */
    @Overwrite
    default int getReceivedRedstonePower(BlockPos pos) {
        int i = 0;
        Direction[] var3 = DIRECTIONS;
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Direction direction = var3[var5];
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
