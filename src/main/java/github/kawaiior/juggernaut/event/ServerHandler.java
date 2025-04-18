package github.kawaiior.juggernaut.event;

import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.game.GameServer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.util.concurrent.*;

@Mod.EventBusSubscriber(modid = Juggernaut.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerHandler {

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event) {
        MinecraftServer server = event.getServer(); // 直接获取服务端实例
        GameServer.getInstance().setServer(server);

        GameServer gameServer = GameServer.getInstance();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(gameServer::tick, 0, 50, TimeUnit.MILLISECONDS);
    }
}
