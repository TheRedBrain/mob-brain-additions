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

		List<String> nodeIdsList = payload.nodeIdsList();

		World world = serverPlayerEntity.getWorld();

		BlockEntity blockEntity = world.getBlockEntity(pathFindingEndNodeBlockPosition);
		BlockState blockState = world.getBlockState(pathFindingEndNodeBlockPosition);

		if (blockEntity instanceof PathFindingEndNodeBlockEntity pathFindingEndNodeBlockEntity) {

			pathFindingEndNodeBlockEntity.setNodeIds(nodeIdsList);
			serverPlayerEntity.sendMessage(Text.translatable("hud.message.node_block.update_successful", blockState.getBlock().getName()), true);
			pathFindingEndNodeBlockEntity.markDirty();
			world.updateListeners(pathFindingEndNodeBlockPosition, blockState, blockState, Block.NOTIFY_ALL);
		}

	}
}
