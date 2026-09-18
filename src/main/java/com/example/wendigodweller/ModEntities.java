package com.example.wendigodweller;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    private ModEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, WendigoDweller.MOD_ID);

    public static final RegistryObject<EntityType<WendigoEntity>> WENDIGO =
            ENTITY_TYPES.register("wendigo", () ->
                    EntityType.Builder.of(WendigoEntity::new, MobCategory.MONSTER)
                            .sized(0.75F, 2.4F)
                            .clientTrackingRange(64)
                            .updateInterval(2)
                            .build("wendigo"));
}
