package com.github.theredbrain.mobbrainadditions.registry;

import com.github.theredbrain.mobbrainadditions.network.packet.UpdatePathFindingBranchingNodeBlockPacket;
import com.github.theredbrain.mobbrainadditions.network.packet.UpdatePathFindingBranchingNodeBlockPacketReceiver;
import com.github.theredbrain.mobbrainadditions.network.packet.UpdatePathFindingEndNodeBlockPacket;
import com.github.theredbrain.mobbrainadditions.network.packet.UpdatePathFindingEndNodeBlockPacketReceiver;
import com.github.theredbrain.mobbrainadditions.network.packet.UpdatePathFindingNodeBlockPacket;
import com.github.theredbrain.mobbrainadditions.network.packet.UpdatePathFindingNodeBlockPacketReceiver;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerPacketRegistry {

	public static void init() {

		PayloadTypeRegistry.playC2S().register(UpdatePathFindingBranchingNodeBlockPacket.PACKET_ID, UpdatePathFindingBranchingNodeBlockPacket.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(UpdatePathFindingBranchingNodeBlockPacket.PACKET_ID, new UpdatePathFindingBranchingNodeBlockPacketReceiver());

		PayloadTypeRegistry.playC2S().register(UpdatePathFindingEndNodeBlockPacket.PACKET_ID, UpdatePathFindingEndNodeBlockPacket.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(UpdatePathFindingEndNodeBlockPacket.PACKET_ID, new UpdatePathFindingEndNodeBlockPacketReceiver());

		PayloadTypeRegistry.playC2S().register(UpdatePathFindingNodeBlockPacket.PACKET_ID, UpdatePathFindingNodeBlockPacket.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(UpdatePathFindingNodeBlockPacket.PACKET_ID, new UpdatePathFindingNodeBlockPacketReceiver());

	}
}
