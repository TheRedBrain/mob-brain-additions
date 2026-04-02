package com.github.theredbrain.mobbrainadditions.block.entity;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface ProvidesPathFindingNode {

	@Nullable
	default BlockPos getNode(String id) {
		return null;
	}
}
