package com.example.wendigodweller.client;

import com.example.wendigodweller.WendigoDweller;
import com.example.wendigodweller.WendigoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class WendigoGeoModel extends GeoModel<WendigoEntity> {
    private static final ResourceLocation MODEL =
            new ResourceLocation(WendigoDweller.MOD_ID, "geo/wendigo.geo.json");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(WendigoDweller.MOD_ID, "textures/entity/wendigo.png");
    private static final ResourceLocation ANIMATIONS =
            new ResourceLocation(WendigoDweller.MOD_ID, "animations/wendigo.animation.json");

    @Override public ResourceLocation getModelResource(WendigoEntity entity) { return MODEL; }
    @Override public ResourceLocation getTextureResource(WendigoEntity entity) { return TEXTURE; }
    @Override public ResourceLocation getAnimationResource(WendigoEntity entity) { return ANIMATIONS; }
}
