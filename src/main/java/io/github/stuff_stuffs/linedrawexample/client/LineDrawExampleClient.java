package io.github.stuff_stuffs.linedrawexample.client;

import io.github.stuff_stuffs.linedrawexample.LineDrawExample;
import io.github.stuff_stuffs.linedrawexample.client.render.LiveAnchorEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class LineDrawExampleClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(LineDrawExample.LINE_ANCHOR_ENTITY_TYPE, LiveAnchorEntityRenderer::new);
    }
}
