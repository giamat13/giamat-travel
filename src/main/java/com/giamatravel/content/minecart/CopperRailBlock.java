package com.giamatravel.content.minecart;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A rail whose top speed is set by right-clicking (0, 4, 8 … 80). It only drives a minecart while it
 * receives a redstone signal (it reuses {@link PoweredRailBlock}'s powering logic).
 */
public class CopperRailBlock extends PoweredRailBlock {
	public static final MapCodec<CopperRailBlock> CODEC = simpleCodec(CopperRailBlock::new);
	/** Speed step 0..20; the actual speed value is step * 4 (so 0..80). */
	public static final IntegerProperty SPEED = IntegerProperty.create("speed", 0, 20);
	public static final int MAX_STEP = 20;

	public CopperRailBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(SHAPE, RailShape.NORTH_SOUTH)
				.setValue(POWERED, false)
				.setValue(WATERLOGGED, false)
				.setValue(SPEED, 15));
	}

	@Override
	@SuppressWarnings("unchecked")
	public MapCodec<PoweredRailBlock> codec() {
		return (MapCodec<PoweredRailBlock>) (MapCodec<?>) CODEC;
	}

	/** The configured speed value (0..80). */
	public int speedValue(BlockState state) {
		return state.getValue(SPEED) * 4;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide()) {
			int next = (state.getValue(SPEED) + 1) % (MAX_STEP + 1);
			level.setBlock(pos, state.setValue(SPEED, next), 3);
			float pitch = 0.6F + 0.05F * next;
			level.playSound(null, pos, SoundType.COPPER.getPlaceSound(), SoundSource.BLOCKS, 0.7F, pitch);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		builder.add(SHAPE, POWERED, WATERLOGGED, SPEED);
	}
}
