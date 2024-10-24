package me.zombii.improved_redstone;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.function.Function;

public class ImprovedItems {

    public static Item IMPROVED_REDSTONE_REPEATER;
    public static Item IMPROVED_REDSTONE_COMPARATOR;

    public static Item IMPROVED_REDSTONE_TORCH;

    public static Item IMPROVED_REDSTONE_LAMP;
    public static Item IMPROVED_OBSERVER;

    public static Item IMPROVED_REDSTONE;
    public static Item IMPROVED_REDSTONE_BLOCK;

    public static Item RGB_PANEL;

    private static Function<Item.Settings, Item> createBlockItemWithUniqueName(Block block) {
        return (settings) -> new BlockItem(block, settings.useItemPrefixedTranslationKey());
    }

    public static void registerItems() {
        ImprovedItems.IMPROVED_REDSTONE_REPEATER = Items.register(ImprovedBlocks.IMPROVED_REDSTONE_REPEATER);
        ImprovedItems.IMPROVED_REDSTONE_COMPARATOR = Items.register(ImprovedBlocks.IMPROVED_REDSTONE_COMPARATOR);
        ImprovedItems.IMPROVED_REDSTONE_LAMP = Items.register(ImprovedBlocks.IMPROVED_REDSTONE_LAMP);
        ImprovedItems.IMPROVED_REDSTONE_TORCH = Items.register(
                ImprovedBlocks.IMPROVED_REDSTONE_TORCH,
                (block, itemSettings) -> new VerticallyAttachableBlockItem(
                    block,
                    ImprovedBlocks.IMPROVED_REDSTONE_WALL_TORCH,
                    Direction.DOWN,
                    itemSettings
            )
        );
        ImprovedItems.IMPROVED_OBSERVER = Items.register(ImprovedBlocks.IMPROVED_OBSERVER);

        ImprovedItems.IMPROVED_REDSTONE = Items.register(
                RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ImprovedRedstone.MOD_ID, "improved_redstone")),
                createBlockItemWithUniqueName(ImprovedBlocks.IMPROVED_REDSTONE)
        );
        ImprovedItems.IMPROVED_REDSTONE_BLOCK = Items.register(ImprovedBlocks.IMPROVED_REDSTONE_BLOCK);

        ImprovedItems.RGB_PANEL = Items.register(ImprovedBlocks.RGB_PANEL);
    }

}
