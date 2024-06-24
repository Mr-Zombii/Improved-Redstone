package me.zombii.improved_redstone.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class ImprovedRedstoneBlock extends Block {
    public static final MapCodec<ImprovedRedstoneBlock> CODEC = createCodec(ImprovedRedstoneBlock::new);

    public MapCodec<ImprovedRedstoneBlock> getCodec() {
        return CODEC;
    }

    public ImprovedRedstoneBlock(Settings settings) {
        super(settings);
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return ImprovedRedstoneWireBlock.MaxStrength - 1;
    }

}
