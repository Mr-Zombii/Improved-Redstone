package me.zombii.improved_redstone.mixin.client;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public class InvertedPlayerMixin {

    @Unique
    private static final List<String> upsideDownUUIDS = List.of(
            "7b05bc2d-14d3-40b1-bf90-05a5a36649e5", // M4ximumPizza
            "27c5d8e7-889c-4c40-b63c-c1d54db72580" // Mr_Zombii
    );

    public InvertedPlayerMixin() {
    }

    @Inject(
            method = {"shouldFlipUpsideDown"},
            at = {@At("RETURN")},
            cancellable = true
    )
    private static void modifyShouldFlipUpsideDown(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(upsideDownUUIDS.contains(entity.getUuid().toString()));
    }

}