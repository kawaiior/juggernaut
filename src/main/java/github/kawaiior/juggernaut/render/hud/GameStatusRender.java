package github.kawaiior.juggernaut.render.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import github.kawaiior.juggernaut.game.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public class GameStatusRender extends AbstractGui {
    public static int gameStatus = 0;
    public static long gameStatusTime = -1;
    private final Minecraft minecraft;
    private MatrixStack matrixStack;
    private final int width;
    private final int height;

    public static final String GAME_UN_START = "游戏未开始";
    private static final String GAME_READY = "游戏即将开始";
    private static final String GAME_START = "游戏时间";
    private static final String GAME_OVER = "游戏结束";

    public GameStatusRender(MatrixStack matrixStack) {
        this.minecraft = Minecraft.getInstance();
        this.matrixStack = matrixStack;
        this.width = this.minecraft.getMainWindow().getScaledWidth();
        this.height = this.minecraft.getMainWindow().getScaledHeight();
    }


    public void render() {
        String gameStatusText;
        String gameTimeText = "00:00";
        if (gameStatus == 0) {
            gameStatusText = GAME_UN_START;
        } else if (gameStatus == 1) {
            long timeRemaining = gameStatusTime - System.currentTimeMillis() + Constants.GAME_PREPARE_TIME;
            if (timeRemaining < 0){
                timeRemaining = 0;
            }
            gameStatusText = GAME_READY;
            gameTimeText = String.format("%02d:%02d", timeRemaining / 1000 / 60, timeRemaining / 1000 % 60);
        } else if (gameStatus == 2){
            long timeRemaining = gameStatusTime - System.currentTimeMillis() + Constants.GAME_MAX_TIME;
            if (timeRemaining < 0){
                timeRemaining = 0;
            }
            gameStatusText = GAME_START;
            gameTimeText = String.format("%02d:%02d", timeRemaining / 1000 / 60, timeRemaining / 1000 % 60);
        }else {
            long timeRemaining = gameStatusTime - System.currentTimeMillis() + Constants.GAME_OVER_TIME;
            if (timeRemaining < 0){
                timeRemaining = 0;
            }
            gameStatusText = GAME_OVER;
            gameTimeText = String.format("%02d:%02d", timeRemaining / 1000 / 60, timeRemaining / 1000 % 60);
        }

        int txtWidth = minecraft.fontRenderer.getStringWidth(gameStatusText);
        int txtHeight = minecraft.fontRenderer.FONT_HEIGHT;
        int timeTxtWidth = minecraft.fontRenderer.getStringWidth(gameTimeText);

        drawString(matrixStack, minecraft.fontRenderer, gameStatusText, (width - txtWidth) / 2, 5, 0xFFFFFF);
        drawString(matrixStack, minecraft.fontRenderer, gameTimeText, (width - timeTxtWidth) / 2, 7+txtHeight, 0xFFFFFF);
    }


}
