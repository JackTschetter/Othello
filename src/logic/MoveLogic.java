package logic;

import model.Board;
import model.Game;
import model.Move;

public final class MoveLogic {

    private MoveLogic() {
    }

    public static boolean isValidMove(Game game, Move move) {
        return game != null && game.isLegalMove(move);
    }

    public static boolean isValidMove(Board board, int color, Move move) {
        return board != null && board.isLegalMove(color, move);
    }
}
