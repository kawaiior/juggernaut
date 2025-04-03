package github.kawaiior.juggernaut.event;

import github.kawaiior.juggernaut.game.JuggernautServer;
import github.kawaiior.juggernaut.game.PlayerGameData;
import github.kawaiior.juggernaut.network.NetworkRegistryHandler;
import github.kawaiior.juggernaut.network.packet.GameStatusPacket;
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
import net.minecraftforge.fml.network.PacketDistributor;

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
        PlayerGameData gameData = new PlayerGameData(player.getScoreboardName());
        JuggernautServer juggernautServer = JuggernautServer.getInstance();
        juggernautServer.getGamePlayerMap().put(player, gameData);
        gameData.syncCardData(player);
        gameData.syncShieldData(player);

        int status = 0;
        long time = -1;
        if (juggernautServer.isStart()){
            status = 2;
            time = juggernautServer.getGameStartTime();
        } else if (juggernautServer.isReady()) {
            status = 1;
            time = juggernautServer.getGameReadyTime();
        }
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                new GameStatusPacket(status, time));
    }

    @SubscribeEvent
    public static void worldEvent(TickEvent.WorldTickEvent event){
        World world = event.world;
        if (world.isRemote || world.getDimensionKey() != ModDimensions.JUGGERNAUT_DIM || event.phase != TickEvent.Phase.END) {
            return;
        }

        JuggernautServer juggernautServer = JuggernautServer.getInstance();
        juggernautServer.tick((ServerWorld) world);

        // 每秒更新一次护甲
        if (world.getGameTime() % 20 != 0)  {
            return;
        }
        long now = System.currentTimeMillis();
        MinecraftServer server = world.getServer();
        for (ServerPlayerEntity player : server.getPlayerList().getPlayers()){
            PlayerGameData gameData = juggernautServer.getPlayerGameData(player);
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

        JuggernautServer juggernautServer = JuggernautServer.getInstance();
        if (!juggernautServer.isStart()){
            event.setAmount(0);
            event.setCanceled(true);
            return;
        }

        Entity attacker = event.getSource().getTrueSource();
        ServerPlayerEntity hurtPlayer = (ServerPlayerEntity) entity;
        if (attacker instanceof ServerPlayerEntity){
            juggernautServer.onPlayerHurt((ServerPlayerEntity) attacker, hurtPlayer, event.getAmount());
        }else {
            juggernautServer.onPlayerHurt(null, hurtPlayer, event.getAmount());
        }

        PlayerGameData gameData = juggernautServer.getPlayerGameData(hurtPlayer);
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

        // 在此维度的玩家不会真正死亡
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) entity;
        event.setCanceled(true);
        EntityUtil.addEffect(serverPlayer, Effects.RESISTANCE, 3, 3);
        EntityUtil.addEffect(serverPlayer, Effects.REGENERATION, 10, 1);

        JuggernautServer juggernautServer = JuggernautServer.getInstance();
        if (!juggernautServer.isStart()){
            return;
        }

        Entity killer = event.getSource().getTrueSource();
        if (killer instanceof ServerPlayerEntity){
            JuggernautServer.getInstance().onPlayerDeath((ServerPlayerEntity) killer, serverPlayer);
        }else {
            JuggernautServer.getInstance().onPlayerDeath(null, serverPlayer);
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
