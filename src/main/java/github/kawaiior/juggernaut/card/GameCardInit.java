package github.kawaiior.juggernaut.card;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class GameCardInit {

    private static final List<GameCard> GAME_CARD_ARRAY = new ArrayList<>();

    private static int id = 0;

    private static int nextId() {
        return id++;
    }

    private static GameCard createGameCard(GameCard gameCard) {
        GAME_CARD_ARRAY.add(gameCard);
        return gameCard;
    }

    public static GameCard getGameCardById(int id) {
        if (id >= GAME_CARD_ARRAY.size()) {
            return null;
        }
        return GAME_CARD_ARRAY.get(id);
    }

    public static final GameCard XING_HUI = createGameCard(new CardXingHui(nextId(), "xing_hui", 3,10, 40, 40));
    public static final GameCard BAI_MO = createGameCard(new CardBaiMo(nextId(), "bai_mo", 1,8, 60, 60));

}
