package github.kawaiior.juggernaut.item;

import github.kawaiior.juggernaut.game.GameServer;
import github.kawaiior.juggernaut.init.JuggernautItemGroup;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class JuggernautReadyItem extends Item {
    public JuggernautReadyItem() {
        super(new Properties().group(JuggernautItemGroup.JUGGERNAUT_GROUP));
    }

    /**
     * 玩家持有此物品右键时触发
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (world.isRemote){
            return super.onItemRightClick(world, player, hand);
        }

        // 游戏开始
        GameServer gameServer = GameServer.getInstance();
        if (gameServer.getGameState() != GameServer.GameState.PREPARE){
            gameServer.gamePrepare();
        }

        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent("右键进入游戏准备阶段"));
    }
}
