package github.kawaiior.juggernaut.capability;

import github.kawaiior.juggernaut.capability.card.CardPower;
import github.kawaiior.juggernaut.capability.shield.ShieldPower;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class ModCapability {

    @CapabilityInject(ShieldPower.class)
    public static Capability<ShieldPower> SHIELD_POWER;

    @CapabilityInject(CardPower.class)
    public static Capability<CardPower> CARD_POWER;

}
