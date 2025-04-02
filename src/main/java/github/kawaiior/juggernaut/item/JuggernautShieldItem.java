package github.kawaiior.juggernaut.item;

import github.kawaiior.juggernaut.capability.ModCapability;
import github.kawaiior.juggernaut.capability.shield.ShieldPower;
import github.kawaiior.juggernaut.init.JuggernautItemGroup;
import github.kawaiior.juggernaut.network.NetworkRegistryHandler;
import github.kawaiior.juggernaut.network.packet.SyncShieldPacket;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

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
        LazyOptional<ShieldPower> capability = player.getCapability(ModCapability.SHIELD_POWER);
        capability.ifPresent((power) -> {
            power.setPlayerShield(power.getPlayerShield() + 20);
            // 向所有客户端发送 ShieldPower 数据
            NetworkRegistryHandler.INSTANCE.send(
                    PacketDistributor.ALL.with(() -> null),
                    new SyncShieldPacket(power.getPlayerShield(), power.getPlayerMaxShield(), player.getUniqueID())
            );
        });

        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent("右键获得20点护甲（可溢出）"));
    }
}
