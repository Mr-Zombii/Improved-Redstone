package me.zombii.improved_redstone;

import net.fabricmc.api.ModInitializer;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImprovedRedstone implements ModInitializer {

	public static final String MOD_ID = "improved_redstone";
    public static final Logger LOGGER = LoggerFactory.getLogger("improved-redstone");

	@Override
	public void onInitialize() {
		ImprovedBlocks.registerBlocks();
		ImprovedItems.registerItems();

		ItemGroup.Builder builder = ItemGroup.create(ItemGroup.Row.TOP, 5);
		builder.displayName(Text.of("Improved Redstone"));
		builder.icon(() -> new ItemStack(ImprovedItems.IMPROVED_REDSTONE));
		builder.entries((displayContext, entries) -> {
			entries.add(ImprovedItems.IMPROVED_REDSTONE);
			entries.add(ImprovedItems.IMPROVED_REDSTONE_BLOCK);
			entries.add(ImprovedItems.IMPROVED_REDSTONE_TORCH);
			entries.add(ImprovedItems.IMPROVED_REDSTONE_COMPARATOR);
			entries.add(ImprovedItems.IMPROVED_REDSTONE_REPEATER);
			entries.add(ImprovedItems.IMPROVED_REDSTONE_LAMP);
			entries.add(ImprovedItems.IMPROVED_OBSERVER);
			entries.add(ImprovedItems.RGB_PANEL);
		});
		Registry.register(Registries.ITEM_GROUP, "improved_redstone", builder.build());
	}
}