package com.github.theredbrain.mobbrainadditions.entity.player;

import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingBranchingNodeBlockEntity;
import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingEndNodeBlockEntity;
import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingNodeBlockEntity;

public interface DuckPlayerEntityMixin {

	default void scriptblocks$openPathFindingNodeBlockScreen(PathFindingNodeBlockEntity pathFindingNodeBlockEntity) {
	}

	default void scriptblocks$openPathFindingBranchingNodeBlockScreen(PathFindingBranchingNodeBlockEntity pathFindingBranchingNodeBlockEntity) {
	}

	default void scriptblocks$openPathFindingEndNodeBlockScreen(PathFindingEndNodeBlockEntity pathFindingEndNodeBlockEntity) {
	}

}
