package com.github.theredbrain.mobbrainadditions.block;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingEndNodeBlockEntity;
import com.github.theredbrain.mobbrainadditions.entity.player.DuckPlayerEntityMixin;
import com.github.theredbrain.mobbrainadditions.registry.BlockRegistry;
import com.mojang.serialization.MapCodec;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class PathFindingEndNodeBlock extends BlockWithEntity implements Waterloggable {
	public static final MapCodec<PathFindingEndNodeBlock> CODEC = createCodec(PathFindingEndNodeBlock::new);
	public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	public PathFindingEndNodeBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(TRIGGERED, false).with(WATERLOGGED, false));
	}

	public MapCodec<PathFindingEndNodeBlock> getCodec() {
		return CODEC;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(TRIGGERED, WATERLOGGED);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new PathFindingEndNodeBlockEntity(pos, state);
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
		if ((Boolean) state.get(WATERLOGGED)) {
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
		BlockState pathFindingBranchingNodeBlockState = BlockRegistry.VISIBLE_PATH_FINDING_BRANCHING_NODE_BLOCK.getDefaultState();
		if (!(MobBrainAdditions.SERVER_CONFIG.enable_path_finding_node_block_debug_mode.get() || context.isHolding(BlockRegistry.PATH_FINDING_END_NODE_BLOCK.asItem()))) {
			pathFindingBranchingNodeBlockState = Blocks.AIR.getDefaultState();
		}
		return pathFindingBranchingNodeBlockState.getOutlineShape(world, pos);
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
		if (blockEntity instanceof PathFindingEndNodeBlockEntity pathFindingEndNodeBlockEntity && player.isCreativeLevelTwoOp()) {
			((DuckPlayerEntityMixin) player).scriptblocks$openPathFindingEndNodeBlockScreen(pathFindingEndNodeBlockEntity);
			return ActionResult.success(world.isClient);
		}
		return ActionResult.PASS;
	}

	@Override
	protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(TRIGGERED) ? 15 : 0;
	}

	@Override
	protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(TRIGGERED) ? 15 : 0;
	}

	@Override
	protected boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (state.get(TRIGGERED)) {
			world.setBlockState(pos, (BlockState) state.with(TRIGGERED, false), Block.NOTIFY_ALL);
			world.updateNeighborsAlways(pos, this);
			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}
		}
	}

	public void trigger(World world, BlockState blockState, BlockPos pos) {
		if (!blockState.get(TRIGGERED)) {
			world.scheduleBlockTick(pos, this, 4);
			world.setBlockState(pos, (BlockState) blockState.with(TRIGGERED, true), Block.NOTIFY_ALL);
			world.updateNeighborsAlways(pos, this);
			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}
		}
	}

}
