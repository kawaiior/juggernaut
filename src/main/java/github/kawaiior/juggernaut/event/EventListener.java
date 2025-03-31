package github.kawaiior.juggernaut.event;

import github.kawaiior.juggernaut.capability.ModCapability;
import github.kawaiior.juggernaut.capability.ShieldPower;
import github.kawaiior.juggernaut.game.JuggernautServer;
import github.kawaiior.juggernaut.network.NetworkRegistryHandler;
import github.kawaiior.juggernaut.network.packet.SyncShieldPacket;
import github.kawaiior.juggernaut.util.EntityUtil;
import github.kawaiior.juggernaut.world.dimension.ModDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class EventListener {

    private static final Map<UUID, Long> PLAYER_LAST_HURT_MAP = new HashMap<>();
    // 5秒
    private static final int RECOVER_SHIELD_TIME = 5000;

    @SubscribeEvent
    public static void worldEvent(TickEvent.WorldTickEvent event){
        World world = event.world;
        if (world.isRemote || world.getDimensionKey() != ModDimensions.JUGGERNAUT_DIM || event.phase != TickEvent.Phase.END) {
            return;
        }

        JuggernautServer.getInstance().tick((ServerWorld) world);

        // 每秒更新一次护甲
        if (world.getGameTime() % 20 != 0)  {
            return;
        }
        long now = System.currentTimeMillis();
        MinecraftServer server = world.getServer();
        for (ServerPlayerEntity player : server.getPlayerList().getPlayers()){
            long lastHurtTime = PLAYER_LAST_HURT_MAP.getOrDefault(player.getUniqueID(), 0L);
            if (now - lastHurtTime < RECOVER_SHIELD_TIME){
                continue;
            }
            // 恢复护甲
            LazyOptional<ShieldPower> capability = player.getCapability(ModCapability.SHIELD_POWER);
            capability.ifPresent((power) -> {
                if (power.getPlayerShield() < power.getPlayerMaxShield()){
                    power.setPlayerShield(power.getPlayerShield() + 1F);
                    // 网络发包
                    NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(() -> null),
                            new SyncShieldPacket(power.getPlayerShield(), power.getPlayerMaxShield(), player.getUniqueID()));
                }
            });
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

        // 更新hurt time
        PLAYER_LAST_HURT_MAP.put(hurtPlayer.getUniqueID(), System.currentTimeMillis());

        // 护甲机制
        LazyOptional<ShieldPower> capability = hurtPlayer.getCapability(ModCapability.SHIELD_POWER);
        capability.ifPresent((power) -> {
            float shield = power.getPlayerShield();
            if (shield <= 0){
                return;
            }
            if (shield > event.getAmount()){
                power.setPlayerShield(shield - event.getAmount());
                event.setAmount(0);
            }else {
                power.setPlayerShield(0);
                event.setAmount(event.getAmount() - shield);
            }
            // update
            NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(() -> null),
                    new SyncShieldPacket(power.getPlayerShield(), power.getPlayerMaxShield(), hurtPlayer.getUniqueID()));
        });
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

}
