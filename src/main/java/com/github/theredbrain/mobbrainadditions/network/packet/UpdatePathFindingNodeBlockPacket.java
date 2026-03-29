package com.github.theredbrain.mobbrainadditions.network.packet;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.List;

public record UpdatePathFindingNodeBlockPacket(
		BlockPos pathFindingNodeBlockPosition,
		List<MutablePair<String, BlockPos>> nodesList
) implements CustomPayload {
	public static final Id<UpdatePathFindingNodeBlockPacket> PACKET_ID = new Id<>(MobBrainAdditions.identifier("update_path_finding_node_block"));
	public static final PacketCodec<RegistryByteBuf, UpdatePathFindingNodeBlockPacket> PACKET_CODEC = PacketCodec.of(UpdatePathFindingNodeBlockPacket::write, UpdatePathFindingNodeBlockPacket::new);

	public UpdatePathFindingNodeBlockPacket(RegistryByteBuf registryByteBuf) {
		this(
				registryByteBuf.readBlockPos(),
				registryByteBuf.readList(MUTABLE_PAIR_STRING_BLOCK_POS)
		);
	}

	private void write(RegistryByteBuf registryByteBuf) {
		registryByteBuf.writeBlockPos(this.pathFindingNodeBlockPosition);
		registryByteBuf.writeCollection(this.nodesList, MUTABLE_PAIR_STRING_BLOCK_POS);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return PACKET_ID;
	}

	public static final PacketCodec<ByteBuf, MutablePair<String, BlockPos>> MUTABLE_PAIR_STRING_BLOCK_POS = new PacketCodec<>() {
		public MutablePair<String, BlockPos> decode(ByteBuf byteBuf) {
			return new MutablePair<>(PacketCodecs.STRING.decode(byteBuf), BlockPos.PACKET_CODEC.decode(byteBuf));
		}

		public void encode(ByteBuf byteBuf, MutablePair<String, BlockPos> pairStringBlockPos) {
			PacketCodecs.STRING.encode(byteBuf, pairStringBlockPos.getLeft());
			BlockPos.PACKET_CODEC.encode(byteBuf, pairStringBlockPos.getRight());
		}
	};

}
