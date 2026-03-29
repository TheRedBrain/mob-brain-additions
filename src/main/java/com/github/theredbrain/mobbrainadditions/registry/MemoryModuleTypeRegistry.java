package com.github.theredbrain.mobbrainadditions.registry;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Unit;

import java.util.Map;
import java.util.Optional;

public class MemoryModuleTypeRegistry {

	public static MemoryModuleType<Unit> IS_NEAR_HOME;

	private static <T> MemoryModuleType<T> register(String id) {
		return Registry.register(Registries.MEMORY_MODULE_TYPE, MobBrainAdditions.identifier(id), new MemoryModuleType<>(Optional.empty()));
	}

	private static <T> MemoryModuleType<T> register(String id, Codec<T> codec) {
		return Registry.register(Registries.MEMORY_MODULE_TYPE, MobBrainAdditions.identifier(id), new MemoryModuleType<>(Optional.of(codec)));
	}

	public static void init() {
		IS_NEAR_HOME = register("is_near_home", Unit.CODEC);
	}
}
