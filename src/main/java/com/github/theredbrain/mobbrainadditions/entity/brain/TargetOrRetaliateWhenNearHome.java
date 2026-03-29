package com.github.theredbrain.mobbrainadditions.entity.brain;

import com.github.theredbrain.mobbrainadditions.registry.MemoryModuleTypeRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Sets the attack target of the entity, utilising a few sources of targets. Only runs when entity has the {@link MemoryModuleTypeRegistry#IS_NEAR_HOME} memory. The target also has to <br>
 * In order:
 * <ol>
 *     <li>The {@link MemoryModuleType#NEAREST_ATTACKABLE} memory value</li>
 *     <li>The {@link MemoryModuleType#HURT_BY_ENTITY} memory value</li>
 *     <li>The closest applicable entity from the {@link MemoryModuleType#VISIBLE_MOBS} memory value</li>
 * </ol>
 * Defaults:
 * <ul>
 *     <li>Targets any live entity, as long as it's not a creative-mode player</li>
 *     <li>Does not alert nearby allies when retaliating</li>
 *     <li>If enabled, only alerts allies of the same class, if they don't already have a target themselves</li>
 * </ul>
 * @param <E> The entity
 */
public class TargetOrRetaliateWhenNearHome<E extends MobEntity> extends ExtendedBehaviour<E> {
	private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(5).hasMemory(MemoryModuleTypeRegistry.IS_NEAR_HOME).usesMemories(MemoryModuleType.ATTACK_TARGET, MemoryModuleType.HURT_BY, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.VISIBLE_MOBS);

	protected Predicate<LivingEntity> canAttackPredicate = entity -> entity.isAlive() && (!(entity instanceof PlayerEntity player) || !player.getAbilities().invulnerable);
	protected BiPredicate<E, Entity> alertAlliesPredicate = (owner, attacker) -> false;
	protected BiPredicate<E, LivingEntity> allyPredicate = (owner, ally) -> {
		if (!owner.getClass().isAssignableFrom(ally.getClass()) || BrainUtils.getTargetOfEntity(ally) != null)
			return false;

		if (owner instanceof Tameable pet && pet.getOwner() != ((Tameable)ally).getOwner())
			return false;

		Entity lastHurtBy = BrainUtils.getMemory(ally, MemoryModuleType.HURT_BY_ENTITY);

		return lastHurtBy == null || !ally.isTeammate(lastHurtBy);
	};
	protected boolean canSwapTarget = true;

	protected int maxEnemyDistanceFromHomePos = 10;

	protected LivingEntity toTarget = null;
	protected MemoryModuleType<? extends LivingEntity> priorityTargetMemory = MemoryModuleType.NEAREST_ATTACKABLE;

	/**
	 * Set the predicate to determine whether a given entity should be targeted or not.
	 * @param maxEnemyDistanceFromHomePos The maximum enemy distance from this entity's home position, in blocks
	 * @return this
	 */
	public TargetOrRetaliateWhenNearHome<E> setMaxEnemyDistanceFromHomePos(int maxEnemyDistanceFromHomePos) {
		this.maxEnemyDistanceFromHomePos = maxEnemyDistanceFromHomePos;

		return this;
	}

	/**
	 * Set the predicate to determine whether a given entity should be targeted or not.
	 * @param predicate The predicate
	 * @return this
	 */
	public TargetOrRetaliateWhenNearHome<E> attackablePredicate(Predicate<LivingEntity> predicate) {
		this.canAttackPredicate = predicate;

		return this;
	}

	/**
	 * Set the memory type that is checked first to target an entity.
	 * Useful for switching to player-only targeting
	 * @return this
	 */
	public TargetOrRetaliateWhenNearHome<E> useMemory(MemoryModuleType<? extends LivingEntity> memory) {
		this.priorityTargetMemory = memory;

		return this;
	}

	/**
	 * Set the predicate to determine whether the brain owner should alert nearby allies of the same entity type when retaliating
	 * @param predicate The predicate
	 * @return this
	 */
	public TargetOrRetaliateWhenNearHome<E> alertAlliesWhen(BiPredicate<E, Entity> predicate) {
		this.alertAlliesPredicate = predicate;

		return this;
	}

	/**
	 * Set the predicate to determine whether a given entity should be alerted to the target as an ally of the brain owner.<br>
	 * Overriding replaces the default predicate, so be sure to include any portions of the default predicate in your own if applicable
	 * @param predicate The predicate
	 * @return this
	 */
	public TargetOrRetaliateWhenNearHome<E> isAllyIf(BiPredicate<E, LivingEntity> predicate) {
		this.allyPredicate = predicate;

		return this;
	}

	/**
	 * Disable the ability to occasionally swap targets if a higher priority target source has one
	 */
	public TargetOrRetaliateWhenNearHome<E> noTargetSwapping() {
		this.canSwapTarget = false;

		return this;
	}

	@Override
	protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
		return MEMORY_REQUIREMENTS;
	}

	@Override
	protected boolean doStartCheck(ServerWorld level, E entity, long gameTime) {
		return (!BrainUtils.hasMemory(entity, MemoryModuleType.ATTACK_TARGET) || (this.canSwapTarget && entity.age % 100 == 0)) && super.doStartCheck(level, entity, gameTime);
	}

	@Override
	protected boolean shouldRun(ServerWorld level, E owner) {
		this.toTarget = getTarget(owner, level, BrainUtils.getTargetOfEntity(owner));

		return this.toTarget != null;
	}

	@Nullable
	protected LivingEntity getTarget(E owner, ServerWorld level, @Nullable LivingEntity existingTarget) {
		Brain<?> brain = owner.getBrain();
		LivingEntity newTarget = BrainUtils.getMemory(brain, this.priorityTargetMemory);

		if (newTarget == null) {
			newTarget = BrainUtils.getMemory(brain, MemoryModuleType.HURT_BY_ENTITY);

			if (newTarget == null) {
				LivingTargetCache nearbyEntities = BrainUtils.getMemory(brain, MemoryModuleType.VISIBLE_MOBS);

				if (nearbyEntities != null)
					newTarget = nearbyEntities.findFirst(this.canAttackPredicate).orElse(null);

				if (newTarget == null)
					return null;
			}
		}

		if (newTarget == existingTarget)
			return null;
		GlobalPos homePos = BrainUtils.getMemory(owner, MemoryModuleType.HOME);
		if (homePos == null) {
			return null;
		}
		if (newTarget.getBlockPos().getManhattanDistance(homePos.pos()) >= this.maxEnemyDistanceFromHomePos) {
			return null;
		}

		return this.canAttackPredicate.test(newTarget) ? newTarget : null;
	}

	@Override
	protected void start(E entity) {
		LivingEntity existingTarget = BrainUtils.getTargetOfEntity(entity);

		BrainUtils.setTargetOfEntity(entity, this.toTarget);
		BrainUtils.clearMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);

		if (this.alertAlliesPredicate.test(entity, this.toTarget) && existingTarget == null)
			alertAllies((ServerWorld)entity.getWorld(), entity);

		this.toTarget = null;
	}

	protected void alertAllies(ServerWorld level, E owner) {
		double followRange = owner.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);

		for (LivingEntity ally : EntityRetrievalUtil.getEntities(owner, followRange, 10, followRange, LivingEntity.class, entity -> this.allyPredicate.test(owner, entity))) {
			BrainUtils.setTargetOfEntity(ally, this.toTarget);
		}
	}
}