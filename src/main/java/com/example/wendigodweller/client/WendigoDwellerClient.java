package com.example.wendigodweller.client;

import com.example.wendigodweller.ModEntities;
import com.example.wendigodweller.WendigoDweller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = WendigoDweller.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class WendigoDwellerClient {
    private WendigoDwellerClient() {}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.WENDIGO.get(), WendigoRenderer::new);
    }
}
