package com.github.theredbrain.mobbrainadditions.entity.brain.sensor;

import com.github.theredbrain.mobbrainadditions.registry.MemoryModuleTypeRegistry;
import com.github.theredbrain.mobbrainadditions.registry.SensorTypeRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.GlobalPos;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

/**
 * This sensor checks if the entity is in range of its home position. If the distance is larger than allowed, the {@link MemoryModuleTypeRegistry#IS_NEAR_HOME}, {@link MemoryModuleType#ATTACK_TARGET}(optionally) and {@link MemoryModuleType#WALK_TARGET}(optionally) memories are cleared.
 * If the entity has no {@link MemoryModuleType#HOME} memory and no {@link MemoryModuleType#HOME} memory, only the {@link MemoryModuleTypeRegistry#IS_NEAR_HOME} memory is cleared.<br>
 * Default:
 * <ul>
 *     <li>10-block max distance from home or old home</li>
 *     <li>attack target memory is cleared when not near home</li>
 *     <li>walk target memory is cleared when not near home</li>
 * </ul>
 *
 * @param <E> The entity
 */
public class IsNearHomeSensor<E extends MobEntity> extends ExtendedSensor<E> { // Extend PredicateSensor so we can use the builtin predicate to check for lava
	private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(MemoryModuleTypeRegistry.IS_NEAR_HOME, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.WALK_TARGET); // Make a static list of memories this sensor applies to. For this example we'll assume we registered a custom MemoryModuleType in MyMemoryTypes

	protected int maxDistanceFromHome = 10;

	protected boolean checkDistanceToOldHome = true;

	protected boolean clearAttackTarget = true;

	protected boolean clearWalkTarget = true;

	public IsNearHomeSensor() {
	}

	@Override
	public List<MemoryModuleType<?>> memoriesUsed() {
		return MEMORIES; // Return our memory list
	}

	@Override
	public SensorType<? extends ExtendedSensor<?>> type() {
		return SensorTypeRegistry.IS_NEAR_HOME; // Return the SensorType for this sensor. For this example we'll assume we registered our sensortype in MySensorTypes
	}

	/**
	 * Whether the sensor alternatively checks if the entity is near its {@link MemoryModuleTypeRegistry#OLD_HOME} memory.
	 *
	 * @param checkDistanceToOldHome max distance from the path, in blocks
	 * @return this
	 */
	public IsNearHomeSensor<E> checkDistanceToOldHome(boolean checkDistanceToOldHome) {
		this.checkDistanceToOldHome = checkDistanceToOldHome;

		return this;
	}

	/**
	 * Whether the sensor clears the {@link MemoryModuleType#WALK_TARGET} memory.
	 *
	 * @return this
	 */
	public IsNearHomeSensor<E> clearAttackTarget(boolean clearAttackTarget) {
		this.clearAttackTarget = clearAttackTarget;

		return this;
	}

	/**
	 * Whether the sensor clears the {@link MemoryModuleType#WALK_TARGET} memory.
	 *
	 * @return this
	 */
	public IsNearHomeSensor<E> clearWalkTarget(boolean clearWalkTarget) {
		this.clearWalkTarget = clearWalkTarget;

		return this;
	}

	/**
	 * Set the max distance from the path after which the sensor removes the {@link MemoryModuleTypeRegistry#IS_NEAR_HOME} memory.
	 *
	 * @param maxDistanceFromHome max distance from home, in blocks
	 * @return this
	 */
	public IsNearHomeSensor<E> setMaxDistanceFromHome(int maxDistanceFromHome) {
		this.maxDistanceFromHome = maxDistanceFromHome;

		return this;
	}

	@Override
	protected void sense(ServerWorld level, E entity) {
		GlobalPos homePos = BrainUtils.getMemory(entity, MemoryModuleType.HOME);
		GlobalPos oldHomePos = BrainUtils.getMemory(entity, MemoryModuleTypeRegistry.OLD_HOME);
		if (homePos == null && oldHomePos == null) {
			BrainUtils.clearMemory(entity, MemoryModuleTypeRegistry.IS_NEAR_HOME);
			return;
		}
		if ((homePos != null && entity.getBlockPos().getManhattanDistance(homePos.pos()) <= this.maxDistanceFromHome) || (this.checkDistanceToOldHome && oldHomePos != null && entity.getBlockPos().getManhattanDistance(oldHomePos.pos()) <= this.maxDistanceFromHome)) {
			BrainUtils.setMemory(entity, MemoryModuleTypeRegistry.IS_NEAR_HOME, Unit.INSTANCE);
		} else {
			BrainUtils.clearMemory(entity, MemoryModuleTypeRegistry.IS_NEAR_HOME);
			if (this.clearAttackTarget) {
				BrainUtils.clearMemory(entity, MemoryModuleType.ATTACK_TARGET);
			}
			if (this.clearWalkTarget) {
				BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
			}
		}
	}
}