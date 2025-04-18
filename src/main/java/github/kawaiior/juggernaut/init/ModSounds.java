package github.kawaiior.juggernaut.init;

import github.kawaiior.juggernaut.Juggernaut;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModSounds {

    public static final SoundEvent babylonAttack = makeSoundEvent("babylon_attack");
    public static final SoundEvent babylonSpawn = makeSoundEvent("babylon_spawn");

    public static final SoundEvent baimoSkill0 = makeSoundEvent("baimo_skill_0");
    public static final SoundEvent baimoSkill1 = makeSoundEvent("baimo_skill_1");
    public static final SoundEvent baimoSkill2 = makeSoundEvent("baimo_skill_2");
    public static final SoundEvent baimoUSkill0 = makeSoundEvent("baimo_u_0");
    public static final SoundEvent baimoUSkill1 = makeSoundEvent("baimo_u_1");
    public static final SoundEvent baimoUSkill2 = makeSoundEvent("baimo_u_2");
    public static final SoundEvent baimoUSkill3 = makeSoundEvent("baimo_u_3");
    public static final SoundEvent baimoUSkill4 = makeSoundEvent("baimo_u_4");

    private static SoundEvent makeSoundEvent(String name) {
        ResourceLocation loc = prefix(name);
        return new SoundEvent(loc).setRegistryName(loc);
    }

    public static ResourceLocation prefix(String path) {
        return new ResourceLocation(Juggernaut.MOD_ID, path);
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> evt) {
        IForgeRegistry<SoundEvent> r = evt.getRegistry();
        r.register(babylonAttack);
        r.register(babylonSpawn);

        r.register(baimoSkill0);
        r.register(baimoSkill1);
        r.register(baimoSkill2);

        r.register(baimoUSkill0);
        r.register(baimoUSkill1);
        r.register(baimoUSkill2);
        r.register(baimoUSkill3);
        r.register(baimoUSkill4);
    }
}
