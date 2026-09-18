package com.example.wendigodweller;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class NightTriggerEvent {
    private NightTriggerEvent() {}

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % 20 != 0) {
            return;
        }

        for (ServerLevel level : event.getServer().getAllLevels()) {
            long time = level.getDayTime() % 24000L;

            // We sample once per second, so accept a small window around 13,000.
            if (time < 13000L || time > 13019L) {
                continue;
            }

            for (ServerPlayer player : level.players()) {
                trigger(player);
            }
        }
    }

    private static void trigger(ServerPlayer player) {
        player.sendSystemMessage(
                net.minecraft.network.chat.Component.literal("something is coming")
                        .withStyle(net.minecraft.ChatFormatting.RED)
        );

        player.playSound(SoundEvents.AMBIENT_CAVE, 1.0F, 0.65F);
        player.playSound(SoundEvents.ENTITY_ENDERMAN_SCREAM, 1.0F, 0.55F);
        player.playSound(SoundEvents.ENTITY_ENDERMAN_STARE, 0.8F, 0.45F);
        player.playSound(SoundEvents.ENTITY_PHANTOM_SCREAM, 0.9F, 0.50F);
        player.playSound(SoundEvents.ENTITY_WARDEN_HEARTBEAT, 0.9F, 0.65F);

        ServerLevel level = player.serverLevel();
        BlockPos origin = player.blockPosition();

        double angle = player.getRandom().nextDouble() * Math.PI * 2.0D;
        int distance = 10 + player.getRandom().nextInt(5);
        int x = origin.getX() + (int)Math.round(Math.cos(angle) * distance);
        int z = origin.getZ() + (int)Math.round(Math.sin(angle) * distance);

        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        if (y <= level.getMinBuildHeight() || y >= level.getMaxBuildHeight()) {
            return;
        }

        BlockPos spawnPos = new BlockPos(x, y, z);
        WendigoEntity wendigo = ModEntities.WENDIGO.get().create(level);

        if (wendigo == null) {
            return;
        }

        wendigo.moveTo(
                spawnPos.getX() + 0.5D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5D,
                player.getRandom().nextFloat() * 360.0F,
                0.0F
        );

        wendigo.finalizeSpawn(
                level,
                level.getCurrentDifficultyAt(spawnPos),
                MobSpawnType.EVENT,
                null,
                null
        );

        level.addFreshEntity(wendigo);
    }
}
