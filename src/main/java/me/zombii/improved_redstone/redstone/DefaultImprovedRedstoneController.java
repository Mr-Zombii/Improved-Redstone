package me.zombii.improved_redstone.redstone;

import com.google.common.collect.Sets;
import java.util.Set;

import me.zombii.improved_redstone.blocks.ImprovedRedstoneWireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class DefaultImprovedRedstoneController extends ImprovedRedstoneController {
    public DefaultImprovedRedstoneController(ImprovedRedstoneWireBlock redstoneWireBlock) {
        super(redstoneWireBlock);
    }

    public void update(World world, BlockPos pos, BlockState state, @Nullable WireOrientation orientation, boolean blockAdded) {
        int i = this.calculateTotalPowerAt(world, pos);
        if (state.get(ImprovedRedstoneWireBlock.POWER) != i) {
            if (world.getBlockState(pos) == state) {
                world.setBlockState(pos, state.with(ImprovedRedstoneWireBlock.POWER, i), 2);
            }

            Set<BlockPos> set = Sets.newHashSet();
            set.add(pos);

            for (Direction direction : Direction.values()) {
                set.add(pos.offset(direction));
            }

            for (BlockPos blockPos : set) {
                world.updateNeighborsAlways(blockPos, this.wire);
            }
        }

    }

    private int calculateTotalPowerAt(World world, BlockPos pos) {
        int i = this.getStrongPowerAt(world, pos);
        return i == ImprovedRedstoneWireBlock.MaxStrength - 1 ? i : Math.max(i, this.calculateWirePowerAt(world, pos));
    }
}
