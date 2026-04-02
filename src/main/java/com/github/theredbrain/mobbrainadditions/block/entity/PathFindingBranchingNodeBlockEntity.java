package com.github.theredbrain.mobbrainadditions.block.entity;

import com.github.theredbrain.mobbrainadditions.block.PathFindingNodeBlock;
import com.github.theredbrain.mobbrainadditions.registry.EntityRegistry;
import com.github.theredbrain.mobbrainadditions.util.RotationUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PathFindingBranchingNodeBlockEntity extends BlockEntity implements ProvidesPathFindingNode {
	private int rotated = 0;
	private boolean x_mirrored = false;
	private boolean z_mirrored = false;
	private String nodeId = "";
	private List<MutablePair<Integer, BlockPos>> branches = new ArrayList<>(List.of());

	public PathFindingBranchingNodeBlockEntity(BlockPos pos, BlockState state) {
		super(EntityRegistry.PATH_FINDING_BRANCHING_NODE_BLOCK_ENTITY, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

		nbt.putString("node_id", this.nodeId);

		int nodesSize = this.branches.size();
		nbt.putInt("branches_size", nodesSize);
		for (int i = 0; i < nodesSize; i++) {
			MutablePair<Integer, BlockPos> pair = branches.get(i);
			nbt.putInt("branch_" + i + "_weight", pair.getLeft());
			nbt.putInt("branch_" + i + "_x", pair.getRight().getX());
			nbt.putInt("branch_" + i + "_y", pair.getRight().getY());
			nbt.putInt("branch_" + i + "_z", pair.getRight().getZ());
		}

		nbt.putInt("rotated", this.rotated);
		nbt.putBoolean("x_mirrored", this.x_mirrored);
		nbt.putBoolean("z_mirrored", this.z_mirrored);

		super.writeNbt(nbt, registryLookup);
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

		this.nodeId = nbt.getString("node_id");

		int branches_size = nbt.getInt("branches_size");
		this.branches = new ArrayList<>(List.of());
		for (int i = 0; i < branches_size; i++) {
			this.branches.add(new MutablePair<>(
					nbt.getInt("branch_" + i + "_weight"),
					new BlockPos(
							nbt.getInt("branch_" + i + "_x"),
							nbt.getInt("branch_" + i + "_y"),
							nbt.getInt("branch_" + i + "_z")
					)));
		}

		this.rotated = MathHelper.clamp(nbt.getInt("rotated"), 0, 3);
		this.x_mirrored = nbt.getBoolean("x_mirrored");
		this.z_mirrored = nbt.getBoolean("z_mirrored");
		if (this.getCachedState().getBlock() instanceof PathFindingNodeBlock) {
			this.onRotate(this.getCachedState());
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
		if (this.world != null && id.equals(this.nodeId) && !this.branches.isEmpty()) {

			int totalWeight = 0;
			for (MutablePair<Integer, BlockPos> node : this.branches) {
				totalWeight += node.getLeft();
			}
			if (totalWeight > 0) {
					int pickedChoice = this.world.random.nextInt(totalWeight);
					for (MutablePair<Integer, BlockPos> node : this.branches) {
						pickedChoice -= node.getLeft();
						if (pickedChoice <= 0) {
							return node.getRight();
						}
					}
			}
		}
		return null;
	}

	// region --- getter & setter ---
	public String getNodeId() {
		return this.nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public List<MutablePair<Integer, BlockPos>> getBranches() {
		return this.branches;
	}

	public void setBranches(List<MutablePair<Integer, BlockPos>> branches) {
		this.branches.clear();
		this.branches.addAll(branches);
	}
	// endregion --- getter & setter ---

	protected void onRotate(BlockState state) {
		if (state.getBlock() instanceof PathFindingNodeBlock) {
			if (state.get(PathFindingNodeBlock.ROTATED) != this.rotated) {
				BlockRotation blockRotation = RotationUtils.calculateRotationFromDifferentRotatedStates(state.get(PathFindingNodeBlock.ROTATED), this.rotated);

				List<MutablePair<Integer, BlockPos>> newBranchNodes = new ArrayList<>(List.of());
				for (MutablePair<Integer, BlockPos> branch : this.branches) {
					newBranchNodes.add(new MutablePair<>(branch.getLeft(), RotationUtils.rotateOffsetBlockPos(branch.getRight(), blockRotation)));
				}
				this.branches = newBranchNodes;

				this.rotated = state.get(PathFindingNodeBlock.ROTATED);
			}
			if (state.get(PathFindingNodeBlock.X_MIRRORED) != this.x_mirrored) {

				List<MutablePair<Integer, BlockPos>> newBranchNodes = new ArrayList<>(List.of());
				for (MutablePair<Integer, BlockPos> branch : this.branches) {
					newBranchNodes.add(new MutablePair<>(branch.getLeft(), RotationUtils.mirrorOffsetBlockPos(branch.getRight(), BlockMirror.FRONT_BACK)));
				}
				this.branches = newBranchNodes;

				this.x_mirrored = state.get(PathFindingNodeBlock.X_MIRRORED);
			}
			if (state.get(PathFindingNodeBlock.Z_MIRRORED) != this.z_mirrored) {

				List<MutablePair<Integer, BlockPos>> newBranchNodes = new ArrayList<>(List.of());
				for (MutablePair<Integer, BlockPos> branch : this.branches) {
					newBranchNodes.add(new MutablePair<>(branch.getLeft(), RotationUtils.mirrorOffsetBlockPos(branch.getRight(), BlockMirror.LEFT_RIGHT)));
				}
				this.branches = newBranchNodes;

				this.z_mirrored = state.get(PathFindingNodeBlock.Z_MIRRORED);
			}
		}
	}
}
