package github.kawaiior.juggernaut.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nullable;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CapabilityRegistryHandler {

    public static  <T extends INBTSerializable<CompoundNBT>> void registryCapability(Class<T> type, T instance){
        CapabilityManager.INSTANCE.register(type,
                new Capability.IStorage<T>() {
                    @Nullable
                    @Override
                    public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
                        return instance.serializeNBT();
                    }
                    @Override
                    public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
                        instance.deserializeNBT((CompoundNBT) nbt);
                    }
                }, ()->instance);
    }

    public static void register(){
        registryCapability(ShieldPower.class, new ShieldPower());
    }

    @SubscribeEvent
    public static void onSetupEvent(FMLCommonSetupEvent event){
        event.enqueueWork(CapabilityRegistryHandler::register);
    }

    public static <T extends IReplicableCap> void handleCapOnPlayerClone(Capability<T> capability, PlayerEntity player, PlayerEntity original) {
        LazyOptional<T> cap = player.getCapability(capability);
        LazyOptional<T> originalCap = original.getCapability(capability);
        if (cap.isPresent() && originalCap.isPresent()){
            cap.ifPresent((c)->{
                originalCap.ifPresent(c::setPower);
            });
        }
    }

}
