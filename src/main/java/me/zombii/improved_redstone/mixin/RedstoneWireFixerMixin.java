package me.zombii.improved_redstone.mixin;

import me.zombii.improved_redstone.ImprovedBlocks;
import me.zombii.improved_redstone.blocks.RGBPanel;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.Map;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireFixerMixin {

    @Shadow @Final private static int[] COLORS;

    @Shadow @Final public static IntProperty POWER;

    @Shadow @Final public static Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY;

    @Shadow private boolean wiresGivePower;

    @Shadow
    private static void addPoweredParticles(World world, Random random, BlockPos pos, int color, Direction perpendicular, Direction direction, float minOffset, float maxOffset) {
    }

    /**
     * @author Mr_Zombii
     * @reason Fix power level being over 15 fixing an overflow crash
     */
    @Overwrite
    public static int getWireColor(int powerLevel) {
        if (powerLevel >= 15) powerLevel = 15;
        return COLORS[powerLevel];
    }

    /**
     * @author Mr_Zombii
     * @reason Fix a power level overflow crash with `int i` being over 15
     */
    @Overwrite
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int i = state.get(POWER);
        if (i >= 15) i = 15;

        if (i != 0) {

            for (Direction direction : Direction.Type.HORIZONTAL) {
                WireConnection wireConnection = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
                switch (wireConnection) {
                    case UP:
                        addPoweredParticles(world, random, pos, COLORS[i], direction, Direction.UP, -0.5F, 0.5F);
                    case SIDE:
                        addPoweredParticles(world, random, pos, COLORS[i], Direction.DOWN, direction, 0.0F, 0.5F);
                        break;
                    case NONE:
                    default:
                        addPoweredParticles(world, random, pos, COLORS[i], Direction.DOWN, direction, 0.0F, 0.3F);
                }
            }

        }
    }

    @Inject(method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z", at = @At("HEAD"), cancellable = true)
    private static void connectsTo(BlockState state, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        if (state.isOf(ImprovedBlocks.RGB_PANEL)) cir.setReturnValue(dir == state.get(RGBPanel.FACING));
    }

    /**
     * @author Mr_Zombii
     * @reason forcing `int i` to be 15 to prevent power level related crashes from happening
     */
    @Overwrite
    public int getStrongPower(World world, BlockPos pos) {
        this.wiresGivePower = false;
        int i = world.getReceivedRedstonePower(pos);
        if (i >= 15) i = 15;
        this.wiresGivePower = true;
        return i;
    }

}
