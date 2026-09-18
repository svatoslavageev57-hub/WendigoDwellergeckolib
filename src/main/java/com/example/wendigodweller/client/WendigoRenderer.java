package com.example.wendigodweller.client;

import com.example.wendigodweller.WendigoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class WendigoRenderer extends GeoEntityRenderer<WendigoEntity> {
    public WendigoRenderer(EntityRendererProvider.Context context) {
        super(context, new WendigoGeoModel());
        this.shadowRadius = 0.55F;
    }
}
