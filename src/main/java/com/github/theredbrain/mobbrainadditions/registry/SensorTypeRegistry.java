package com.github.theredbrain.mobbrainadditions.registry;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import com.github.theredbrain.mobbrainadditions.entity.brain.IsNearHomeSensor;
import com.github.theredbrain.mobbrainadditions.entity.brain.UpdateHomeFromPathFindingBlockSensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;

import java.util.function.Supplier;

public class SensorTypeRegistry {

	public static SensorType<IsNearHomeSensor<?>> IS_NEAR_HOME;
	public static SensorType<UpdateHomeFromPathFindingBlockSensor<?>> UPDATE_HOME_FROM_PATH_FINDING_BLOCK;

	private static <T extends ExtendedSensor<?>> SensorType<T> register(String id, Supplier<T> sensor) {
		return Registry.register(Registries.SENSOR_TYPE, MobBrainAdditions.identifier(id), new SensorType<>(sensor));
	}

	public static void init() {
		IS_NEAR_HOME = register("is_near_home", IsNearHomeSensor::new);
		UPDATE_HOME_FROM_PATH_FINDING_BLOCK = register("update_home_from_path_finding_block", UpdateHomeFromPathFindingBlockSensor::new);
	}
}
