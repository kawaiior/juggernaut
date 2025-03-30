package github.kawaiior.juggernaut.init;

import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.item.JuggernautItem;
import github.kawaiior.juggernaut.item.JuggernautReadyItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Juggernaut.MOD_ID);

    public static final RegistryObject<Item> JUGGERNAUT_ITEM = ITEMS.register("juggernaut_item", JuggernautItem::new);
    public static final RegistryObject<Item> JUGGERNAUT_READY_ITEM = ITEMS.register("juggernaut_ready_item", JuggernautReadyItem::new);

}
