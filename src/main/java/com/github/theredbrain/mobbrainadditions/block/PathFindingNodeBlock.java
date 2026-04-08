package com.github.theredbrain.mobbrainadditions.block;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingNodeBlockEntity;
import com.github.theredbrain.mobbrainadditions.entity.player.DuckPlayerEntityMixin;
import com.github.theredbrain.mobbrainadditions.registry.BlockRegistry;
import com.github.theredbrain.mobbrainadditions.util.RotationUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class PathFindingNodeBlock extends BlockWithEntity implements Waterloggable, PathFindingNode {
	public static final MapCodec<PathFindingNodeBlock> CODEC = createCodec(PathFindingNodeBlock::new);
	public static final IntProperty ROTATED = IntProperty.of("rotated", 0, 3);
	public static final BooleanProperty X_MIRRORED = BooleanProperty.of("x_mirrored");
	public static final BooleanProperty Z_MIRRORED = BooleanProperty.of("z_mirrored");
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public PathFindingNodeBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(ROTATED, 0).with(X_MIRRORED, false).with(Z_MIRRORED, false).with(WATERLOGGED, false));
	}

	public MapCodec<PathFindingNodeBlock> getCodec() {
		return CODEC;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(ROTATED, X_MIRRORED, Z_MIRRORED, WATERLOGGED);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new PathFindingNodeBlockEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
		return true;
	}

	@Override
	protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return 1.0F;
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
			BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		BlockState pathFindingNodeBlockState = BlockRegistry.VISIBLE_PATH_FINDING_NODE_BLOCK.getDefaultState();
		if (!(MobBrainAdditions.SERVER_CONFIG.enable_path_finding_node_block_debug_mode.get() || isPathFindingNodeVisible(context))) {
			pathFindingNodeBlockState = Blocks.AIR.getDefaultState();
		}
		return pathFindingNodeBlockState.getOutlineShape(world, pos);
	}

	@Override
	public ItemStack tryDrainFluid(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, BlockState state) {
		return player != null && player.isCreative() ? Waterloggable.super.tryDrainFluid(player, world, pos, state) : ItemStack.EMPTY;
	}

	@Override
	public boolean canFillWithFluid(@Nullable PlayerEntity player, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return player != null && player.isCreative() ? Waterloggable.super.canFillWithFluid(player, world, pos, state, fluid) : false;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof PathFindingNodeBlockEntity pathFindingNodeBlockEntity && player.isCreativeLevelTwoOp()) {
			((DuckPlayerEntityMixin) player).scriptblocks$openPathFindingNodeBlockScreen(pathFindingNodeBlockEntity);
			return ActionResult.success(world.isClient);
		}
		return ActionResult.PASS;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(ROTATED, RotationUtils.calculateNewRotatedBlockState(state.get(ROTATED), rotation));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		if (mirror == BlockMirror.FRONT_BACK) {
			return state.with(X_MIRRORED, !state.get(X_MIRRORED));
		} else if (mirror == BlockMirror.LEFT_RIGHT) {
			return state.with(Z_MIRRORED, !state.get(Z_MIRRORED));
		}
		return state;
	}

}
