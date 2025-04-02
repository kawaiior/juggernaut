package github.kawaiior.juggernaut.network.packet;


import github.kawaiior.juggernaut.capability.ModCapability;
import github.kawaiior.juggernaut.capability.card.CardPower;
import github.kawaiior.juggernaut.card.GameCard;
import github.kawaiior.juggernaut.card.GameCardInit;
import github.kawaiior.juggernaut.game.JuggernautServer;
import github.kawaiior.juggernaut.network.NetworkRegistryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class PlayerSelectCardPacket {

    private final int cardId;

    public PlayerSelectCardPacket(int cardId)  {
        this.cardId = cardId;
    }

    public static void encode(PlayerSelectCardPacket packet, PacketBuffer buffer) {
        buffer.writeInt(packet.cardId);
    }

    public static PlayerSelectCardPacket decode(PacketBuffer buffer){
        return new PlayerSelectCardPacket(buffer.readInt());
    }

    public static void handlePacket(PlayerSelectCardPacket packet, Supplier<NetworkEvent.Context> content){
        NetworkEvent.Context context = content.get();
        context.enqueueWork(()->{
            if (context.getDirection().getReceptionSide().isClient()) {
                onClientCustomPack(packet, context);
            }
            if (context.getDirection().getReceptionSide().isServer()){
                onServerCustomPack(packet, context);
            }
        });
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onClientCustomPack(PlayerSelectCardPacket packet, NetworkEvent.Context context){
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null){
            return;
        }

        String message;
        GameCard card = GameCardInit.getGameCardById(packet.cardId);
        if (card == null){
            message = "切换角色失败";
        }else {
            message = "已切换角色为: " + card.getCardTranslationName().getString();
        }

        player.sendMessage(new StringTextComponent(message), player.getUniqueID());
    }

    public static void onServerCustomPack(PlayerSelectCardPacket packet, NetworkEvent.Context context){
        GameCard card = GameCardInit.getGameCardById(packet.cardId);
        ServerPlayerEntity player = context.getSender();
        if (player == null){
            return;
        }

        if (JuggernautServer.getInstance().isStart()){
            // TODO: 游戏中切换角色后，玩家复活时生效

            // 游戏中无法切换角色
            player.sendStatusMessage(new StringTextComponent("游戏中无法切换角色"), false);
            return;
        }

        if (card == null){
            // 角色不存在
            player.sendStatusMessage(new StringTextComponent("角色不存在"), false);
            return;
        }

        LazyOptional<CardPower> capability = player.getCapability(ModCapability.CARD_POWER);
        capability.ifPresent((power) -> {
            power.setCardId(card.getCardId());
            power.reset();
            NetworkRegistryHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    new PlayerSelectCardPacket(card.getCardId()));
            CardPower.sendCardData(player, power);
        });
    }
}
