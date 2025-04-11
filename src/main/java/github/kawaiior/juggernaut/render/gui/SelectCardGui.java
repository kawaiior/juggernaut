package github.kawaiior.juggernaut.render.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import github.kawaiior.juggernaut.Juggernaut;
import github.kawaiior.juggernaut.card.GameCard;
import github.kawaiior.juggernaut.card.GameCardInit;
import github.kawaiior.juggernaut.network.NetworkRegistryHandler;
import github.kawaiior.juggernaut.network.packet.PlayerSelectCardPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class SelectCardGui extends Screen {

    public static class OpenSelectCardGui{
        public OpenSelectCardGui(){
            Minecraft.getInstance().displayGuiScreen(new SelectCardGui(new StringTextComponent("test")));
        }
    }

    private static final int textureWidth = 400;
    private static final int textureHeight = 225;

    List<Button> buttonList = new ArrayList<>();

    ResourceLocation BACKGROUND_GUI_TEXTURE = new ResourceLocation(Juggernaut.MOD_ID, "textures/gui/select_card_gui_background.png");

    protected SelectCardGui(ITextComponent component) {
        super(component);
    }

    @Override
    protected void init() {
        this.minecraft.keyboardListener.enableRepeatEvents(true);

        this.buttonList = new ArrayList<>();
        int startX = this.width / 2 + 15;
        int startY = 20;

        for (int i = 0; i < GameCardInit.GAME_CARD_ARRAY.size(); i++) {
            GameCard card = GameCardInit.GAME_CARD_ARRAY.get(i);
            ITextComponent component = card.getCardTranslationName();
            String str = component.getString();
            int x = startX + (i % 3) * 60;
            int y = startY + (i / 3) * 30;
            Button button = new Button(x, y, 50, 20, card.getCardTranslationName(), (b) -> {
                Juggernaut.debug("用户点击了按钮 " + str);
                NetworkRegistryHandler.INSTANCE.send(PacketDistributor.SERVER.with(()-> null),
                        new PlayerSelectCardPacket(card.getCardId()));
                Minecraft.getInstance().popGuiLayer(); // 关闭当前GUI
            });
            this.buttonList.add(button);
            this.addButton(button);
        }

        super.init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_GUI_TEXTURE);
        blit(matrixStack, (this.width - textureWidth) / 2, 10, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

        for (Button button : buttonList) {
            button.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
