package model;

import java.util.List;
import java.util.Random;

public class RandomPlayer extends Player {

    private final Random random;

    public RandomPlayer() {
        this(new Random());
    }

    public RandomPlayer(Random random) {
        super("Random");
        this.random = random == null ? new Random() : random;
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public Move chooseMove(Game game) {
        if (game == null || game.isGameOver()) {
            return Move.PASS;
        }

        List<Move> legalMoves = game.legalMoves();
        if (legalMoves.isEmpty()) {
            return Move.PASS;
        }
        return legalMoves.get(random.nextInt(legalMoves.size()));
    }
}
