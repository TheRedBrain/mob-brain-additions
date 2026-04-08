package com.github.theredbrain.mobbrainadditions.compat;

import com.github.theredbrain.scriptblocks.ScriptBlocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class ScriptBlocksCompat {

	public static void trigger(ServerWorld serverWorld, BlockPos blockPos, boolean resets) {
		ScriptBlocks.trigger(serverWorld, blockPos, resets);
	}
}
