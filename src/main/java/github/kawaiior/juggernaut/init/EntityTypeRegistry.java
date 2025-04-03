package github.kawaiior.juggernaut.init;

import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.entity.ReviveBeaconEntity;
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


}
