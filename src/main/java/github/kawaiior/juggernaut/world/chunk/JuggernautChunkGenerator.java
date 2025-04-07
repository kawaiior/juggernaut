package github.kawaiior.juggernaut.world.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import github.kawaiior.juggernaut.game.Constants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.function.Supplier;

public class JuggernautChunkGenerator extends NoiseChunkGenerator {

    // 这东西人能看懂？
    public static final Codec<NoiseChunkGenerator> CODEC =
            RecordCodecBuilder.create((p_236091_0_) -> p_236091_0_.group(
                    BiomeProvider.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeProvider),
                    Codec.LONG.fieldOf("seed").stable().forGetter(
                            (p_236093_0_) -> p_236093_0_.field_236084_w_
                    ),
                    DimensionSettings.DIMENSION_SETTINGS_CODEC.fieldOf("settings").forGetter(
                            (p_236090_0_) -> p_236090_0_.field_236080_h_)
                    ).apply(p_236091_0_, p_236091_0_.stable(JuggernautChunkGenerator::new))
            );

    public JuggernautChunkGenerator(BiomeProvider p_i241975_1_, long p_i241975_2_, Supplier<DimensionSettings> p_i241975_4_) {
        super(p_i241975_1_, p_i241975_2_, p_i241975_4_);
    }

    /**
     * fillFromNoise
     */
    @Override
    public void func_230352_b_(IWorld world, StructureManager p_230352_2_, IChunk chunk) {
        // Do Noting
    }

    @Override
    public void generateSurface(WorldGenRegion p_225551_1_, IChunk chunk) {
        // 生成游戏场地、游戏准备房间
        int x = chunk.getPos().x * 16;
        int z = chunk.getPos().z * 16;

        this.generateJuggernautSpawnHome(chunk);
        this.generatePlayground(x, z, chunk);
        this.generateReadyHome(x, z, chunk);
        this.generateSpawnHome(x, z, chunk);
    }

    private static final int JUGGERNAUT_CHUNK_X = Constants.GAME_PLAYGROUND_X_WIDTH / 32;
    private static final int JUGGERNAUT_CHUNK_Z = Constants.GAME_PLAYGROUND_Z_WIDTH / 32;
    private void generateJuggernautSpawnHome(IChunk chunk){
        if (chunk.getPos().x != JUGGERNAUT_CHUNK_X || chunk.getPos().z != JUGGERNAUT_CHUNK_Z) {
            return;
        }

        BlockPos chunkPos = chunk.getPos().asBlockPos().add(0, 65, 0);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                chunk.setBlockState(chunkPos.add(i, 0, j), Blocks.QUARTZ_BRICKS.getDefaultState(), false);
                chunk.setBlockState(chunkPos.add(i, 8, j), Blocks.QUARTZ_BRICKS.getDefaultState(), false);
            }
        }

        for (int i = 1; i < 8; i++) {
            chunk.setBlockState(chunkPos.add(0, i, 0), Blocks.QUARTZ_PILLAR.getDefaultState(), false);
            chunk.setBlockState(chunkPos.add(15, i, 0), Blocks.QUARTZ_PILLAR.getDefaultState(), false);
            chunk.setBlockState(chunkPos.add(0, i, 15), Blocks.QUARTZ_PILLAR.getDefaultState(), false);
            chunk.setBlockState(chunkPos.add(15, i, 15), Blocks.QUARTZ_PILLAR.getDefaultState(), false);
        }
    }

    private static final int SPAWN_HOME_START_X = Constants.SPAWN_HOME_POS.getX();
    private static final int SPAWN_HOME_START_Z = Constants.SPAWN_HOME_POS.getZ();
    private static final int SPAWN_HOME_END_X = SPAWN_HOME_START_X + Constants.SPAWN_HOME_X_WIDTH;
    private static final int SPAWN_HOME_END_Z = SPAWN_HOME_START_Z + Constants.SPAWN_HOME_Z_WIDTH;
    private static final int SPAWN_HOME_HEIGHT = 64;
    private static final BlockState SPAWN_HOME_EDGE_BLOCK = Blocks.GLASS.getDefaultState();
    private void generateSpawnHome(int x, int z, IChunk chunk) {
        if (x >= SPAWN_HOME_END_X || z >= SPAWN_HOME_END_Z || x < SPAWN_HOME_START_X || z < SPAWN_HOME_START_Z) {
            return;
        }

        // 玩家重生房间
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                chunk.setBlockState(
                        Constants.SPAWN_HOME_POS.add(x + i, 0, z + j),
                        Blocks.QUARTZ_BLOCK.getDefaultState(),
                        false
                );
            }
        }

        // 边缘
        if (x == SPAWN_HOME_START_X){
            for (int i = 0; i < 16; i++) {
                for (int j = 1; j < SPAWN_HOME_HEIGHT; j++) {
                    chunk.setBlockState(
                            Constants.SPAWN_HOME_POS.add(x, j, z + i),
                            SPAWN_HOME_EDGE_BLOCK,
                            false
                    );
                }
            }
        }

        if (z == SPAWN_HOME_START_Z){
            for (int i = 0; i < 16; i++)
                for (int j = 1; j < SPAWN_HOME_HEIGHT; j++)
                    chunk.setBlockState(
                            Constants.SPAWN_HOME_POS.add(x + i, j, z),
                            SPAWN_HOME_EDGE_BLOCK,
                            false
                    );
        }

        if (x == SPAWN_HOME_END_X - 16){
            if (z == SPAWN_HOME_END_Z - 16){
                // 这个区块是出生点的右上角
                return;
            }
            for (int i = 0; i < 16; i++)
                for (int j = 1; j < SPAWN_HOME_HEIGHT; j++)
                    chunk.setBlockState(
                            Constants.SPAWN_HOME_POS.add(x+15, j, z + i),
                            SPAWN_HOME_EDGE_BLOCK,
                            false
                    );
        }

        if (z == SPAWN_HOME_END_Z - 16){
            for (int i = 0; i < 16; i++)
                for (int j = 1; j < SPAWN_HOME_HEIGHT; j++)
                    chunk.setBlockState(
                            Constants.SPAWN_HOME_POS.add(x + i, j, z+15),
                            SPAWN_HOME_EDGE_BLOCK,
                            false
                    );
        }

    }

    private static final int GAME_PLAYGROUND_START_X = Constants.GAME_PLAYGROUND_POS.getX();
    private static final int GAME_PLAYGROUND_START_Z = Constants.GAME_PLAYGROUND_POS.getZ();
    private static final int GAME_PLAYGROUND_END_X = GAME_PLAYGROUND_START_X + Constants.GAME_PLAYGROUND_X_WIDTH;
    private static final int GAME_PLAYGROUND_END_Z = GAME_PLAYGROUND_START_Z + Constants.GAME_PLAYGROUND_Z_WIDTH;
    private static final int GAME_PLAYGROUND_HEIGHT = 64;
    private static final BlockState GAME_PLAYGROUND_EDGE_BLOCK = Blocks.GLASS.getDefaultState();
    private void generatePlayground(int x, int z, IChunk chunk) {
        if (x >= GAME_PLAYGROUND_END_X || z >= GAME_PLAYGROUND_END_Z || x < GAME_PLAYGROUND_START_X || z < GAME_PLAYGROUND_START_Z) {
            return;
        }

        // 游戏场地
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                chunk.setBlockState(
                        Constants.GAME_PLAYGROUND_POS.add(x + i, 0, z + j),
                        Blocks.QUARTZ_BLOCK.getDefaultState(),
                        false
                );
            }
        }

        // 边缘
        if (x == GAME_PLAYGROUND_START_X){
            if (z == GAME_PLAYGROUND_START_Z){
                // 这里为什么要return呢？因为这个区块要作为出生点到游戏广场的入口
                return;
            }

            for (int i = 0; i < 16; i++) {
                for (int j = 1; j < GAME_PLAYGROUND_HEIGHT; j++) {
                    chunk.setBlockState(
                            Constants.GAME_PLAYGROUND_POS.add(x, j, z + i),
                            GAME_PLAYGROUND_EDGE_BLOCK,
                            false
                    );
                }
            }
        }

        if (z == GAME_PLAYGROUND_START_Z){
            for (int i = 0; i < 16; i++) {
                for (int j = 1; j < GAME_PLAYGROUND_HEIGHT; j++) {
                    chunk.setBlockState(
                            Constants.GAME_PLAYGROUND_POS.add(x + i, j, z),
                            GAME_PLAYGROUND_EDGE_BLOCK,
                            false
                    );
                }
            }
        }

        if (x == GAME_PLAYGROUND_END_X - 16){
            for (int i = 0; i < 16; i++) {
                for (int j = 1; j < GAME_PLAYGROUND_HEIGHT; j++) {
                    chunk.setBlockState(
                            Constants.GAME_PLAYGROUND_POS.add(x + 15, j, z + i),
                            GAME_PLAYGROUND_EDGE_BLOCK,
                            false
                    );
                }
            }
        }

        if (z == GAME_PLAYGROUND_END_Z - 16){
            for (int i = 0; i < 16; i++) {
                for (int j = 1; j < GAME_PLAYGROUND_HEIGHT; j++) {
                    chunk.setBlockState(
                            Constants.GAME_PLAYGROUND_POS.add(x + i, j, z + 15),
                            GAME_PLAYGROUND_EDGE_BLOCK,
                            false
                    );
                }
            }
        }

    }

    private static final int GAME_READY_HOME_START_X = Constants.GAME_READY_HOME_POS.getX();
    private static final int GAME_READY_HOME_START_Z = Constants.GAME_READY_HOME_POS.getZ();
    private static final int GAME_READY_HOME_END_X = GAME_READY_HOME_START_X + Constants.GAME_READY_HOME_X_WIDTH;
    private static final int GAME_READY_HOME_END_Z = GAME_READY_HOME_START_Z + Constants.GAME_READY_HOME_Z_WIDTH;
    private static final int READY_HOME_HEIGHT = 64;
    private static final BlockState READY_HOME_EDGE_BLOCK = Blocks.GLASS.getDefaultState();
    private void generateReadyHome(int x, int z, IChunk chunk) {
        if (x >= GAME_READY_HOME_END_X || z >= GAME_READY_HOME_END_Z || x < GAME_READY_HOME_START_X || z < GAME_READY_HOME_START_Z) {
            return;
        }

        // 游戏准备房间
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                chunk.setBlockState(
                        Constants.GAME_READY_HOME_POS.add(x + i, 0, z + j),
                        Blocks.QUARTZ_BLOCK.getDefaultState(),
                        false
                );
            }
        }

        if (x == GAME_READY_HOME_START_X){
            for (int i = 0; i < 16; i++) {
                for (int j = 1; j < READY_HOME_HEIGHT; j++) {
                    chunk.setBlockState(
                            Constants.GAME_READY_HOME_POS.add(x, j, z + i),
                            READY_HOME_EDGE_BLOCK,
                            false
                    );
                }
            }
        }

        if (z == GAME_READY_HOME_START_Z){
            for (int i = 0; i < 16; i++) {
                for (int j = 1; j < READY_HOME_HEIGHT; j++) {
                    chunk.setBlockState(
                            Constants.GAME_READY_HOME_POS.add(x + i, j, z),
                            READY_HOME_EDGE_BLOCK,
                            false
                    );
                }
            }
        }

        if (x == GAME_READY_HOME_END_X - 16){
            for (int i = 0; i < 16; i++) {
                for (int j = 1; j < READY_HOME_HEIGHT; j++) {
                    chunk.setBlockState(
                            Constants.GAME_READY_HOME_POS.add(x + 15, j, z + i),
                            READY_HOME_EDGE_BLOCK,
                            false
                    );
                }
            }
        }

        if (z == GAME_READY_HOME_END_Z - 16){
            for (int i = 0; i < 16; i++) {
                for (int j = 1; j < READY_HOME_HEIGHT; j++) {
                    chunk.setBlockState(
                            Constants.GAME_READY_HOME_POS.add(x + i, j, z + 15),
                            READY_HOME_EDGE_BLOCK,
                            false
                    );
                }
            }
        }
    }

    @Override
    public void func_230351_a_(WorldGenRegion p_230351_1_, StructureManager p_230351_2_) {
        // Do Noting
    }

    @Override
    public void func_242707_a(DynamicRegistries p_242707_1_, StructureManager p_242707_2_, IChunk p_242707_3_, TemplateManager p_242707_4_, long p_242707_5_) {
        // Do Noting
    }

    @Override
    public void func_235953_a_(ISeedReader p_235953_1_, StructureManager p_235953_2_, IChunk p_235953_3_) {
        // Do Noting
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

}
