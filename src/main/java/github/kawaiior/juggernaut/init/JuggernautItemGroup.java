package github.kawaiior.juggernaut.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class JuggernautItemGroup extends ItemGroup {

    public static final JuggernautItemGroup JUGGERNAUT_GROUP = new JuggernautItemGroup();

    public JuggernautItemGroup() {
        super("juggernaut_group");
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Items.DIAMOND);
    }
}
