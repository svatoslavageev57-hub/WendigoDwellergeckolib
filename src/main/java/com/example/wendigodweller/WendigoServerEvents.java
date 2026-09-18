package com.example.wendigodweller;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = WendigoDweller.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public final class WendigoServerEvents {
    private WendigoServerEvents() {}

    @SubscribeEvent
    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.WENDIGO.get(), WendigoEntity.createAttributes().build());
    }
}
