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

public record UpdatePathFindingEndNodeBlockPacket(
		BlockPos pathFindingEndNodeBlockPosition,
		boolean useScriptBlocksMode,
		List<MutablePair<String, MutablePair<BlockPos, Boolean>>> triggeredBlocksList
) implements CustomPayload {
	public static final Id<UpdatePathFindingEndNodeBlockPacket> PACKET_ID = new Id<>(MobBrainAdditions.identifier("update_path_finding_end_node_block"));
	public static final PacketCodec<RegistryByteBuf, UpdatePathFindingEndNodeBlockPacket> PACKET_CODEC = PacketCodec.of(UpdatePathFindingEndNodeBlockPacket::write, UpdatePathFindingEndNodeBlockPacket::new);

	public UpdatePathFindingEndNodeBlockPacket(RegistryByteBuf registryByteBuf) {
		this(
				registryByteBuf.readBlockPos(),
				registryByteBuf.readBoolean(),
				registryByteBuf.readList(MUTABLE_PAIR_STRING_MUTABLE_PAIR_BLOCK_POS_BOOL)
		);
	}

	private void write(RegistryByteBuf registryByteBuf) {
		registryByteBuf.writeBlockPos(this.pathFindingEndNodeBlockPosition);
		registryByteBuf.writeBoolean(this.useScriptBlocksMode);
		registryByteBuf.writeCollection(this.triggeredBlocksList, MUTABLE_PAIR_STRING_MUTABLE_PAIR_BLOCK_POS_BOOL);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return PACKET_ID;
	}

	public static final PacketCodec<ByteBuf, MutablePair<String, MutablePair<BlockPos, Boolean>>> MUTABLE_PAIR_STRING_MUTABLE_PAIR_BLOCK_POS_BOOL = new PacketCodec<>() {
		public MutablePair<String, MutablePair<BlockPos, Boolean>> decode(ByteBuf byteBuf) {
//			String string = PacketCodecs.STRING.decode(byteBuf);
//			BlockPos blockPos = BlockPos.PACKET_CODEC.decode(byteBuf);
//			boolean bool = PacketCodecs.BOOL.decode(byteBuf);
//			return new MutablePair<>(string, new MutablePair<>(blockPos, bool));
			return new MutablePair<>(PacketCodecs.STRING.decode(byteBuf), new MutablePair<>(BlockPos.PACKET_CODEC.decode(byteBuf), PacketCodecs.BOOL.decode(byteBuf)));
		}

		public void encode(ByteBuf byteBuf, MutablePair<String, MutablePair<BlockPos, Boolean>> pairStringPairBlockPosBoolean) {
			PacketCodecs.STRING.encode(byteBuf, pairStringPairBlockPosBoolean.getLeft());
			BlockPos.PACKET_CODEC.encode(byteBuf, pairStringPairBlockPosBoolean.getRight().getLeft());
			PacketCodecs.BOOL.encode(byteBuf, pairStringPairBlockPosBoolean.getRight().getRight());
		}
	};

}
