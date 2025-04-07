package github.kawaiior.juggernaut.event;

import github.kawaiior.juggernaut.card.GameCard;
import github.kawaiior.juggernaut.game.GameServer;
import github.kawaiior.juggernaut.game.PlayerGameData;
import github.kawaiior.juggernaut.util.EntityUtil;
import github.kawaiior.juggernaut.util.JuggernautUtil;
import github.kawaiior.juggernaut.world.dimension.ModDimensions;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
    public static void worldEvent(TickEvent.WorldTickEvent event){
        World world = event.world;
        if (world.isRemote || world.getDimensionKey() != ModDimensions.JUGGERNAUT_DIM || event.phase != TickEvent.Phase.END) {
            return;
        }

        GameServer gameServer = GameServer.getInstance();
        gameServer.tick((ServerWorld) world);

        // 每秒更新一次护甲
        if (world.getGameTime() % 20 != 0)  {
            return;
        }
        long now = System.currentTimeMillis();
        MinecraftServer server = world.getServer();
        for (ServerPlayerEntity player : server.getPlayerList().getPlayers()){
            PlayerGameData gameData = gameServer.getPlayerGameData(player);
            if (gameData == null){
                continue;
            }
            gameData.shieldTick(player, now);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event){
        Entity entity = event.getEntity();
        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        World world = entity.world;
        if (world.isRemote || world.getDimensionKey() != ModDimensions.JUGGERNAUT_DIM) {
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

        PlayerGameData gameData = gameServer.getPlayerGameData(hurtPlayer);
        if (gameData == null){
            return;
        }

        // 更新hurt time
        gameData.setLastHurtTime(System.currentTimeMillis());

        // 护甲机制
        float amount = event.getAmount();
        // 先消耗临时护甲
        if (amount < gameData.getTemporaryShield()){
            gameData.setTemporaryShield(gameData.getTemporaryShield() - amount);
            event.setAmount(0);
        }else {
            amount = amount - gameData.getTemporaryShield();
            gameData.setTemporaryShield(0);
            float shield = gameData.getShield();
            if (amount < shield){
                gameData.setShield(shield - amount);
                event.setAmount(0);
            }else {
                gameData.setShield(0);
                event.setAmount(amount - shield);
            }
        }

        // sync
        gameData.syncShieldData(hurtPlayer);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeath(LivingDeathEvent event){
        Entity entity = event.getEntity();
        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        World world = entity.world;
        if (world.isRemote || world.getDimensionKey() != ModDimensions.JUGGERNAUT_DIM) {
            return;
        }

        GameServer gameServer = GameServer.getInstance();
        if (gameServer.getGameState() != GameServer.GameState.START){
            return;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) entity;

        // 检查死亡是否被取消
        PlayerGameData data = gameServer.getPlayerGameData(serverPlayer);
        GameCard card = data.getCard(serverPlayer);
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
        if (world.isRemote() || event.getPlayer().isCreative() || world.getDimensionKey() != ModDimensions.JUGGERNAUT_DIM){
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
        if (world.isRemote || world.getDimensionKey() != ModDimensions.JUGGERNAUT_DIM){
            return;
        }

        // 去除 Material != Material.WOOL 的方块
        event.getAffectedBlocks().removeIf((pos) -> world.getBlockState(pos).getMaterial() != Material.WOOL);
    }

}
