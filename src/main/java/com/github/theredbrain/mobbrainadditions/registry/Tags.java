package com.github.theredbrain.mobbrainadditions.registry;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class Tags {
	public static final TagKey<Item> PROVIDES_PATH_FINDING_NODE = TagKey.of(RegistryKeys.ITEM, MobBrainAdditions.identifier("provides_path_finding_node"));
}
