package github.kawaiior.juggernaut.item;

import github.kawaiior.juggernaut.game.JuggernautServer;
import github.kawaiior.juggernaut.game.PlayerGameData;
import github.kawaiior.juggernaut.init.JuggernautItemGroup;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class JuggernautShieldItem extends Item {
    public JuggernautShieldItem() {
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

        // 护甲+20
        PlayerGameData gameData = JuggernautServer.getInstance().getPlayerGameData((ServerPlayerEntity) player);
        if (gameData == null){
            return super.onItemRightClick(world, player, hand);
        }

        gameData.setTemporaryShield(gameData.getTemporaryShield() + 20F);
        gameData.syncShieldData((ServerPlayerEntity) player);

        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent("右键获得20点护甲（可溢出）"));
    }
}
