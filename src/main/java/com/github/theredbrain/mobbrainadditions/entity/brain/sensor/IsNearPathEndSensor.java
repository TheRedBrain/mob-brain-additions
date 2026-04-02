package com.github.theredbrain.mobbrainadditions.entity.brain.sensor;

import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingEndNodeBlockEntity;
import com.github.theredbrain.mobbrainadditions.entity.mob.TracksPathFindingNodes;
import com.github.theredbrain.mobbrainadditions.registry.MemoryModuleTypeRegistry;
import com.github.theredbrain.mobbrainadditions.registry.SensorTypeRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.List;

/**
 * This sensor checks if the entity is near the end of a path. If the distance is larger than allowed, the {@link MemoryModuleTypeRegistry#IS_NEAR_HOME}, {@link MemoryModuleType#ATTACK_TARGET} and {@link MemoryModuleType#WALK_TARGET} memories are cleared.
 * If the entity has no {@link MemoryModuleType#HOME} memory, only the {@link MemoryModuleTypeRegistry#IS_NEAR_HOME} memory is cleared.<br>
 * Default:
 * <ul>
 *     <li>10-block max distance from path</li>
 * </ul>
 *
 * @param <E> The entity
 */
public class IsNearPathEndSensor<E extends MobEntity> extends ExtendedSensor<E> {
	private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(MemoryModuleTypeRegistry.PATH_END_POSITION); // Make a static list of memories this sensor applies to. For this example we'll assume we registered a custom MemoryModuleType in MyMemoryTypes

	protected SquareRadius radius = new SquareRadius(1, 1);

	public IsNearPathEndSensor() {
	}

	@Override
	public List<MemoryModuleType<?>> memoriesUsed() {
		return MEMORIES; // Return our memory list
	}

	@Override
	public SensorType<? extends ExtendedSensor<?>> type() {
		return SensorTypeRegistry.IS_NEAR_PATH_END; // Return the SensorType for this sensor. For this example we'll assume we registered our sensortype in MySensorTypes
	}

	/**
	 * Set the radius for the sensor to scan
	 *
	 * @param radius The coordinate radius, in blocks
	 * @return this
	 */
	public IsNearPathEndSensor<E> setRadius(double radius) {
		return setRadius(radius, radius);
	}

	/**
	 * Set the radius for the sensor to scan.
	 *
	 * @param xz The X/Z coordinate radius, in blocks
	 * @param y  The Y coordinate radius, in blocks
	 * @return this
	 */
	public IsNearPathEndSensor<E> setRadius(double xz, double y) {
		this.radius = new SquareRadius(xz, y);

		return this;
	}

	@Override
	public void sense(ServerWorld level, E entity) {

		for (BlockPos pos : BlockPos.iterate(entity.getBlockPos().subtract(this.radius.toVec3i()), entity.getBlockPos().add(this.radius.toVec3i()))) {

			if (entity instanceof TracksPathFindingNodes tracksPathFindingNodes && level.getBlockEntity(pos) instanceof PathFindingEndNodeBlockEntity pathFindingEndNodeBlockEntity) {
				String trackedPathFindingNodeId = tracksPathFindingNodes.getTrackedPathFindingNodeId();
				if (!trackedPathFindingNodeId.isEmpty()) {
					if (pathFindingEndNodeBlockEntity.hasNodeId(trackedPathFindingNodeId)) {
						BrainUtils.setMemory(entity, MemoryModuleTypeRegistry.PATH_END_POSITION, new GlobalPos(level.getRegistryKey(), pos));
						break;
					}
				}
			}
		}
	}
}