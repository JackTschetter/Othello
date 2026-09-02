package test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import model.Board;
import model.ComputerPlayer;
import model.Game;
import model.Move;
import model.Player;
import model.RandomPlayer;
import ui.OthelloBoard;

public class OthelloRulesTest {

    public static void main(String[] args) {
        testInitialPosition();
        testMoveFlipsDisks();
        testIllegalMoveRejected();
        testAutomaticPass();
        testAiReturnsLegalMove();
        testCompleteSimulatedGame();
        testBoardCanRenderOffscreen();
        System.out.println("All Othello rule tests passed.");
    }

    private static void testInitialPosition() {
        Game game = new Game();
        require(game.blackScore() == 2, "Initial black disk count should be 2.");
        require(game.whiteScore() == 2, "Initial white disk count should be 2.");
        require(game.getCurrentPlayer() == Board.BLACK, "Black should move first.");

        Set<Move> expected = new HashSet<>(Arrays.asList(
                new Move(2, 3), new Move(3, 2), new Move(4, 5), new Move(5, 4)));
        require(new HashSet<>(game.legalMoves()).equals(expected), "Initial legal moves did not match Othello rules.");
    }

    private static void testMoveFlipsDisks() {
        Game game = new Game();
        require(game.play(new Move(2, 3)), "Black should be able to play 2 3.");
        Board board = game.getBoard();
        require(board.get(2, 3) == Board.BLACK, "Placed disk should be black.");
        require(board.get(3, 3) == Board.BLACK, "Move should flip the bracketed white disk.");
        require(game.blackScore() == 4, "Black should have four disks after the opening move.");
        require(game.whiteScore() == 1, "White should have one disk after the opening move.");
        require(game.getCurrentPlayer() == Board.WHITE, "White should move after Black.");
    }

    private static void testIllegalMoveRejected() {
        Game game = new Game();
        require(!game.play(new Move(0, 0)), "Corner should not be legal in the initial position.");
        require(game.blackScore() == 2 && game.whiteScore() == 2, "Illegal move should not mutate the board.");
    }

    private static void testAutomaticPass() {
        Board board = Board.fromCompactString(
                "BBBBBBBB"
                        + "BBBBBBBB"
                        + "BBBBBBBB"
                        + "BBBBBBBB"
                        + "BBBBBBBB"
                        + "BBBBBBBB"
                        + "BBBBBBBB"
                        + "BWWWWWW.");
        Game game = new Game(board, Board.WHITE);
        require(game.getCurrentPlayer() == Board.BLACK, "White should pass when only Black has a move.");
        require(game.play(new Move(7, 7)), "Black should fill the final square.");
        require(game.isGameOver(), "Full board should end the game.");
        require(game.blackScore() == 64, "The final black move should flip the last white row.");
    }

    private static void testAiReturnsLegalMove() {
        Game game = new Game();
        Player ai = new ComputerPlayer(3, ComputerPlayer.Strategy.ALPHA_BETA, new Random(7));
        Move move = ai.chooseMove(game.copy());
        require(game.isLegalMove(move), "Alpha-beta should return a legal move from the initial position.");
    }

    private static void testCompleteSimulatedGame() {
        Game game = new Game();
        Player black = new ComputerPlayer(2, ComputerPlayer.Strategy.ALPHA_BETA, new Random(11));
        Player white = new RandomPlayer(new Random(13));
        int turns = 0;

        while (!game.isGameOver() && turns < 120) {
            Player player = game.getCurrentPlayer() == Board.BLACK ? black : white;
            Move move = player.chooseMove(game.copy());
            require(move != null && !move.isPass(), "Players should produce a concrete legal move while game is active.");
            require(game.play(move), "Simulated player produced an illegal move: " + move);
            turns++;
        }

        require(game.isGameOver(), "Simulated game should terminate.");
        require(game.blackScore() + game.whiteScore() <= 64, "Final disk count cannot exceed board size.");
        require(turns <= 60, "A complete game cannot contain more than 60 placements after setup.");
    }

    private static void testBoardCanRenderOffscreen() {
        OthelloBoard board = new OthelloBoard(null);
        board.setSize(620, 620);
        BufferedImage image = new BufferedImage(620, 620, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        board.paint(graphics);
        graphics.dispose();

        int nonTransparentPixels = 0;
        for (int y = 0; y < image.getHeight(); y += 10) {
            for (int x = 0; x < image.getWidth(); x += 10) {
                if (((image.getRGB(x, y) >>> 24) & 0xff) > 0) {
                    nonTransparentPixels++;
                }
            }
        }
        require(nonTransparentPixels > 200, "Board render should paint visible pixels.");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
