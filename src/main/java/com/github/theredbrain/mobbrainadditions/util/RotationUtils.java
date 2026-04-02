package com.github.theredbrain.mobbrainadditions.util;

import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

public class RotationUtils {

	public static int calculateNewRotatedBlockState(int currentStateValue, BlockRotation rotation) {
		if (rotation == BlockRotation.CLOCKWISE_90) {
			return (currentStateValue + 1) % 4;
		} else if (rotation == BlockRotation.CLOCKWISE_180) {
			return (currentStateValue + 2) % 4;
		} else if (rotation == BlockRotation.COUNTERCLOCKWISE_90) {
			return (currentStateValue + 3) % 4;
		} else {
			return currentStateValue;
		}
	}

	public static BlockRotation calculateRotationFromDifferentRotatedStates(int value1, int value2) {
		if ((value2 + 1) % 4 == value1) {
			return BlockRotation.CLOCKWISE_90;
		} else if ((value2 + 2) % 4 == value1) {
			return BlockRotation.CLOCKWISE_180;
		} else if ((value2 + 3) % 4 == value1) {
			return BlockRotation.COUNTERCLOCKWISE_90;
		} else {
			return BlockRotation.NONE;
		}
	}

	public static BlockPos rotateOffsetBlockPos(BlockPos blockPos, BlockRotation rotation) {
		if (rotation == BlockRotation.CLOCKWISE_90) {
			return new BlockPos(-(blockPos.getZ()), blockPos.getY(), blockPos.getX());
		} else if (rotation == BlockRotation.CLOCKWISE_180) {
			return new BlockPos(-(blockPos.getX()), blockPos.getY(), -(blockPos.getZ()));
		} else if (rotation == BlockRotation.COUNTERCLOCKWISE_90) {
			return new BlockPos(blockPos.getZ(), blockPos.getY(), -(blockPos.getX()));
		} else {
			return blockPos;
		}
	}

	public static BlockPos mirrorOffsetBlockPos(BlockPos blockPos, BlockMirror mirror) {
		if (mirror == BlockMirror.FRONT_BACK) {
			return new BlockPos(-(blockPos.getX()), blockPos.getY(), blockPos.getZ());
		} else if (mirror == BlockMirror.LEFT_RIGHT) {
			return new BlockPos(blockPos.getX(), blockPos.getY(), -(blockPos.getZ()));
		} else {
			return blockPos;
		}
	}

}
