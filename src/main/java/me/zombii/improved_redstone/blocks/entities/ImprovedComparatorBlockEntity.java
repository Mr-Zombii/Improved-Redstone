package me.zombii.improved_redstone.blocks.entities;

import me.zombii.improved_redstone.ImprovedBlockEntityTypes;
import me.zombii.improved_redstone.api.IRedstoneBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class ImprovedComparatorBlockEntity extends BlockEntity implements IRedstoneBlockEntity {
    private int outputSignal;

    public ImprovedComparatorBlockEntity(BlockPos pos, BlockState state) {
        super(ImprovedBlockEntityTypes.IMRPOVED_COMPARATOR, pos, state);
    }

    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("OutputSignal", this.outputSignal);
    }

    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.outputSignal = nbt.getInt("OutputSignal");
    }

    @Override
    public int getOutputSignal() {
        return this.outputSignal;
    }

    @Override
    public void setOutputSignal(int outputSignal) {
        this.outputSignal = outputSignal;
    }
}
