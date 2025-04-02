package github.kawaiior.juggernaut.capability;

import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.capability.card.CardPower;
import github.kawaiior.juggernaut.capability.card.CardPowerProvider;
import github.kawaiior.juggernaut.capability.shield.ShieldPower;
import github.kawaiior.juggernaut.capability.shield.ShieldPowerProvider;
import github.kawaiior.juggernaut.card.GameCard;
import github.kawaiior.juggernaut.game.JuggernautServer;
import github.kawaiior.juggernaut.network.NetworkRegistryHandler;
import github.kawaiior.juggernaut.network.packet.GameStatusPacket;
import github.kawaiior.juggernaut.network.packet.SyncAllPlayerShieldPacket;
import github.kawaiior.juggernaut.network.packet.SyncShieldPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class CapabilityEvent {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event){
        PlayerEntity player = event.getPlayer();
        PlayerEntity original = event.getOriginal();
        CapabilityRegistryHandler.handleCapOnPlayerClone(ModCapability.SHIELD_POWER, player, original);
        CapabilityRegistryHandler.handleCapOnPlayerClone(ModCapability.CARD_POWER, player, original);
    }

    @SubscribeEvent
    public static void onAttachEntityCapabilityEvent(AttachCapabilitiesEvent<Entity> event){
        // 实体能力
        Entity entity = event.getObject();
        if (entity instanceof PlayerEntity){
            Juggernaut.debug("为玩家添加Capability");
            event.addCapability(new ResourceLocation(Juggernaut.MOD_ID,"shield_power"), new ShieldPowerProvider());
            event.addCapability(new ResourceLocation(Juggernaut.MOD_ID,"card_power"), new CardPowerProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinWorldEvent event){
        Entity entity = event.getEntity();
        if (event.getWorld().isRemote || !(entity instanceof PlayerEntity)){
            return;
        }
        PlayerEntity player = (PlayerEntity) entity;
        LazyOptional<ShieldPower> capability = player.getCapability(ModCapability.SHIELD_POWER);
        capability.ifPresent((power) -> {
            // 向所有客户端发送 ShieldPower 数据
            NetworkRegistryHandler.INSTANCE.send(PacketDistributor.ALL.with(() -> null),
                    new SyncShieldPacket(power.getPlayerShield(), power.getPlayerMaxShield(), player.getUniqueID())
            );
        });

        // 向 player 发送所有 ShieldPower 数据
        MinecraftServer server = player.getServer();
        if (server == null){
            return;
        }
        List<ShieldPower.PlayerShieldData> playerShieldData = new ArrayList<>();
        for (ServerPlayerEntity serverPlayer : server.getPlayerList().getPlayers()){
            LazyOptional<ShieldPower> cap = serverPlayer.getCapability(ModCapability.SHIELD_POWER);
            cap.ifPresent((power) -> {
                playerShieldData.add(new ShieldPower.PlayerShieldData(
                        power.getPlayerShield(), power.getPlayerMaxShield(), serverPlayer.getUniqueID()
                ));
            });
        }
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                new SyncAllPlayerShieldPacket(playerShieldData));

        // 向玩家发送 card 数据
        CardPower.sendCardData((ServerPlayerEntity) player);

        // 向玩家发送游戏状态
        // TODO: 把这些操作独立出来
        int status = 0;
        long time = -1;
        if (JuggernautServer.getInstance().isStart()){
            status = 2;
            time = JuggernautServer.getInstance().getGameStartTime();
        } else if (JuggernautServer.getInstance().isReady()) {
            status = 1;
            time = JuggernautServer.getInstance().getGameReadyTime();
        }
        NetworkRegistryHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                new GameStatusPacket(status, time));
    }

}
