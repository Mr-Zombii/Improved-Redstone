package me.zombii.improved_redstone;

import me.zombii.improved_redstone.blocks.ImprovedRedstoneWireBlock;
import me.zombii.improved_redstone.blocks.RGBPanel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.render.RenderLayer;

import static me.zombii.improved_redstone.ImprovedBlocks.IMPROVED_REDSTONE;
import static me.zombii.improved_redstone.ImprovedBlocks.RGB_PANEL;

public class ImprovedRedstoneClient implements ClientModInitializer {

	public void onInitializeClient() {
		setupRenderlayers();
		setupColorProviders();
	}

	void setupRenderlayers() {
		RenderLayer layer = RenderLayer.getCutout();
		BlockRenderLayerMap.INSTANCE.putBlock(ImprovedBlocks.IMPROVED_REDSTONE_COMPARATOR, layer);
		BlockRenderLayerMap.INSTANCE.putBlock(ImprovedBlocks.IMPROVED_REDSTONE_WALL_TORCH, layer);
		BlockRenderLayerMap.INSTANCE.putBlock(ImprovedBlocks.IMPROVED_REDSTONE_REPEATER, layer);
		BlockRenderLayerMap.INSTANCE.putBlock(ImprovedBlocks.IMPROVED_REDSTONE_TORCH, layer);
		BlockRenderLayerMap.INSTANCE.putBlock(IMPROVED_REDSTONE, layer);
		BlockRenderLayerMap.INSTANCE.putBlock(RGB_PANEL, layer);
	}

	void setupColorProviders() {
		ColorProviderRegistry.BLOCK.register(ImprovedRedstoneWireBlock::getBlockColor, IMPROVED_REDSTONE);
		ColorProviderRegistry.BLOCK.register(RGBPanel::getBlockColor, RGB_PANEL);
	}
}