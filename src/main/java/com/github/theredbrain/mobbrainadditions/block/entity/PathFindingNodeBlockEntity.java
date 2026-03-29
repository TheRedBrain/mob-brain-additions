package com.github.theredbrain.mobbrainadditions.block.entity;

import com.github.theredbrain.mobbrainadditions.block.PathFindingNodeBlock;
import com.github.theredbrain.mobbrainadditions.registry.EntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathFindingNodeBlockEntity extends BlockEntity {
	private int rotated = 0;
	private boolean x_mirrored = false;
	private boolean z_mirrored = false;
	private HashMap<String, BlockPos> nodes = new HashMap<>(Map.of());

	public PathFindingNodeBlockEntity(BlockPos pos, BlockState state) {
		super(EntityRegistry.PATH_FINDING_NODE_BLOCK_ENTITY, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

		List<String> keyList = this.nodes.keySet().stream().toList();
		int nodesSize = this.nodes.keySet().size();
		nbt.putInt("nodes_size", nodesSize);
		for (int i = 0; i < nodesSize; i++) {
			String key = keyList.get(i);
			nbt.putString("key_" + i, key);
			nbt.putInt("node_" + i + "_x", this.nodes.get(key).getX());
			nbt.putInt("node_" + i + "_y", this.nodes.get(key).getY());
			nbt.putInt("node_" + i + "_z", this.nodes.get(key).getZ());
		}

		nbt.putInt("rotated", this.rotated);
		nbt.putBoolean("x_mirrored", this.x_mirrored);
		nbt.putBoolean("z_mirrored", this.z_mirrored);

		this.rotated = MathHelper.clamp(nbt.getInt("rotated"), 0, 3);
		this.x_mirrored = nbt.getBoolean("x_mirrored");
		this.z_mirrored = nbt.getBoolean("z_mirrored");
		if (this.getCachedState().getBlock() instanceof PathFindingNodeBlock) {
			this.onRotate(this.getCachedState());
		}

		super.writeNbt(nbt, registryLookup);
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

		int nodes_size = nbt.getInt("nodes_size");
		this.nodes = new HashMap<>(Map.of());
		for (int i = 0; i < nodes_size; i++) {
			String key = nbt.getString("key_" + i);
			this.nodes.put(key, new BlockPos(
					nbt.getInt("node_" + i + "_x"),
					nbt.getInt("node_" + i + "_y"),
					nbt.getInt("node_" + i + "_z")
			));
		}

		super.readNbt(nbt, registryLookup);
	}

	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		return this.createComponentlessNbt(registryLookup);
	}

	@Nullable
	public BlockPos getNode(String id) {
		BlockPos nodePos = this.nodes.get(id);
		if (nodePos != null) {
			return new BlockPos(this.pos.add(nodePos.getX(), nodePos.getY(), nodePos.getZ()));
		}
		return null;
	}

	// region --- getter & setter ---
	public HashMap<String, BlockPos> getNodes() {
		return this.nodes;
	}

	public void setNodes(HashMap<String, BlockPos> nodes) {
		this.nodes.clear();
		this.nodes.putAll(nodes);
	}
	// endregion --- getter & setter ---

	protected void onRotate(BlockState state) {
		if (state.getBlock() instanceof PathFindingNodeBlock) {
			if (state.get(PathFindingNodeBlock.ROTATED) != this.rotated) {
				BlockRotation blockRotation = PathFindingNodeBlock.calculateRotationFromDifferentRotatedStates(state.get(PathFindingNodeBlock.ROTATED), this.rotated);

				List<String> keyList = this.nodes.keySet().stream().toList();
				int nodesSize = this.nodes.keySet().size();
				for (int i = 0; i < nodesSize; i++) {
					String key = keyList.get(i);
					BlockPos rotatedBlockPos = PathFindingNodeBlock.rotateOffsetBlockPos(this.nodes.get(key), blockRotation);
					this.nodes.put(key, rotatedBlockPos);
				}

				this.rotated = state.get(PathFindingNodeBlock.ROTATED);
			}
			if (state.get(PathFindingNodeBlock.X_MIRRORED) != this.x_mirrored) {

				List<String> keyList = this.nodes.keySet().stream().toList();
				int nodesSize = this.nodes.keySet().size();
				for (int i = 0; i < nodesSize; i++) {
					String key = keyList.get(i);
					BlockPos mirroredBlockPos = PathFindingNodeBlock.mirrorOffsetBlockPos(this.nodes.get(key), BlockMirror.FRONT_BACK);
					this.nodes.put(key, mirroredBlockPos);
				}

				this.x_mirrored = state.get(PathFindingNodeBlock.X_MIRRORED);
			}
			if (state.get(PathFindingNodeBlock.Z_MIRRORED) != this.z_mirrored) {

				List<String> keyList = this.nodes.keySet().stream().toList();
				int nodesSize = this.nodes.keySet().size();
				for (int i = 0; i < nodesSize; i++) {
					String key = keyList.get(i);
					BlockPos mirroredBlockPos = PathFindingNodeBlock.mirrorOffsetBlockPos(this.nodes.get(key), BlockMirror.LEFT_RIGHT);
					this.nodes.put(key, mirroredBlockPos);
				}

				this.z_mirrored = state.get(PathFindingNodeBlock.Z_MIRRORED);
			}
		}
	}
}
