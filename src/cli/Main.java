package cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import model.Board;
import model.ComputerPlayer;
import model.Game;
import model.HumanPlayer;
import model.Move;
import model.Player;
import model.RandomPlayer;

public class Main {

    public static void main(String[] args) {
        Config config = Config.from(args);
        if (config.showHelp) {
            printUsage();
            return;
        }

        if (config.simulations > 0) {
            runSimulations(config);
        } else {
            runInteractiveGame(config);
        }
    }

    private static void runInteractiveGame(Config config) {
        Game game = new Game();
        Player black = createPlayer(config.blackPlayer);
        Player white = createPlayer(config.whitePlayer);
        Scanner scanner = new Scanner(System.in);

        while (!game.isGameOver()) {
            printBoard(game);
            Player current = game.getCurrentPlayer() == Board.BLACK ? black : white;
            Move move = current.isHuman() ? readHumanMove(scanner, game) : current.chooseMove(game.copy());
            if (move == null) {
                System.out.println("Goodbye.");
                return;
            }

            if (!move.isPass()) {
                System.out.println(Board.colorName(game.getCurrentPlayer()) + " plays " + move.toDisplayString());
            }
            if (!game.play(move)) {
                System.out.println("Illegal move. Try again.");
            } else {
                System.out.println(game.getStatusMessage());
            }
        }

        printBoard(game);
        System.out.println(game.getStatusMessage());
    }

    private static Move readHumanMove(Scanner scanner, Game game) {
        while (true) {
            System.out.println(Board.colorName(game.getCurrentPlayer()) + " legal moves: " + displayMoves(game.legalMoves()));
            System.out.print("Enter row col, or q to quit: ");
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("q") || line.equalsIgnoreCase("quit")) {
                return null;
            }

            String[] parts = line.split("\\s+");
            if (parts.length != 2) {
                System.out.println("Please enter two numbers, such as 2 3.");
                continue;
            }

            try {
                Move move = new Move(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                if (game.isLegalMove(move)) {
                    return move;
                }
            } catch (NumberFormatException ignored) {
                // Fall through to the retry message.
            }
            System.out.println("That is not a legal move for this position.");
        }
    }

    private static void runSimulations(Config config) {
        int blackWins = 0;
        int whiteWins = 0;
        int draws = 0;
        int blackDisks = 0;
        int whiteDisks = 0;

        for (int gameNumber = 1; gameNumber <= config.simulations; gameNumber++) {
            Game game = new Game();
            Player black = createPlayer(config.blackPlayer);
            Player white = createPlayer(config.whitePlayer);
            int turns = 0;
            while (!game.isGameOver() && turns < 120) {
                Player current = game.getCurrentPlayer() == Board.BLACK ? black : white;
                Move move = current.chooseMove(game.copy());
                if (move == null || move.isPass() || !game.play(move)) {
                    throw new IllegalStateException("Player produced an illegal move on turn " + turns + ": " + move);
                }
                turns++;
            }

            blackDisks += game.blackScore();
            whiteDisks += game.whiteScore();
            int winner = game.winner();
            if (winner == Board.BLACK) {
                blackWins++;
            } else if (winner == Board.WHITE) {
                whiteWins++;
            } else {
                draws++;
            }
        }

        System.out.println("Simulations: " + config.simulations);
        System.out.println("Black (" + config.blackPlayer + ") wins: " + blackWins);
        System.out.println("White (" + config.whitePlayer + ") wins: " + whiteWins);
        System.out.println("Draws: " + draws);
        System.out.printf(Locale.US, "Average final score: Black %.1f, White %.1f%n",
                blackDisks / (double) config.simulations, whiteDisks / (double) config.simulations);
    }

    private static Player createPlayer(String spec) {
        String normalized = spec == null ? "human" : spec.trim().toLowerCase(Locale.US);
        if (normalized.equals("human")) {
            return new HumanPlayer();
        }
        if (normalized.equals("random")) {
            return new RandomPlayer();
        }
        if (normalized.startsWith("minimax")) {
            return new ComputerPlayer(parseDepth(normalized, 2), ComputerPlayer.Strategy.MINIMAX);
        }
        if (normalized.startsWith("ai") || normalized.startsWith("alphabeta") || normalized.startsWith("alpha-beta")) {
            return new ComputerPlayer(parseDepth(normalized, 4), ComputerPlayer.Strategy.ALPHA_BETA);
        }
        throw new IllegalArgumentException("Unknown player type: " + spec);
    }

    private static int parseDepth(String spec, int fallback) {
        int separator = spec.indexOf(':');
        if (separator < 0) {
            return fallback;
        }
        return Integer.parseInt(spec.substring(separator + 1));
    }

    private static void printBoard(Game game) {
        Board board = game.getBoard();
        System.out.println();
        System.out.println("    0 1 2 3 4 5 6 7");
        System.out.println("   -----------------");
        for (int row = 0; row < Board.SIZE; row++) {
            System.out.print(" " + row + " |");
            for (int col = 0; col < Board.SIZE; col++) {
                System.out.print(Board.toChar(board.get(row, col)) + " ");
            }
            System.out.println();
        }
        System.out.println("Black " + game.blackScore() + " | White " + game.whiteScore());
    }

    private static String displayMoves(List<Move> moves) {
        List<String> labels = new ArrayList<>();
        for (Move move : moves) {
            labels.add(move.toDisplayString());
        }
        return String.join(", ", labels);
    }

    private static void printUsage() {
        System.out.println("Othello CLI");
        System.out.println("Usage:");
        System.out.println("  java -cp out cli.Main");
        System.out.println("  java -cp out cli.Main --black human --white ai:4");
        System.out.println("  java -cp out cli.Main --simulate 20 --black alphabeta:4 --white random");
        System.out.println();
        System.out.println("Players: human, random, minimax:N, ai:N, alphabeta:N");
    }

    private static class Config {
        private String blackPlayer = "human";
        private String whitePlayer = "ai:4";
        private int simulations = 0;
        private boolean showHelp = false;

        private static Config from(String[] args) {
            Config config = new Config();
            for (int index = 0; index < args.length; index++) {
                String arg = args[index];
                if (arg.equals("--help") || arg.equals("-h")) {
                    config.showHelp = true;
                } else if (arg.equals("--black") && index + 1 < args.length) {
                    config.blackPlayer = args[++index];
                } else if (arg.equals("--white") && index + 1 < args.length) {
                    config.whitePlayer = args[++index];
                } else if (arg.equals("--simulate") && index + 1 < args.length) {
                    config.simulations = Integer.parseInt(args[++index]);
                    if (config.blackPlayer.equals("human")) {
                        config.blackPlayer = "ai:4";
                    }
                    if (config.whitePlayer.equals("human")) {
                        config.whitePlayer = "random";
                    }
                } else {
                    throw new IllegalArgumentException("Unknown or incomplete argument: " + arg);
                }
            }
            return config;
        }
    }
}
