package com.github.theredbrain.mobbrainadditions.block.entity;

import com.github.theredbrain.mobbrainadditions.registry.EntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class PathFindingEndNodeBlockEntity extends BlockEntity {
	private final List<String> nodeIds = new ArrayList<>(List.of());

	public PathFindingEndNodeBlockEntity(BlockPos pos, BlockState state) {
		super(EntityRegistry.PATH_FINDING_END_NODE_BLOCK_ENTITY, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

		super.writeNbt(nbt, registryLookup);

		int nodeIdsSize = this.nodeIds.size();
		nbt.putInt("node_ids_size", nodeIdsSize);
		for (int i = 0; i < nodeIdsSize; i++) {
			nbt.putString("node_id_" + i, nodeIds.get(i));
		}

	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

		super.readNbt(nbt, registryLookup);

		this.nodeIds.clear();
		int nodeIdsSize = nbt.getInt("node_ids_size");
		for (int i = 0; i < nodeIdsSize; i++) {
			this.nodeIds.add(nbt.getString("node_id_" + i));
		}

	}

	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		return this.createComponentlessNbt(registryLookup);
	}

	public boolean hasNodeId(String id) {
		return this.world != null && this.nodeIds.contains(id);
	}

	// region --- getter & setter ---
	public List<String> getNodeIds() {
		return this.nodeIds;
	}

	public void setNodeIds(List<String> nodeIds) {
		this.nodeIds.clear();
		this.nodeIds.addAll(nodeIds);
	}
	// endregion --- getter & setter ---

}
