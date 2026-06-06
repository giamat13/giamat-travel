package com.giamatravel.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Lets horses ride through leaves while still being able to stand on the canopy.
 * A leaf block has no collision for a horse moving through it, but keeps its collision when the
 * horse is descending onto its top face — so you can ride across treetops in a dark forest
 * without falling, yet brush straight through foliage at body height.
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class LeavesCollisionMixin {
	@Shadow
	public abstract Block getBlock();

	@ModifyReturnValue(
			method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
			at = @At("RETURN"))
	private VoxelShape giamatravel$horseLeafPassthrough(VoxelShape original, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (original.isEmpty() || !(getBlock() instanceof LeavesBlock)) {
			return original;
		}
		if (!(context instanceof EntityCollisionContext entityContext)
				|| !(entityContext.getEntity() instanceof AbstractHorse horse)) {
			return original;
		}

		// Standing on the canopy: feet at/above this block's top and not moving upward -> keep footing.
		double topY = pos.getY() + 1.0;
		if (horse.getDeltaMovement().y <= 0.0 && horse.getY() >= topY - 0.05) {
			return original;
		}
		// Otherwise the horse passes straight through the leaves.
		return Shapes.empty();
	}
}
