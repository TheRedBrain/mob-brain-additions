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

public record UpdatePathFindingBranchingNodeBlockPacket(
		BlockPos pathFindingBranchingNodeBlockPosition,
		String nodeId,
		List<MutablePair<Integer, BlockPos>> branchList
) implements CustomPayload {
	public static final Id<UpdatePathFindingBranchingNodeBlockPacket> PACKET_ID = new Id<>(MobBrainAdditions.identifier("update_path_finding_branching_node_block"));
	public static final PacketCodec<RegistryByteBuf, UpdatePathFindingBranchingNodeBlockPacket> PACKET_CODEC = PacketCodec.of(UpdatePathFindingBranchingNodeBlockPacket::write, UpdatePathFindingBranchingNodeBlockPacket::new);

	public UpdatePathFindingBranchingNodeBlockPacket(RegistryByteBuf registryByteBuf) {
		this(
				registryByteBuf.readBlockPos(),
				registryByteBuf.readString(),
				registryByteBuf.readList(MUTABLE_PAIR_INTEGER_BLOCK_POS)
		);
	}

	private void write(RegistryByteBuf registryByteBuf) {
		registryByteBuf.writeBlockPos(this.pathFindingBranchingNodeBlockPosition);
		registryByteBuf.writeString(this.nodeId);
		registryByteBuf.writeCollection(this.branchList, MUTABLE_PAIR_INTEGER_BLOCK_POS);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return PACKET_ID;
	}

	public static final PacketCodec<ByteBuf, MutablePair<Integer, BlockPos>> MUTABLE_PAIR_INTEGER_BLOCK_POS = new PacketCodec<>() {
		public MutablePair<Integer, BlockPos> decode(ByteBuf byteBuf) {
			return new MutablePair<>(PacketCodecs.INTEGER.decode(byteBuf), BlockPos.PACKET_CODEC.decode(byteBuf));
		}

		public void encode(ByteBuf byteBuf, MutablePair<Integer, BlockPos> pairStringBlockPos) {
			PacketCodecs.INTEGER.encode(byteBuf, pairStringBlockPos.getLeft());
			BlockPos.PACKET_CODEC.encode(byteBuf, pairStringBlockPos.getRight());
		}
	};

}
