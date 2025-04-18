package github.kawaiior.juggernaut.event;

import github.kawaiior.juggernaut.card.GameCard;
import github.kawaiior.juggernaut.game.GameData;
import github.kawaiior.juggernaut.game.GameServer;
import github.kawaiior.juggernaut.util.EntityUtil;
import github.kawaiior.juggernaut.util.JuggernautUtil;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventListener {

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (event.getWorld().isRemote || !(entity instanceof PlayerEntity)) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        JuggernautUtil.setUUIDServerPlayerEntityMap(player);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity){
            GameServer.getInstance().onPlayerLoggedIn((ServerPlayerEntity) player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event){
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity){
            GameServer.getInstance().onPlayerLoggedOut((ServerPlayerEntity) player);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event){
        Entity entity = event.getEntity();
        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        World world = entity.world;
        if (world.isRemote) {
            return;
        }

        GameServer gameServer = GameServer.getInstance();
        if (gameServer.getGameState() != GameServer.GameState.START){
            event.setAmount(0);
            event.setCanceled(true);
            return;
        }

        Entity attacker = event.getSource().getTrueSource();
        ServerPlayerEntity hurtPlayer = (ServerPlayerEntity) entity;
        if (attacker instanceof ServerPlayerEntity){
            gameServer.onPlayerHurt((ServerPlayerEntity) attacker, hurtPlayer, event.getAmount());
        }else {
            gameServer.onPlayerHurt(null, hurtPlayer, event.getAmount());
        }

        GameData gameData = gameServer.getPlayerGameData(hurtPlayer);
        if (gameData == null){
            return;
        }

        // 更新hurt time
        gameData.getBoardData().lastHurtTime = System.currentTimeMillis();

        // 护甲机制
        float amount = event.getAmount();
        // 先消耗临时护甲
        if (amount < gameData.getShieldData().temporaryShield){
            gameData.getShieldData().temporaryShield -= amount;
            event.setAmount(0);
        }else {
            amount = amount - gameData.getShieldData().temporaryShield;
            gameData.getShieldData().temporaryShield = 0;
            if (amount < gameData.getShieldData().shield){
                gameData.getShieldData().shield -= amount;
                event.setAmount(0);
            }else {
                event.setAmount(amount - gameData.getShieldData().shield);
                gameData.getShieldData().shield = 0;
            }
        }

        // sync
        gameData.getShieldData().syncData(hurtPlayer);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event){
        Entity entity = event.getEntity();
        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        World world = entity.world;
        if (world.isRemote) {
            return;
        }

        GameServer gameServer = GameServer.getInstance();
        if (gameServer.getGameState() != GameServer.GameState.START){
            return;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) entity;

        // 检查死亡是否被取消
        GameData data = gameServer.getPlayerGameData(serverPlayer);
        GameCard card = data.getCardData().getCard(serverPlayer);
        boolean flag = card.onPlayerDeath(serverPlayer);
        if (flag){
            // 取消死亡
            EntityUtil.addEffect(serverPlayer, Effects.RESISTANCE, 2, 3);
            event.setCanceled(true);
            return;
        }

        // 在此维度的玩家不会真正死亡
        event.setCanceled(true);
        EntityUtil.addEffect(serverPlayer, Effects.RESISTANCE, 3, 3);
        EntityUtil.addEffect(serverPlayer, Effects.REGENERATION, 10, 1);

        Entity killer = event.getSource().getTrueSource();
        if (killer instanceof ServerPlayerEntity){
            gameServer.onPlayerDeath((ServerPlayerEntity) killer, serverPlayer);
        }else {
            gameServer.onPlayerDeath(null, serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerBreakBlock(BlockEvent.BreakEvent event){
        World world = event.getPlayer().world;
        if (world.isRemote() || event.getPlayer().isCreative()){
            return;
        }

        if (event.getState().getMaterial() == Material.WOOL){
            // 可以破坏羊毛
            return;
        }

        // 防止破坏
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event){
        World world = event.getWorld();
        if (world.isRemote){
            return;
        }

        // 去除 Material != Material.WOOL 的方块
        event.getAffectedBlocks().removeIf((pos) -> world.getBlockState(pos).getMaterial() != Material.WOOL);
    }


}
