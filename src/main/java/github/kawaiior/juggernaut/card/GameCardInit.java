package github.kawaiior.juggernaut.card;

import java.util.ArrayList;
import java.util.List;

public class GameCardInit {

    public static final List<GameCard> GAME_CARD_ARRAY = new ArrayList<>();

    private static int id = 0;

    private static int nextId() {
        return id++;
    }

    private static GameCard createGameCard(GameCard gameCard, int cardId, String cardName, int skillUseCount, int skillCoolDown, int ultimateSkillCoolDown) {
        gameCard.setCardId(cardId);
        gameCard.setCardName(cardName);
        gameCard.setSkillUseCount(skillUseCount);
        gameCard.setSkillCoolDown(skillCoolDown * 1000);
        gameCard.setUltimateSkillCoolDown(ultimateSkillCoolDown * 1000);

        GAME_CARD_ARRAY.add(gameCard);
        return gameCard;
    }

    public static GameCard getGameCardById(int id) {
        if (id >= GAME_CARD_ARRAY.size() || id < 0) {
            return null;
        }
        return GAME_CARD_ARRAY.get(id);
    }

    public static final GameCard XING_HUI = createGameCard(new CardXingHui(), nextId(), "xing_hui", 3,10, 40);
    public static final GameCard BAI_MO = createGameCard(new CardBaiMo(), nextId(), "bai_mo", 3,8, 60);
    public static final GameCard MADDELENA = createGameCard(new CardMaddelena(), nextId(), "maddeleena", 3,10, 60);
    public static final GameCard FRAGRANS = createGameCard(new CardFragrans(), nextId(), "fragrans", 3,40, 60);
    public static final GameCard XIN = createGameCard(new CardXin(), nextId(), "xin", 2,40, 60);
    public static final GameCard MING = createGameCard(new CardMing(), nextId(), "ming", 1,15, 30);
    public static final GameCard GILGAMESH = createGameCard(new CardGilgamesh(), nextId(), "gilgamesh", 20,3, 30);

}
