package github.kawaiior.juggernaut.item;

import github.kawaiior.juggernaut.game.JuggernautServer;
import github.kawaiior.juggernaut.init.JuggernautItemGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class JuggernautReadyItem extends Item {
    public JuggernautReadyItem() {
        super(new Properties().group(JuggernautItemGroup.JUGGERNAUT_GROUP));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (world.isRemote){
            return super.onItemRightClick(world, player, hand);
        }

        // 游戏开始
        JuggernautServer juggernautServer = JuggernautServer.getInstance();
        if (!juggernautServer.isStart()){
            juggernautServer.gameStart();
        }

        return super.onItemRightClick(world, player, hand);
    }
}
