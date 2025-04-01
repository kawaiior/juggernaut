package github.kawaiior.juggernaut.event;

import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.render.gui.SelectCardGui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class KeyBoardInput {

    public static final KeyBinding OPEN_SELECT_CARD_GUI = new KeyBinding("key.open_select_card_gui",
            KeyConflictContext.IN_GAME,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.category." + Juggernaut.MOD_ID);

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent event) {
        if (OPEN_SELECT_CARD_GUI.isPressed()) {
            DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> SelectCardGui.OpenSelectCardGui::new);
        }
    }

}
