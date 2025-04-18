package github.kawaiior.juggernaut.card;

import github.kawaiior.juggernaut.game.GameServer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class CardFragrans extends GameCard{

    @Override
    public void playerUseSkill(ServerPlayerEntity player, ServerPlayerEntity target) {
        // 给附近玩家添加生命回复效果
        // 遍历游戏内的玩家
        GameServer.getInstance().getGamePlayerMap().forEach((thatPlayer, gameData) -> {
            if (player.equals(thatPlayer)){
                player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 20 * 20, 0));
            } else if (player.getDistance(thatPlayer) <= 10 && !gameData.getBoardData().juggernaut)  {
                thatPlayer.addPotionEffect(new EffectInstance(Effects.REGENERATION, 20 * 20, 0));
            }
        });
    }

    @Override
    public void playerUseUltimateSkill(ServerPlayerEntity player, ServerPlayerEntity target) {

    }

    @Override
    public void cardTick(PlayerEntity player) {

    }
}
