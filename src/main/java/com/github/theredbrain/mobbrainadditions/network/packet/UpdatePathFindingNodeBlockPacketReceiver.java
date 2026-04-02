package com.github.theredbrain.mobbrainadditions.network.packet;

import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingNodeBlockEntity;
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

public class UpdatePathFindingNodeBlockPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<UpdatePathFindingNodeBlockPacket> {

	@Override
	public void receive(UpdatePathFindingNodeBlockPacket payload, ServerPlayNetworking.Context context) {

		ServerPlayerEntity serverPlayerEntity = context.player();

		if (!serverPlayerEntity.isCreativeLevelTwoOp()) {
			serverPlayerEntity.sendMessage(Text.translatable("hud.message.node_block.no_permission"), true);
			return;
		}

		BlockPos pathFindingNodeBlockPosition = payload.pathFindingNodeBlockPosition();

		List<MutablePair<String, BlockPos>> nodesList = payload.nodesList();
		HashMap<String, BlockPos> nodes = new HashMap<>();
		for (MutablePair<String, BlockPos> node : nodesList) {
			nodes.put(node.getLeft(), node.getRight());
		}

		World world = serverPlayerEntity.getWorld();

		BlockEntity blockEntity = world.getBlockEntity(pathFindingNodeBlockPosition);
		BlockState blockState = world.getBlockState(pathFindingNodeBlockPosition);

		if (blockEntity instanceof PathFindingNodeBlockEntity pathFindingNodeBlockEntity) {

			pathFindingNodeBlockEntity.setNodes(nodes);
			serverPlayerEntity.sendMessage(Text.translatable("hud.message.node_block.update_successful", blockState.getBlock().getName()), true);
			pathFindingNodeBlockEntity.markDirty();
			world.updateListeners(pathFindingNodeBlockPosition, blockState, blockState, Block.NOTIFY_ALL);
		}

	}
}
