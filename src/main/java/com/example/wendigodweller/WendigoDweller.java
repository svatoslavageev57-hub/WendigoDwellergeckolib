package com.example.wendigodweller;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WendigoDweller.MOD_ID)
public final class WendigoDweller {
    public static final String MOD_ID = "wendigo_dweller";

    public WendigoDweller() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEntities.ENTITY_TYPES.register(modBus);

        MinecraftForge.EVENT_BUS.register(NightTriggerEvent.class);
    }
}
