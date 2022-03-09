package io.github.stuff_stuffs.linedrawexample;

import io.github.stuff_stuffs.linedrawexample.entity.LineAnchorEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LineDrawExample implements ModInitializer {
    public static final EntityType<LineAnchorEntity> LINE_ANCHOR_ENTITY_TYPE = FabricEntityTypeBuilder.createLiving().entityFactory(LineAnchorEntity::new).spawnGroup(SpawnGroup.MISC).defaultAttributes(LivingEntity::createLivingAttributes).dimensions(EntityDimensions.fixed(1, 1)).build();

    @Override
    public void onInitialize() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier("test", "line_anchor"), LINE_ANCHOR_ENTITY_TYPE);
    }
}
