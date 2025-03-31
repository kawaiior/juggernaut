package github.kawaiior.juggernaut.render.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

import java.util.ArrayList;
import java.util.List;

public class DeathBoardGui extends AbstractGui {
    private final Minecraft minecraft;
    private MatrixStack matrixStack;

    private static final int MARGIN = 5;
    private static final List<DeathBoardMsg> DEATH_BOARD_MSG_LIST = new ArrayList<>();
    private static final int MAX_MSG_NUM = 10;
    private static final int MAX_MSG_TIME_MILLIS = 10000;
    private static final int STRING_HEIGHT = 11;

    public DeathBoardGui(MatrixStack matrixStack) {
        this.minecraft = Minecraft.getInstance();
        this.matrixStack = matrixStack;
    }

    public static void receiveMsg(String msg) {
        if (DEATH_BOARD_MSG_LIST.size() >= MAX_MSG_NUM) {
            DEATH_BOARD_MSG_LIST.remove(0);
        }
        DEATH_BOARD_MSG_LIST.add(new DeathBoardMsg(msg, System.currentTimeMillis()));
    }

    public void setMatrixStack(MatrixStack stack) {
        this.matrixStack = stack;
    }

    public void render() {
        matrixStack.push();
        matrixStack.scale(0.73F, 0.73F, 1.0F);
        for (int i = 0; i < DEATH_BOARD_MSG_LIST.size(); i++) {
            drawString(matrixStack, minecraft.fontRenderer, DEATH_BOARD_MSG_LIST.get(i).message,
                    MARGIN, MARGIN + i * STRING_HEIGHT, 0xFFFFFF);
        }
        matrixStack.pop();

        if (minecraft.player != null && minecraft.player.ticksExisted % 20 == 0){
            // 移除过期的消息
            DEATH_BOARD_MSG_LIST.removeIf(
                    deathBoardMsg -> System.currentTimeMillis() - deathBoardMsg.receiveTime > MAX_MSG_TIME_MILLIS
            );
        }
    }

    public static class DeathBoardMsg{
        public final String message;
        public final long receiveTime;

        public DeathBoardMsg(String message, long receiveTime) {
            this.message = message;
            this.receiveTime = receiveTime;
        }
    }
}
