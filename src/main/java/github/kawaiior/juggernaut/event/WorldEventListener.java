package github.kawaiior.juggernaut.event;

import github.kawaiior.juggernaut.game.JuggernautServer;
import github.kawaiior.juggernaut.util.EntityUtil;
import github.kawaiior.juggernaut.world.dimension.ModDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class WorldEventListener {

    @SubscribeEvent
    public static void worldEvent(TickEvent.WorldTickEvent event){
        World world = event.world;
        if (world.isRemote || world.getDimensionKey() != ModDimensions.JUGGERNAUT_DIM) {
            return;
        }

        JuggernautServer.getInstance().tick((ServerWorld) world);
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
        if (attacker instanceof ServerPlayerEntity){
            juggernautServer.onPlayerHurt((ServerPlayerEntity) attacker, (ServerPlayerEntity) entity, event.getAmount());
        }else {
            juggernautServer.onPlayerHurt(null, (ServerPlayerEntity) entity, event.getAmount());
        }
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
