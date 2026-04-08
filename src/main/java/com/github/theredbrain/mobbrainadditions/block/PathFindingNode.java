package com.github.theredbrain.mobbrainadditions.block;

import com.github.theredbrain.mobbrainadditions.registry.BlockRegistry;
import net.minecraft.block.ShapeContext;

public interface PathFindingNode {
	default boolean isPathFindingNodeVisible(ShapeContext shapeContext) {
		return shapeContext.isHolding(BlockRegistry.PATH_FINDING_NODE_BLOCK.asItem()) || shapeContext.isHolding(BlockRegistry.PATH_FINDING_END_NODE_BLOCK.asItem()) || shapeContext.isHolding(BlockRegistry.PATH_FINDING_BRANCHING_NODE_BLOCK.asItem());
	}
}
