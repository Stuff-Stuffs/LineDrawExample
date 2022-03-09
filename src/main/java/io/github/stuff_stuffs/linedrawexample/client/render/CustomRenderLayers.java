package io.github.stuff_stuffs.linedrawexample.client.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import java.util.OptionalDouble;

public final class CustomRenderLayers extends RenderPhase {
    private static final LineWidth THICK_LINE_WIDTH = new LineWidth(OptionalDouble.of(10));
    public static final RenderLayer THICK_LINE = RenderLayer.of("thick_line", VertexFormats.LINES, VertexFormat.DrawMode.LINES, 256, false, false, RenderLayer.MultiPhaseParameters.builder().target(RenderPhase.ITEM_TARGET).shader(RenderPhase.LINES_SHADER).lineWidth(THICK_LINE_WIDTH).build(false));

    private CustomRenderLayers() {
        //Doesnt matter never invoked, we extedn RenderPhase solely to access its protected members
        super(null, null, null);
    }
}
