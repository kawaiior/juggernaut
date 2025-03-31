package github.kawaiior.juggernaut.world.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;

import java.util.function.Supplier;

public class JuggernautChunkGenerator extends NoiseChunkGenerator {

    public static final Codec<NoiseChunkGenerator> CODEC = RecordCodecBuilder.create((p_236091_0_) -> p_236091_0_.group(
            BiomeProvider.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeProvider),
            Codec.LONG.fieldOf("seed").stable().forGetter((p_236093_0_) -> p_236093_0_.field_236084_w_),
            DimensionSettings.DIMENSION_SETTINGS_CODEC.fieldOf("settings")
                    .forGetter((p_236090_0_) -> p_236090_0_.field_236080_h_))
            .apply(p_236091_0_, p_236091_0_.stable(JuggernautChunkGenerator::new)));

    public JuggernautChunkGenerator(BiomeProvider p_i241975_1_, long p_i241975_2_, Supplier<DimensionSettings> p_i241975_4_) {
        super(p_i241975_1_, p_i241975_2_, p_i241975_4_);
    }

    @Override
    public void func_230352_b_(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_) {
        // Do Noting
    }

    @Override
    public void generateSurface(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {
        // Do Noting
    }


}
