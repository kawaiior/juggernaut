package github.kawaiior.juggernaut.init;

import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.card.GameCard;
import github.kawaiior.juggernaut.game.JuggernautClient;
import github.kawaiior.juggernaut.game.PlayerGameData;
import github.kawaiior.juggernaut.network.NetworkRegistryHandler;
import github.kawaiior.juggernaut.network.packet.PlayerSelectCardPacket;
import github.kawaiior.juggernaut.network.packet.PlayerUseSkillRequestPacket;
import github.kawaiior.juggernaut.render.gui.SelectCardGui;
import github.kawaiior.juggernaut.render.hud.GameDataRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class KeyBoardInput {

    public static final KeyBinding OPEN_SELECT_CARD_GUI = new KeyBinding("key.open_select_card_gui",
            KeyConflictContext.IN_GAME,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.category." + Juggernaut.MOD_ID);

    public static final KeyBinding PLAYER_USE_SKILL = new KeyBinding("key.player_use_skill",
            KeyConflictContext.IN_GAME,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "key.category." + Juggernaut.MOD_ID);

    public static final KeyBinding PLAYER_USE_ULTIMATE_SKILL = new KeyBinding("key.player_use_ultimate_skill",
            KeyConflictContext.IN_GAME,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            "key.category." + Juggernaut.MOD_ID);

    public static final KeyBinding PLAYER_LOOK_GAME_DATA = new KeyBinding("key.player_look_game_data",
            KeyConflictContext.IN_GAME,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_TAB,
            "key.category." + Juggernaut.MOD_ID);

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null){
            return;
        }

        if (OPEN_SELECT_CARD_GUI.isPressed()) {
            DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> SelectCardGui.OpenSelectCardGui::new);
        }
        if (PLAYER_USE_SKILL.isPressed())  {
            PlayerGameData gameData = JuggernautClient.getInstance().getPlayerData(player.getUniqueID());
            GameCard card = gameData.getCard(null);
            if (card == null){
                player.sendStatusMessage(new StringTextComponent("未选择角色"), false);
                return;
            }
            if (!gameData.skillUsable(null)){
                player.sendStatusMessage(new StringTextComponent("技能冷却中"), false);
                return;
            }
            Juggernaut.debug("玩家使用技能");
            NetworkRegistryHandler.INSTANCE.send(PacketDistributor.SERVER.with(()-> null),
                    new PlayerUseSkillRequestPacket(false, false, null));
        }
        if (PLAYER_USE_ULTIMATE_SKILL.isPressed()) {
            PlayerGameData gameData = JuggernautClient.getInstance().getPlayerData(player.getUniqueID());
            GameCard card = gameData.getCard(null);
            if (card == null){
                player.sendStatusMessage(new StringTextComponent("未选择角色"), false);
                return;
            }
            if (!gameData.ultimateSkillUsable(null)){
                player.sendStatusMessage(new StringTextComponent("终极技能冷却中"), false);
                return;
            }
            Juggernaut.debug("玩家使用终极技能");
            NetworkRegistryHandler.INSTANCE.send(PacketDistributor.SERVER.with(()-> null),
                    new PlayerUseSkillRequestPacket(true, false, null));
        }

        GameDataRender.render = PLAYER_LOOK_GAME_DATA.isKeyDown();
    }

}
