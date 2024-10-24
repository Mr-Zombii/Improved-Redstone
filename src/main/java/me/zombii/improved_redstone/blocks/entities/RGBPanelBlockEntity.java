package me.zombii.improved_redstone.blocks.entities;

import me.zombii.improved_redstone.ImprovedBlockEntityTypes;
import me.zombii.improved_redstone.api.IRedstoneBlockEntity;
import me.zombii.improved_redstone.blocks.ImprovedRedstoneWireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;

import static me.zombii.improved_redstone.blocks.RGBPanel.LIT;

public class RGBPanelBlockEntity extends BlockEntity implements IRedstoneBlockEntity {
    private int colour;

    public RGBPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ImprovedBlockEntityTypes.RGB_PANEL, pos, state);
    }

    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("OutputSignal", this.colour);
    }

    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.colour = nbt.getInt("OutputSignal");
    }

    public static void tick(World world, BlockPos pos, BlockState state, RGBPanelBlockEntity blockEntity) {
        int power = world.getEmittedRedstonePower(pos.offset(state.get(HorizontalFacingBlock.FACING).getOpposite()), state.get(HorizontalFacingBlock.FACING).getOpposite());

        boolean isGettingPower = power > 0;

        if (state.get(LIT) && isGettingPower) {
            blockEntity.setOutputSignal(power);
        }
        if (state.get(LIT) && !isGettingPower) {
            blockEntity.setOutputSignal(power);
            world.setBlockState(pos, state.cycle(LIT), 2);
        }
    }

    @Override
    public int getOutputSignal() {
        return this.colour;
    }

    @Override
    public void setOutputSignal(int colour) {
        this.colour = colour;
        toUpdatePacket();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
