package github.kawaiior.juggernaut.network;

import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.network.packet.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkRegistryHandler {
    public static SimpleChannel INSTANCE;
    private static final String VERSION = "0.1.0";
    private static int ID = 0;
    public static int nextID(){
        return ID++;
    }

    public static void registerMessage(){
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Juggernaut.MOD_ID,"power"),
                ()->VERSION,
                (version)-> version.equals(VERSION),
                (version)->version.equals(VERSION));

        INSTANCE.registerMessage(nextID(), DeathBoardMsgPacket.class, DeathBoardMsgPacket::encode,
                DeathBoardMsgPacket::decode, DeathBoardMsgPacket::handlePacket);
        INSTANCE.registerMessage(nextID(), SyncShieldPacket.class, SyncShieldPacket::encode,
                SyncShieldPacket::decode, SyncShieldPacket::handlePacket);
        INSTANCE.registerMessage(nextID(), SyncCardDataPacket.class, SyncCardDataPacket::encode,
                SyncCardDataPacket::decode, SyncCardDataPacket::handlePacket);
        INSTANCE.registerMessage(nextID(), PlayerSelectCardPacket.class, PlayerSelectCardPacket::encode,
                PlayerSelectCardPacket::decode, PlayerSelectCardPacket::handlePacket);
        INSTANCE.registerMessage(nextID(), GameStatusPacket.class, GameStatusPacket::encode,
                GameStatusPacket::decode, GameStatusPacket::handlePacket);

//        INSTANCE.registerMessage(nextID(), PlayerLeftClickPacket.class, PlayerLeftClickPacket::encode,
//                PlayerLeftClickPacket::decode, PlayerLeftClickPacket::handlePacket);
    }

}
