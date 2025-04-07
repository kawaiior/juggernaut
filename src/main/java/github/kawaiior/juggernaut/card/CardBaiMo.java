package github.kawaiior.juggernaut.card;

import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.entity.ReviveBeaconEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

public class CardBaiMo extends GameCard{

    @Override
    public void playerUseSkill(PlayerEntity player, PlayerEntity target) {
        Juggernaut.debug("玩家使用白墨技能");
        Vector3d velocity = new Vector3d(player.getLookVec().x * 10.0, player.getLookVec().y * 0.2, player.getLookVec().z * 10.0);
        player.setMotion(velocity);
        player.velocityChanged = true; // 强制同步速度到客户端
    }

    @Override
    public void playerUseUltimateSkill(PlayerEntity player, PlayerEntity target) {
        ReviveBeaconEntity entity = ReviveBeaconEntity.create(player);
        entity.setPosition(player.getPosX(), player.getPosY(), player.getPosZ());
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
