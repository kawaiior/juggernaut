package github.kawaiior.juggernaut.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class ModCapability {

    @CapabilityInject(ShieldPower.class)
    public static Capability<ShieldPower> SHIELD_POWER;

}
