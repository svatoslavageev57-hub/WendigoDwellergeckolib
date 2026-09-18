package com.example.wendigodweller;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.EnumSet;

public class WendigoBreakBarrierGoal extends Goal {
    private final WendigoEntity mob;
    private BlockPos barrierPos;
    private int ticks;

    public WendigoBreakBarrierGoal(WendigoEntity mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        BlockPos candidate = findBarrierAhead();
        if (candidate == null) {
            return false;
        }
        barrierPos = candidate;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return ticks < 40 && barrierPos != null && isBarrier(mob.level(), barrierPos);
    }

    @Override
    public void start() {
        ticks = 0;
        mob.getNavigation().stop();
        mob.getLookControl().setLookAt(
                barrierPos.getX() + 0.5D,
                barrierPos.getY() + 0.5D,
                barrierPos.getZ() + 0.5D
        );
    }

    @Override
    public void tick() {
        ticks++;

        if (barrierPos == null) {
            return;
        }

        mob.getNavigation().stop();
        mob.getLookControl().setLookAt(
                barrierPos.getX() + 0.5D,
                barrierPos.getY() + 0.5D,
                barrierPos.getZ() + 0.5D
        );

        // Heavy knocking every 10 ticks while waiting.
        if (ticks % 10 == 0 && ticks < 40) {
            mob.playSound(SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, 1.4F, 0.55F);
        }

        // Exactly 40 ticks = 2 seconds.
        if (ticks == 40 && isBarrier(mob.level(), barrierPos)) {
            BlockState state = mob.level().getBlockState(barrierPos);
            boolean glass = isGlassPane(state);

            mob.level().destroyBlock(barrierPos, false);

            // Wooden doors consist of two blocks; remove the other half too.
            if (state.getBlock() instanceof DoorBlock) {
                DoubleBlockHalf half = state.getValue(DoorBlock.HALF);
                BlockPos other = half == DoubleBlockHalf.LOWER
                        ? barrierPos.above()
                        : barrierPos.below();

                if (mob.level().getBlockState(other).getBlock() instanceof DoorBlock) {
                    mob.level().destroyBlock(other, false);
                }
            }

            mob.playSound(
                    glass ? SoundEvents.BLOCK_GLASS_BREAK : SoundEvents.BLOCK_WOODEN_DOOR_BREAK,
                    1.5F,
                    0.65F
            );
        }
    }

    @Override
    public void stop() {
        barrierPos = null;
        ticks = 0;
    }

    private BlockPos findBarrierAhead() {
        Level level = mob.level();

        // Check the block directly in front, plus the block at the mob's feet/head.
        BlockPos base = mob.blockPosition();
        var forward = mob.getViewVector(1.0F).normalize();

        int dx = Integer.signum((int)Math.round(forward.x));
        int dz = Integer.signum((int)Math.round(forward.z));

        BlockPos[] candidates = new BlockPos[] {
                base.offset(dx, 0, dz),
                base.offset(dx, 1, dz),
                base.offset(dx, -1, dz)
        };

        for (BlockPos pos : candidates) {
            if (isBarrier(level, pos)) {
                return pos;
            }
        }
        return null;
    }

    private static boolean isBarrier(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        return state.is(BlockTags.WOODEN_DOORS)
                || block == Blocks.GLASS_PANE
                || block instanceof StainedGlassPaneBlock
                || (block instanceof IronBarsBlock && block != Blocks.IRON_BARS);
    }

    private static boolean isGlassPane(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.GLASS_PANE || block instanceof StainedGlassPaneBlock;
    }
}
