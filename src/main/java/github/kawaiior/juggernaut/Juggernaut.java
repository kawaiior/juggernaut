package github.kawaiior.juggernaut;

import github.kawaiior.juggernaut.init.ItemInit;
import github.kawaiior.juggernaut.network.NetworkRegistryHandler;
import github.kawaiior.juggernaut.world.dimension.ModDimensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Juggernaut.MOD_ID)
public class Juggernaut
{
    public static final String MOD_ID = "juggernaut";
    public static final Logger LOGGER = LogManager.getLogger();

    public Juggernaut() {
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemInit.ITEMS.register(modbus);

        modbus.addListener(this::onCommonSetUp);
    }

    public void onCommonSetUp(FMLCommonSetupEvent event){
        NetworkRegistryHandler.registerMessage();

        event.enqueueWork(() -> {
            ModDimensions.register();
        });
    }

    public static void debug(Object o){
        LOGGER.debug(o);
    }

    public static void info(Object o){
        LOGGER.info(o);
    }

    public static void waring(Object o){
        LOGGER.warn(o);
    }

}
