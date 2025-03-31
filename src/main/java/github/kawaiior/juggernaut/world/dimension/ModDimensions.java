package github.kawaiior.juggernaut.world.dimension;

import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.world.chunk.JuggernautChunkGenerator;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ModDimensions {

    public static RegistryKey<World> JUGGERNAUT_DIM = RegistryKey.getOrCreateKey(Registry.WORLD_KEY,
            new ResourceLocation(Juggernaut.MOD_ID, "juggernaut_dim"));

    public static void register(){
        Registry.register(Registry.CHUNK_GENERATOR_CODEC,
                new ResourceLocation(Juggernaut.MOD_ID, "juggernaut_chunk"), JuggernautChunkGenerator.CODEC);
    }
}
