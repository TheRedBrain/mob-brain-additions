package com.github.theredbrain.mobbrainadditions;

import com.github.theredbrain.mobbrainadditions.registry.BlockRegistry;
import com.github.theredbrain.mobbrainadditions.registry.EntityRegistry;
import com.github.theredbrain.mobbrainadditions.render.block.entity.PathFindingNodeBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(value = EnvType.CLIENT)
public class MobBrainAdditionsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		registerTransparency();
		registerBlockEntityRenderer();
	}

	private void registerTransparency() {
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
				BlockRegistry.PATH_FINDING_NODE_BLOCK,
				BlockRegistry.VISIBLE_PATH_FINDING_NODE_BLOCK
		);
	}

	private void registerBlockEntityRenderer() {
		BlockEntityRendererFactories.register(EntityRegistry.PATH_FINDING_NODE_BLOCK_ENTITY, PathFindingNodeBlockEntityRenderer::new);
	}

}