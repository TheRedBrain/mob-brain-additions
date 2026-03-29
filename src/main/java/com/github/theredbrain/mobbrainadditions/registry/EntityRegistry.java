package com.github.theredbrain.mobbrainadditions.registry;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingNodeBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EntityRegistry {

	public static final BlockEntityType<PathFindingNodeBlockEntity> PATH_FINDING_NODE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
			MobBrainAdditions.identifier("path_finding_node_block"),
			BlockEntityType.Builder.create(PathFindingNodeBlockEntity::new, BlockRegistry.PATH_FINDING_NODE_BLOCK).build());

	public static void init() {
	}

}
