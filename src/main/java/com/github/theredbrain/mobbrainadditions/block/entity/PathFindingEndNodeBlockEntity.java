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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathFindingEndNodeBlockEntity extends BlockEntity {
	private int rotated = 0;
	private boolean x_mirrored = false;
	private boolean z_mirrored = false;
	private final HashMap<String, MutablePair<BlockPos, Boolean>> triggeredBlocks = new HashMap<>(Map.of());

	private boolean useScriptBlocksMode = true;

	public PathFindingEndNodeBlockEntity(BlockPos pos, BlockState state) {
		super(EntityRegistry.PATH_FINDING_END_NODE_BLOCK_ENTITY, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

		super.writeNbt(nbt, registryLookup);

		nbt.putInt("rotated", this.rotated);
		nbt.putBoolean("x_mirrored", this.x_mirrored);
		nbt.putBoolean("z_mirrored", this.z_mirrored);

		nbt.putBoolean("use_script_blocks_mode", this.useScriptBlocksMode);

		List<String> keyList = this.triggeredBlocks.keySet().stream().toList();
		int triggeredBlocksSize = this.triggeredBlocks.size();
		nbt.putInt("triggered_blocks_size", triggeredBlocksSize);
		for (int i = 0; i < triggeredBlocksSize; i++) {
			String key = keyList.get(i);
			nbt.putString("key_" + i, key);
			nbt.putInt("triggered_block_" + i + "_x", this.triggeredBlocks.get(key).getLeft().getX());
			nbt.putInt("triggered_block_" + i + "_y", this.triggeredBlocks.get(key).getLeft().getY());
			nbt.putInt("triggered_block_" + i + "_z", this.triggeredBlocks.get(key).getLeft().getZ());
			nbt.putBoolean("triggered_block_" + i + "_resets", this.triggeredBlocks.get(key).getRight());
		}

	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

		super.readNbt(nbt, registryLookup);

		this.rotated = MathHelper.clamp(nbt.getInt("rotated"), 0, 3);
		this.x_mirrored = nbt.getBoolean("x_mirrored");
		this.z_mirrored = nbt.getBoolean("z_mirrored");
		if (this.getCachedState().getBlock() instanceof PathFindingNodeBlock) {
			this.onRotate(this.getCachedState());
		}

		this.useScriptBlocksMode = nbt.getBoolean("use_script_blocks_mode");

		int triggeredBlocksSize = nbt.getInt("triggered_blocks_size");
		this.triggeredBlocks.clear();
		for (int i = 0; i < triggeredBlocksSize; i++) {
			String key = nbt.getString("key_" + i);
			this.triggeredBlocks.put(key, new MutablePair<>(
					new BlockPos(
							nbt.getInt("triggered_block_" + i + "_x"),
							nbt.getInt("triggered_block_" + i + "_y"),
							nbt.getInt("triggered_block_" + i + "_z")
					),
					nbt.getBoolean("triggered_block_" + i + "_resets"))
			);
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

	@Nullable
	public MutablePair<BlockPos, Boolean> getTriggeredPos(String id) {
		MutablePair<BlockPos, Boolean> pair = this.triggeredBlocks.get(id);
		if (pair != null) {
			return new MutablePair<>(this.pos.add(pair.getLeft()), pair.getRight());
		}
		return null;
	}

	// region --- getter & setter ---
	public boolean useScriptBlocksMode() {
		return this.useScriptBlocksMode;
	}

	public void setUseScriptBlocksMode(boolean useScriptBlocksMode) {
		this.useScriptBlocksMode = useScriptBlocksMode;
	}

	public HashMap<String, MutablePair<BlockPos, Boolean>> getTriggeredBlocks() {
		return this.triggeredBlocks;
	}

	public void setTriggeredBlocks(HashMap<String, MutablePair<BlockPos, Boolean>> triggeredBlocks) {
		this.triggeredBlocks.clear();
		this.triggeredBlocks.putAll(triggeredBlocks);
	}
	// endregion --- getter & setter ---

	protected void onRotate(BlockState state) {
		if (state.getBlock() instanceof PathFindingNodeBlock) {
			if (state.get(PathFindingNodeBlock.ROTATED) != this.rotated) {
				BlockRotation blockRotation = RotationUtils.calculateRotationFromDifferentRotatedStates(state.get(PathFindingNodeBlock.ROTATED), this.rotated);

				List<String> keyList = this.triggeredBlocks.keySet().stream().toList();
				for (String key : keyList) {
					MutablePair<BlockPos, Boolean> pair = this.triggeredBlocks.get(key);
					BlockPos rotatedBlockPos = RotationUtils.rotateOffsetBlockPos(pair.getLeft(), blockRotation);
					this.triggeredBlocks.put(key, new MutablePair<>(rotatedBlockPos, pair.getRight()));
				}

				this.rotated = state.get(PathFindingNodeBlock.ROTATED);
			}
			if (state.get(PathFindingNodeBlock.X_MIRRORED) != this.x_mirrored) {

				List<String> keyList = this.triggeredBlocks.keySet().stream().toList();
				for (String key : keyList) {
					MutablePair<BlockPos, Boolean> pair = this.triggeredBlocks.get(key);
					BlockPos mirroredBlockPos = RotationUtils.mirrorOffsetBlockPos(pair.getLeft(), BlockMirror.FRONT_BACK);
					this.triggeredBlocks.put(key, new MutablePair<>(mirroredBlockPos, pair.getRight()));
				}

				this.x_mirrored = state.get(PathFindingNodeBlock.X_MIRRORED);
			}
			if (state.get(PathFindingNodeBlock.Z_MIRRORED) != this.z_mirrored) {

				List<String> keyList = this.triggeredBlocks.keySet().stream().toList();
				for (String key : keyList) {
					MutablePair<BlockPos, Boolean> pair = this.triggeredBlocks.get(key);
					BlockPos mirroredBlockPos = RotationUtils.mirrorOffsetBlockPos(pair.getLeft(), BlockMirror.LEFT_RIGHT);
					this.triggeredBlocks.put(key, new MutablePair<>(mirroredBlockPos, pair.getRight()));
				}

				this.z_mirrored = state.get(PathFindingNodeBlock.Z_MIRRORED);
			}
		}
	}

}
