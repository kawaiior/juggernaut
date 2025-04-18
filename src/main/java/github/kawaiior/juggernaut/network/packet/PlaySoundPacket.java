package github.kawaiior.juggernaut.network.packet;


import github.kawaiior.juggernaut.sound.SkillAudio;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PlaySoundPacket {
    private final SoundEvent sound;
    private final SoundCategory category;

    public PlaySoundPacket(SoundEvent sound, SoundCategory category) {
        this.sound = sound;
        this.category = category;
    }

    public static void encode(PlaySoundPacket packet, PacketBuffer buffer) {
        buffer.writeVarInt(Registry.SOUND_EVENT.getId(packet.sound));
        buffer.writeEnumValue(packet.category);
    }

    public static PlaySoundPacket decode(PacketBuffer buffer){
        return new PlaySoundPacket(
                Registry.SOUND_EVENT.getByValue(buffer.readVarInt()),
                buffer.readEnumValue(SoundCategory.class)
        );
    }

    public static void handlePacket(PlaySoundPacket packet, Supplier<NetworkEvent.Context> content){
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
    public static void onClientCustomPack(PlaySoundPacket packet, NetworkEvent.Context context){
        Minecraft.getInstance().getSoundHandler().play(new SkillAudio(packet.sound, packet.category));
    }

    public static void onServerCustomPack(PlaySoundPacket packet, NetworkEvent.Context context){
        // 服务端不会收到这个包
    }
}
