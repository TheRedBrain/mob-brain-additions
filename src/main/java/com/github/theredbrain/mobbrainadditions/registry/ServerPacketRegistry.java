package com.github.theredbrain.mobbrainadditions.registry;

import com.github.theredbrain.mobbrainadditions.network.packet.UpdatePathFindingNodeBlockPacket;
import com.github.theredbrain.mobbrainadditions.network.packet.UpdatePathFindingNodeBlockPacketReceiver;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerPacketRegistry {

	public static void init() {

		PayloadTypeRegistry.playC2S().register(UpdatePathFindingNodeBlockPacket.PACKET_ID, UpdatePathFindingNodeBlockPacket.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(UpdatePathFindingNodeBlockPacket.PACKET_ID, new UpdatePathFindingNodeBlockPacketReceiver());

	}
}
