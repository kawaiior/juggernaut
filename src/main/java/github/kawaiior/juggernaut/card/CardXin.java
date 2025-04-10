package github.kawaiior.juggernaut.card;

import github.kawaiior.juggernaut.entity.SuperWatchBeaconEntity;
import github.kawaiior.juggernaut.entity.WatchBeaconEntity;
import github.kawaiior.juggernaut.init.EntityTypeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class CardXin extends GameCard{

    @Override
    public void playerUseSkill(ServerPlayerEntity player, ServerPlayerEntity target) {
        WatchBeaconEntity beacon = new WatchBeaconEntity(EntityTypeRegistry.WATCH_BEACON_ENTITY.get(), player.world);
        beacon.setPosition(player.getPosX(), player.getPosY()+0.1D, player.getPosZ());
        beacon.setOwner(player);
        player.world.addEntity(beacon);
    }

    @Override
    public void playerUseUltimateSkill(ServerPlayerEntity player, ServerPlayerEntity target) {
        SuperWatchBeaconEntity beacon = new SuperWatchBeaconEntity(EntityTypeRegistry.SUPER_WATCH_BEACON_ENTITY.get(), player.world);
        beacon.setPosition(player.getPosX(), player.getPosY()+0.1D, player.getPosZ());
        beacon.setOwner(player);
        player.world.addEntity(beacon);
    }

    @Override
    public void cardTick(PlayerEntity player) {

    }
}
