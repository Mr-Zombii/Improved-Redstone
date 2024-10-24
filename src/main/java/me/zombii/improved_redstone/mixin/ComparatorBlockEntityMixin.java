package me.zombii.improved_redstone.mixin;

import me.zombii.improved_redstone.api.IRedstoneBlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ComparatorBlockEntity.class)
public class ComparatorBlockEntityMixin implements IRedstoneBlockEntity {
    @Shadow private int outputSignal;

    @Override
    public int getOutputSignal() {
        return outputSignal;
    }

    @Override
    public void setOutputSignal(int outputSignal) {
        this.outputSignal = outputSignal;
    }
}
