package github.kawaiior.juggernaut.item;

import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.game.JuggernautServer;
import github.kawaiior.juggernaut.init.JuggernautItemGroup;
import github.kawaiior.juggernaut.world.dimension.JuggernautTeleporter;
import github.kawaiior.juggernaut.world.dimension.ModDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class JuggernautItem extends Item {
    public JuggernautItem() {
        super(new Properties().group(JuggernautItemGroup.JUGGERNAUT_GROUP));
    }

    /**
     * 玩家持有此物品右键点击时触发
     * 将玩家传送到 JUGGERNAUT_DIM 维度
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (world.isRemote){
            return super.onItemRightClick(world, player, hand);
        }

        MinecraftServer server = world.getServer();
        if (server == null){
            return super.onItemRightClick(world, player, hand);
        }

        ServerWorld serverWorld;
        boolean flag = false;
        if (world.getDimensionKey() == ModDimensions.JUGGERNAUT_DIM){
            serverWorld = server.getWorld(World.OVERWORLD);
        }else {
            serverWorld = server.getWorld(ModDimensions.JUGGERNAUT_DIM);
            flag = true;
        }

        if (serverWorld == null){
            return super.onItemRightClick(world, player, hand);
        }

        Juggernaut.debug("Teleporting player to " + serverWorld.getDimensionKey().getLocation());
        player.changeDimension(serverWorld, new JuggernautTeleporter(
                JuggernautServer.READY_HOME_POS,
                flag
        ));

        return super.onItemRightClick(world, player, hand);
    }

}
