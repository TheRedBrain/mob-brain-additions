package com.github.theredbrain.mobbrainadditions.mixin.client.network;

import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingBranchingNodeBlockEntity;
import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingEndNodeBlockEntity;
import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingNodeBlockEntity;
import com.github.theredbrain.mobbrainadditions.entity.player.DuckPlayerEntityMixin;
import com.github.theredbrain.mobbrainadditions.gui.screen.ingame.PathFindingBranchingNodeBlockScreen;
import com.github.theredbrain.mobbrainadditions.gui.screen.ingame.PathFindingEndNodeBlockScreen;
import com.github.theredbrain.mobbrainadditions.gui.screen.ingame.PathFindingNodeBlockScreen;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements DuckPlayerEntityMixin {

	@Shadow
	@Final
	protected MinecraftClient client;

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Override
	public void scriptblocks$openPathFindingNodeBlockScreen(PathFindingNodeBlockEntity pathFindingNodeBlockEntity) {
		this.client.setScreen(new PathFindingNodeBlockScreen(pathFindingNodeBlockEntity));
	}

	@Override
	public void scriptblocks$openPathFindingBranchingNodeBlockScreen(PathFindingBranchingNodeBlockEntity pathFindingBranchingNodeBlockEntity) {
		this.client.setScreen(new PathFindingBranchingNodeBlockScreen(pathFindingBranchingNodeBlockEntity));
	}

	@Override
	public void scriptblocks$openPathFindingEndNodeBlockScreen(PathFindingEndNodeBlockEntity pathFindingEndNodeBlockEntity) {
		this.client.setScreen(new PathFindingEndNodeBlockScreen(pathFindingEndNodeBlockEntity));
	}

}
