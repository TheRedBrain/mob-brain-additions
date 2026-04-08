package com.github.theredbrain.mobbrainadditions.render.block.entity;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingBranchingNodeBlockEntity;
import com.github.theredbrain.mobbrainadditions.registry.BlockRegistry;
import com.github.theredbrain.mobbrainadditions.registry.Tags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.random.Random;

@Environment(value = EnvType.CLIENT)
public class PathFindingBranchingNodeBlockEntityRenderer
		implements BlockEntityRenderer<PathFindingBranchingNodeBlockEntity> {
	public PathFindingBranchingNodeBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
	}

	@Override
	public void render(PathFindingBranchingNodeBlockEntity pathFindingBranchingNodeBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getCutout());
		boolean debugRender = MobBrainAdditions.SERVER_CONFIG.enable_path_finding_node_block_debug_mode.get();
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null) {
			debugRender = debugRender || (player.isCreativeLevelTwoOp() && player.getInventory().getMainHandStack().isIn(Tags.PROVIDES_PATH_FINDING_NODE));
		}
		if (debugRender) {
			blockRenderManager.renderBlock(BlockRegistry.VISIBLE_PATH_FINDING_BRANCHING_NODE_BLOCK.getDefaultState(), pathFindingBranchingNodeBlockEntity.getPos(), pathFindingBranchingNodeBlockEntity.getWorld(), matrixStack, vertexConsumer, true, Random.create());
		}
	}
}

