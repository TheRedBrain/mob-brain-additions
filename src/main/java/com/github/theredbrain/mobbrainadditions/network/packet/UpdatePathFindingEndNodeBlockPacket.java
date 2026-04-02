package com.github.theredbrain.mobbrainadditions.network.packet;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public record UpdatePathFindingEndNodeBlockPacket(
		BlockPos pathFindingEndNodeBlockPosition,
		List<String> nodeIdsList
) implements CustomPayload {
	public static final Id<UpdatePathFindingEndNodeBlockPacket> PACKET_ID = new Id<>(MobBrainAdditions.identifier("update_path_finding_end_node_block"));
	public static final PacketCodec<RegistryByteBuf, UpdatePathFindingEndNodeBlockPacket> PACKET_CODEC = PacketCodec.of(UpdatePathFindingEndNodeBlockPacket::write, UpdatePathFindingEndNodeBlockPacket::new);

	public UpdatePathFindingEndNodeBlockPacket(RegistryByteBuf registryByteBuf) {
		this(
				registryByteBuf.readBlockPos(),
				registryByteBuf.readList(PacketCodecs.STRING)
		);
	}

	private void write(RegistryByteBuf registryByteBuf) {
		registryByteBuf.writeBlockPos(this.pathFindingEndNodeBlockPosition);
		registryByteBuf.writeCollection(this.nodeIdsList, PacketCodecs.STRING);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return PACKET_ID;
	}

}
