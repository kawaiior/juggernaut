package github.kawaiior.juggernaut.init;

import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.entity.PaintBubbleEntity;
import github.kawaiior.juggernaut.entity.ReviveBeaconEntity;
import github.kawaiior.juggernaut.entity.SuperPaintBubbleEntity;
import github.kawaiior.juggernaut.entity.WatchBeaconEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypeRegistry {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, Juggernaut.MOD_ID);

    public static final RegistryObject<EntityType<ReviveBeaconEntity>> REVIVE_BEACON_ENTITY =
            ENTITY_TYPES.register("revive_beacon_entity",
                    () -> EntityType.Builder.create(ReviveBeaconEntity::new,
                            EntityClassification.MISC).size(0.5F, 0.5F)
                            .build("revive_beacon_entity"));

    public static final RegistryObject<EntityType<PaintBubbleEntity>> PAINT_BUBBLE_ENTITY =
            ENTITY_TYPES.register("paint_bubble_entity",
                    () -> EntityType.Builder.create(PaintBubbleEntity::new,
                                EntityClassification.MISC).size(4F, 4F)
                            .build("paint_bubble_entity"));

    public static final RegistryObject<EntityType<PaintBubbleEntity>> SUPER_PAINT_BUBBLE_ENTITY =
            ENTITY_TYPES.register("super_paint_bubble_entity",
                    () -> EntityType.Builder.create(SuperPaintBubbleEntity::new,
                                    EntityClassification.MISC).size(14F, 14F)
                                    .build("super_paint_bubble_entity"));

    public static final RegistryObject<EntityType<WatchBeaconEntity>> WATCH_BEACON_ENTITY =
            ENTITY_TYPES.register("watch_beacon_entity",
                        () -> EntityType.Builder.create(WatchBeaconEntity::new,
                                        EntityClassification.MISC).size(4F, 4F)
                            .build("watch_beacon_entity"));
}
