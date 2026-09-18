package com.example.wendigodweller;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import de.maxhenkel.voicechat.api.packets.MicrophonePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.sounds.SoundEvents;

@ForgeVoicechatPlugin
public final class VoiceChatIntegration implements VoicechatPlugin {
    private volatile VoicechatApi api;

    @Override
    public String getPluginId() {
        return "wendigo_dweller";
    }

    @Override
    public void initialize(VoicechatApi api) {
        this.api = api;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket);
    }

    private void onMicrophonePacket(MicrophonePacketEvent event) {
        if (api == null) {
            return;
        }

        var connection = event.getSenderConnection();
        if (connection == null || connection.getPlayer() == null) {
            return;
        }

        ServerPlayer speaker = connection.getPlayer();
        MicrophonePacket packet = event.getPacket();

        if (packet == null || packet.getOpusEncodedData() == null) {
            return;
        }

        // Simple Voice Chat sends Opus-encoded microphone frames.
        // Decode one frame and calculate 16-bit PCM RMS.
        OpusDecoder decoder = null;
        try {
            decoder = api.createDecoder();
            short[] pcm = decoder.decode(packet.getOpusEncodedData());

            if (pcm == null || pcm.length == 0) {
                return;
            }

            double sum = 0.0D;
            for (short sample : pcm) {
                double s = sample;
                sum += s * s;
            }

            double rms = Math.sqrt(sum / pcm.length);

            if (rms > 1500.0D) {
                alertNearbyWendigos(speaker);
            }
        } catch (Throwable ignored) {
            // Do not let a voice packet break the voice-chat pipeline.
        } finally {
            if (decoder != null) {
                try {
                    decoder.close();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    private void alertNearbyWendigos(ServerPlayer speaker) {
        ServerLevel level = speaker.serverLevel();
        AABB box = speaker.getBoundingBox().inflate(64.0D);

        for (WendigoEntity wendigo : level.getEntitiesOfClass(
                WendigoEntity.class,
                box,
                Entity::isAlive
        )) {
            wendigo.hearPlayer(speaker);
            wendigo.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 2.0F, 0.65F);
        }
    }
}
