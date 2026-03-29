package com.github.theredbrain.mobbrainadditions.mixin.entity.player;

import com.github.theredbrain.mobbrainadditions.entity.player.DuckPlayerEntityMixin;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements DuckPlayerEntityMixin {

}
