package github.kawaiior.juggernaut.card;

import github.kawaiior.juggernaut.Juggernaut;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

public class CardBaiMo extends GameCard{

    @Override
    public void playerUseSkill(PlayerEntity player, PlayerEntity target) {
        Juggernaut.debug("玩家使用白墨技能");

        Vector3d velocity = new Vector3d(player.getLookVec().x * 10.0, player.getLookVec().y * 0.2, player.getLookVec().z * 10.0);
        Juggernaut.debug(velocity);

        player.setMotion(velocity);
        player.velocityChanged = true; // 强制同步速度到客户端
    }

    @Override
    public void playerUseUltimateSkill(PlayerEntity player, PlayerEntity target) {

    }

    @Override
    public void cardTick(PlayerEntity player) {

    }
}
