package model;

/**
 * Strategy interface for a human or computer Othello player.
 */
public abstract class Player {

    private final String name;

    protected Player(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public abstract boolean isHuman();

    public abstract Move chooseMove(Game game);

    public void updateGame(Game game) {
        if (game == null || game.isGameOver()) {
            return;
        }

        Move move = chooseMove(game.copy());
        if (move != null && !move.isPass()) {
            game.play(move);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
