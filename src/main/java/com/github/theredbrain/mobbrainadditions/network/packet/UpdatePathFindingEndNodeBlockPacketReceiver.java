package com.github.theredbrain.mobbrainadditions.network.packet;

import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingEndNodeBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.HashMap;
import java.util.List;

public class UpdatePathFindingEndNodeBlockPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<UpdatePathFindingEndNodeBlockPacket> {

	@Override
	public void receive(UpdatePathFindingEndNodeBlockPacket payload, ServerPlayNetworking.Context context) {

		ServerPlayerEntity serverPlayerEntity = context.player();

		if (!serverPlayerEntity.isCreativeLevelTwoOp()) {
			serverPlayerEntity.sendMessage(Text.translatable("hud.message.node_block.no_permission"), true);
			return;
		}

		BlockPos pathFindingEndNodeBlockPosition = payload.pathFindingEndNodeBlockPosition();

		boolean useScriptBlocksMode = payload.useScriptBlocksMode();

		List<MutablePair<String, MutablePair<BlockPos, Boolean>>> triggeredBlocksList = payload.triggeredBlocksList();
		HashMap<String, MutablePair<BlockPos, Boolean>> triggeredBlocks = new HashMap<>();
		for (MutablePair<String, MutablePair<BlockPos, Boolean>> triggeredBlock : triggeredBlocksList) {
			triggeredBlocks.put(triggeredBlock.getLeft(), triggeredBlock.getRight());
		}

		World world = serverPlayerEntity.getWorld();

		BlockEntity blockEntity = world.getBlockEntity(pathFindingEndNodeBlockPosition);
		BlockState blockState = world.getBlockState(pathFindingEndNodeBlockPosition);

		if (blockEntity instanceof PathFindingEndNodeBlockEntity pathFindingEndNodeBlockEntity) {

			pathFindingEndNodeBlockEntity.setUseScriptBlocksMode(useScriptBlocksMode);
			pathFindingEndNodeBlockEntity.setTriggeredBlocks(triggeredBlocks);
			serverPlayerEntity.sendMessage(Text.translatable("hud.message.node_block.update_successful", blockState.getBlock().getName()), true);
			pathFindingEndNodeBlockEntity.markDirty();
			world.updateListeners(pathFindingEndNodeBlockPosition, blockState, blockState, Block.NOTIFY_ALL);
		}

	}
}
