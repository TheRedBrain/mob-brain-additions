package com.github.theredbrain.mobbrainadditions.entity.brain.behaviour;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import com.github.theredbrain.mobbrainadditions.block.PathFindingEndNodeBlock;
import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingEndNodeBlockEntity;
import com.github.theredbrain.mobbrainadditions.entity.mob.TracksPathFindingNodes;
import com.github.theredbrain.mobbrainadditions.registry.MemoryModuleTypeRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.List;

/**
 * Set the walk and look target of the entity to its current home position.
 *
 * @param <E> The entity
 */
public class ReactToNearbyEndOfPath<E extends MobEntity> extends ExtendedBehaviour<E> {
	private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).hasMemory(MemoryModuleTypeRegistry.PATH_END_POSITION);

	private boolean discardEntity = false;

	/**
	 * Whether the entity should be discarded when reaching the end of a path.
	 *
	 * @return this
	 */
	public ReactToNearbyEndOfPath<E> discardEntity(boolean discardEntity) {
		this.discardEntity = discardEntity;

		return this;
	}

	@Override
	protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
		return MEMORY_REQUIREMENTS;
	}

	@Override
	protected void start(E entity) {
		GlobalPos pathEndGlobalPosition = BrainUtils.getMemory(entity, MemoryModuleTypeRegistry.PATH_END_POSITION);
		World world = entity.getEntityWorld();

		if (world.isClient() || pathEndGlobalPosition == null) {
			return;
		}

		BlockEntity blockEntity = world.getBlockEntity(pathEndGlobalPosition.pos());

		if (entity instanceof TracksPathFindingNodes tracksPathFindingNodes && blockEntity instanceof PathFindingEndNodeBlockEntity pathFindingEndNodeBlockEntity) {
			String string = tracksPathFindingNodes.getTrackedPathFindingNodeId();
			if (!string.isEmpty()) {
				MutablePair<BlockPos, Boolean> triggeredPos = pathFindingEndNodeBlockEntity.getTriggeredPos(string);
				if (triggeredPos != null) {

					if (!pathFindingEndNodeBlockEntity.useScriptBlocksMode() || (world instanceof ServerWorld serverWorld && !MobBrainAdditions.trigger(serverWorld, triggeredPos.getLeft(), triggeredPos.getRight()))) {
						BlockState blockState = world.getBlockState(pathEndGlobalPosition.pos());

						if (blockState.getBlock() instanceof PathFindingEndNodeBlock pathFindingEndNodeBlock) {
							pathFindingEndNodeBlock.trigger(world, blockState, pathEndGlobalPosition.pos());
						}
					}

					if (this.discardEntity) {
						entity.discard();
					}
				}
			}
		}
	}
}