package com.github.theredbrain.mobbrainadditions.registry;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import com.github.theredbrain.mobbrainadditions.block.PathFindingBranchingNodeBlock;
import com.github.theredbrain.mobbrainadditions.block.PathFindingNodeBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class BlockRegistry {

	public static final Block PATH_FINDING_NODE_BLOCK = registerBlock("path_finding_node_block", new PathFindingNodeBlock(Block.Settings.create().mapColor(MapColor.LIGHT_GRAY).nonOpaque().requiresTool().strength(-1.0f, 3600000.0f).dropsNothing()), ItemGroups.OPERATOR);
	public static final Block VISIBLE_PATH_FINDING_NODE_BLOCK = Registry.register(Registries.BLOCK, MobBrainAdditions.identifier("visible_path_finding_node_block"), new Block(Block.Settings.create().mapColor(MapColor.LIGHT_GRAY).nonOpaque().requiresTool().strength(-1.0f, 3600000.0f).dropsNothing()));

	public static final Block PATH_FINDING_BRANCHING_NODE_BLOCK = registerBlock("path_finding_branching_node_block", new PathFindingBranchingNodeBlock(Block.Settings.create().mapColor(MapColor.LIGHT_GRAY).nonOpaque().requiresTool().strength(-1.0f, 3600000.0f).dropsNothing()), ItemGroups.OPERATOR);
	public static final Block VISIBLE_PATH_FINDING_BRANCHING_NODE_BLOCK = Registry.register(Registries.BLOCK, MobBrainAdditions.identifier("visible_path_finding_branching_node_block"), new Block(Block.Settings.create().mapColor(MapColor.LIGHT_GRAY).nonOpaque().requiresTool().strength(-1.0f, 3600000.0f).dropsNothing()));

	private static Block registerBlock(String name, Block block, RegistryKey<ItemGroup> itemGroup) {
		Registry.register(Registries.ITEM, MobBrainAdditions.identifier(name), new BlockItem(block, new Item.Settings()));
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(content -> content.add(block));
		return Registry.register(Registries.BLOCK, MobBrainAdditions.identifier(name), block);
	}

	public static void init() {
	}
}
