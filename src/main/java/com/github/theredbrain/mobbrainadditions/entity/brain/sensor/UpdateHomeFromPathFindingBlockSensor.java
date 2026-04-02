package com.github.theredbrain.mobbrainadditions.entity.brain.sensor;

import com.github.theredbrain.mobbrainadditions.block.entity.ProvidesPathFindingNode;
import com.github.theredbrain.mobbrainadditions.entity.mob.TracksPathFindingNodes;
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

public class UpdateHomeFromPathFindingBlockSensor<E extends MobEntity> extends ExtendedSensor<E> { // Extend PredicateSensor so we can use the builtin predicate to check for lava
	private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(MemoryModuleType.HOME); // Make a static list of memories this sensor applies to. For this example we'll assume we registered a custom MemoryModuleType in MyMemoryTypes

	protected SquareRadius radius = new SquareRadius(1, 1);

	public UpdateHomeFromPathFindingBlockSensor() {
	}

	@Override
	public List<MemoryModuleType<?>> memoriesUsed() {
		return MEMORIES; // Return our memory list
	}

	@Override
	public SensorType<? extends ExtendedSensor<?>> type() {
		return SensorTypeRegistry.UPDATE_HOME_FROM_PATH_FINDING_BLOCK; // Return the SensorType for this sensor. For this example we'll assume we registered our sensortype in MySensorTypes
	}

	/**
	 * Set the radius for the sensor to scan
	 *
	 * @param radius The coordinate radius, in blocks
	 * @return this
	 */
	public UpdateHomeFromPathFindingBlockSensor<E> setRadius(double radius) {
		return setRadius(radius, radius);
	}

	/**
	 * Set the radius for the sensor to scan.
	 *
	 * @param xz The X/Z coordinate radius, in blocks
	 * @param y  The Y coordinate radius, in blocks
	 * @return this
	 */
	public UpdateHomeFromPathFindingBlockSensor<E> setRadius(double xz, double y) {
		this.radius = new SquareRadius(xz, y);

		return this;
	}

	@Override
	public void sense(ServerWorld level, E entity) {
		BlockPos newHomePos;

		for (BlockPos pos : BlockPos.iterate(entity.getBlockPos().subtract(this.radius.toVec3i()), entity.getBlockPos().add(this.radius.toVec3i()))) {

			if (entity instanceof TracksPathFindingNodes tracksPathFindingNodes && level.getBlockEntity(pos) instanceof ProvidesPathFindingNode providesPathFindingNode) {
				String trackedPathFindingNodeId = tracksPathFindingNodes.getTrackedPathFindingNodeId();
				if (!trackedPathFindingNodeId.isEmpty()) {
					newHomePos = providesPathFindingNode.getNode(trackedPathFindingNodeId);
					if (newHomePos != null) {
						BrainUtils.setMemory(entity, MemoryModuleType.HOME, new GlobalPos(level.getRegistryKey(), newHomePos));
						break;
					}
				}
			}
		}
	}
}