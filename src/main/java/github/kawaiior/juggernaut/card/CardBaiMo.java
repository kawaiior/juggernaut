package github.kawaiior.juggernaut.card;

import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.entity.ReviveBeaconEntity;
import github.kawaiior.juggernaut.init.ModSounds;
import github.kawaiior.juggernaut.network.NetworkRegistryHandler;
import github.kawaiior.juggernaut.network.packet.PlaySoundPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class CardBaiMo extends GameCard{

    private static final List<SoundEvent> SKILL_SOUNDS = new ArrayList<>();
    private static final List<SoundEvent> ULTIMATE_SOUNDS = new ArrayList<>();

    static {
        SKILL_SOUNDS.add(ModSounds.baimoSkill0);
        SKILL_SOUNDS.add(ModSounds.baimoSkill1);
        SKILL_SOUNDS.add(ModSounds.baimoSkill2);

        ULTIMATE_SOUNDS.add(ModSounds.baimoUSkill0);
        ULTIMATE_SOUNDS.add(ModSounds.baimoUSkill1);
        ULTIMATE_SOUNDS.add(ModSounds.baimoUSkill2);
        ULTIMATE_SOUNDS.add(ModSounds.baimoUSkill3);
        ULTIMATE_SOUNDS.add(ModSounds.baimoUSkill4);
    }

    @Override
    public void playerUseSkill(ServerPlayerEntity player, ServerPlayerEntity target) {
        // 播放音效
        // player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SKILL_SOUNDS.get(player.world.rand.nextInt(SKILL_SOUNDS.size())), player.getSoundCategory(), 1.0F, 1.0F);
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(() -> null),
                new PlaySoundPacket(
                        SKILL_SOUNDS.get(player.world.rand.nextInt(SKILL_SOUNDS.size())),
                        SoundCategory.MUSIC
                )
        );

        Vector3d velocity = new Vector3d(player.getLookVec().x * 10.0, player.getLookVec().y * 0.2, player.getLookVec().z * 10.0);
        player.setMotion(velocity);
        player.velocityChanged = true; // 强制同步速度到客户端
    }

    @Override
    public void playerUseUltimateSkill(ServerPlayerEntity player, ServerPlayerEntity target) {
        // 播放音效
        // player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), ULTIMATE_SOUNDS.get(player.world.rand.nextInt(ULTIMATE_SOUNDS.size())), player.getSoundCategory(), 1.0F, 1.0F);
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(() -> null),
                new PlaySoundPacket(
                        ULTIMATE_SOUNDS.get(player.world.rand.nextInt(ULTIMATE_SOUNDS.size())),
                        SoundCategory.MUSIC
                )
        );

        ReviveBeaconEntity entity = ReviveBeaconEntity.create(player);
        entity.setPosition(player.getPosX(), player.getPosY()+0.1D, player.getPosZ());
        player.world.addEntity(entity);
    }

    @Override
    public void cardTick(PlayerEntity player) {

    }

    @Override
    public boolean onPlayerDeath(PlayerEntity player) {
        ReviveBeaconEntity entity = ReviveBeaconEntity.getPlayerReviveBeacon(player);
        if (entity != null){
            entity.removeFromMap();
            entity.setBeaconDead();
            player.moveForced(entity.getPositionVec());
            player.setHealth(player.getMaxHealth());
            return true;
        }
        return false;
    }

    @Override
    public void reset(PlayerEntity player) {
        ReviveBeaconEntity entity = ReviveBeaconEntity.getPlayerReviveBeacon(player);
        if (entity != null){
            entity.removeFromMap();
            entity.setBeaconDead();
        }
    }
}
