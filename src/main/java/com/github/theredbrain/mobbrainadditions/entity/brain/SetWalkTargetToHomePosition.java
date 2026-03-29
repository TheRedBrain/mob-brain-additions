package com.github.theredbrain.mobbrainadditions.entity.brain;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Set the walk target of the entity to its current attack target.
 * @param <E> The entity
 */
public class SetWalkTargetToHomePosition<E extends MobEntity> extends ExtendedBehaviour<E> {
	private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(3).hasMemory(MemoryModuleType.HOME).usesMemories(MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET);

	protected BiFunction<E, BlockPos, Float> speedMod = (owner, pos) -> 1f;
	protected BiFunction<E, BlockPos, Integer> closeEnoughDist = (entity, pos) -> 2;

	protected BlockPos target = null;

	/**
	 * Set the movespeed modifier for the entity when moving to the target.
	 *
	 * @param speedModifier The movespeed modifier/multiplier
	 * @return this
	 */
	public SetWalkTargetToHomePosition<E> speedMod(BiFunction<E, BlockPos, Float> speedModifier) {
		this.speedMod = speedModifier;

		return this;
	}

	/**
	 * Set the distance (in blocks) that is 'close enough' for the entity to be considered at the target position
	 *
	 * @param function The function
	 * @return this
	 */
	public SetWalkTargetToHomePosition<E> closeEnoughWhen(final BiFunction<E, BlockPos, Integer> function) {
		this.closeEnoughDist = function;

		return this;
	}

	@Override
	protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
		return MEMORY_REQUIREMENTS;
	}

	@Override
	protected boolean shouldRun(ServerWorld level, E entity) {
		GlobalPos homePos = BrainUtils.getMemory(entity, MemoryModuleType.HOME);
		if (homePos != null) {
			this.target = homePos.pos();
		}

		return this.target != null;
	}

	@Override
	protected void start(E entity) {
		BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(this.target, this.speedMod.apply(entity, this.target), this.closeEnoughDist.apply(entity, this.target)));
		BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.target));
	}

	@Override
	protected void stop(E entity) {
		this.target = null;
	}
}