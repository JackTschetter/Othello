package model;

public class HumanPlayer extends Player {

    public HumanPlayer() {
        super("Human");
    }

    @Override
    public boolean isHuman() {
        return true;
    }

    @Override
    public Move chooseMove(Game game) {
        return null;
    }
}
