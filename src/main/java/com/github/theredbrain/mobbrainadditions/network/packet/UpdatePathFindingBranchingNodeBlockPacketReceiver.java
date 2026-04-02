package com.github.theredbrain.mobbrainadditions.network.packet;

import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingBranchingNodeBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.List;

public class UpdatePathFindingBranchingNodeBlockPacketReceiver implements ServerPlayNetworking.PlayPayloadHandler<UpdatePathFindingBranchingNodeBlockPacket> {

	@Override
	public void receive(UpdatePathFindingBranchingNodeBlockPacket payload, ServerPlayNetworking.Context context) {

		ServerPlayerEntity serverPlayerEntity = context.player();

		if (!serverPlayerEntity.isCreativeLevelTwoOp()) {
			serverPlayerEntity.sendMessage(Text.translatable("hud.message.node_block.no_permission"), true);
			return;
		}

		BlockPos pathFindingBranchingNodeBlockPosition = payload.pathFindingBranchingNodeBlockPosition();

		List<MutablePair<Integer, BlockPos>> branchList = payload.branchList();

		String nodeId = payload.nodeId();

		World world = serverPlayerEntity.getWorld();

		BlockEntity blockEntity = world.getBlockEntity(pathFindingBranchingNodeBlockPosition);
		BlockState blockState = world.getBlockState(pathFindingBranchingNodeBlockPosition);

		if (blockEntity instanceof PathFindingBranchingNodeBlockEntity pathFindingBranchingNodeBlockEntity) {

			pathFindingBranchingNodeBlockEntity.setNodeId(nodeId);
			pathFindingBranchingNodeBlockEntity.setBranches(branchList);
			serverPlayerEntity.sendMessage(Text.translatable("hud.message.node_block.update_successful", blockState.getBlock().getName()), true);
			pathFindingBranchingNodeBlockEntity.markDirty();
			world.updateListeners(pathFindingBranchingNodeBlockPosition, blockState, blockState, Block.NOTIFY_ALL);
		}

	}
}
