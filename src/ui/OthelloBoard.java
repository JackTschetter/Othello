package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.Timer;

import model.Board;
import model.ComputerPlayer;
import model.Game;
import model.HumanPlayer;
import model.Move;
import model.Player;

public class OthelloBoard extends JPanel {

    private static final long serialVersionUID = -6014690893709316364L;

    private static final int BOARD_PADDING = 46;
    private static final int FRAME_WIDTH = 10;
    private static final int FRAME_RADIUS = 14;
    private static final int COORDINATE_GAP = 10;
    private static final int TIMER_DELAY_MS = 420;

    private final OthelloWindow window;
    private final Color boardColor = new Color(31, 119, 78);
    private final Color boardDark = new Color(19, 80, 54);
    private final Color highlightColor = new Color(247, 206, 94);
    private Game game;
    private Player blackPlayer;
    private Player whitePlayer;
    private Move hoverMove;
    private Timer aiTimer;

    public OthelloBoard(OthelloWindow window) {
        this.window = window;
        this.game = new Game();
        this.blackPlayer = new HumanPlayer();
        this.whitePlayer = new ComputerPlayer(4, ComputerPlayer.Strategy.ALPHA_BETA);
        setOpaque(true);
        setBackground(new Color(222, 225, 216));
        setPreferredSize(new Dimension(640, 640));
        setMinimumSize(new Dimension(460, 460));

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                handleClick(event.getX(), event.getY());
            }

            @Override
            public void mouseMoved(MouseEvent event) {
                hoverMove = moveAt(event.getX(), event.getY());
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent event) {
                hoverMove = null;
                repaint();
            }
        };
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public Game getGame() {
        return game;
    }

    public void setBlackPlayer(Player player) {
        this.blackPlayer = player == null ? new HumanPlayer() : player;
        runCurrentPlayerIfNeeded();
        repaint();
    }

    public void setWhitePlayer(Player player) {
        this.whitePlayer = player == null ? new HumanPlayer() : player;
        runCurrentPlayerIfNeeded();
        repaint();
    }

    public void restart() {
        stopTimer();
        this.game.restart();
        this.hoverMove = null;
        refreshWindow();
        repaint();
        runCurrentPlayerIfNeeded();
    }

    public void undo() {
        stopTimer();
        if (game.undo()) {
            hoverMove = null;
            refreshWindow();
            repaint();
            runCurrentPlayerIfNeeded();
        }
    }

    public void runCurrentPlayerIfNeeded() {
        if (game.isGameOver() || currentPlayer().isHuman() || aiTimer != null) {
            refreshWindow();
            return;
        }

        aiTimer = new Timer(TIMER_DELAY_MS, event -> {
            Player player = currentPlayer();
            player.updateGame(game);
            stopTimer();
            hoverMove = null;
            refreshWindow();
            repaint();
            runCurrentPlayerIfNeeded();
        });
        aiTimer.setRepeats(false);
        aiTimer.start();
    }

    private void stopTimer() {
        if (aiTimer != null) {
            aiTimer.stop();
            aiTimer = null;
        }
    }

    private Player currentPlayer() {
        return game.getCurrentPlayer() == Board.BLACK ? blackPlayer : whitePlayer;
    }

    private void handleClick(int x, int y) {
        if (game.isGameOver() || !currentPlayer().isHuman()) {
            return;
        }

        Move move = moveAt(x, y);
        if (move != null && game.play(move)) {
            hoverMove = null;
            refreshWindow();
            repaint();
            runCurrentPlayerIfNeeded();
        }
    }

    private void refreshWindow() {
        if (window != null) {
            window.refresh();
        }
    }

    private Move moveAt(int x, int y) {
        Rectangle board = boardBounds();
        if (!board.contains(x, y)) {
            return null;
        }
        int tile = board.width / Board.SIZE;
        int col = (x - board.x) / tile;
        int row = (y - board.y) / tile;
        if (!Board.isInBounds(row, col)) {
            return null;
        }
        return new Move(row, col);
    }

    private Rectangle boardBounds() {
        int size = Math.max(0, Math.min(getWidth(), getHeight()) - 2 * BOARD_PADDING);
        int tile = Math.max(1, size / Board.SIZE);
        int boardSize = tile * Board.SIZE;
        return new Rectangle((getWidth() - boardSize) / 2, (getHeight() - boardSize) / 2, boardSize, boardSize);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        Rectangle board = boardBounds();
        int tile = board.width / Board.SIZE;
        Board snapshot = game.getBoard();
        List<Move> legalMoves = game.legalMoves();
        Set<Move> legalMoveSet = new HashSet<>(legalMoves);

        g.setColor(new Color(18, 47, 35));
        g.fillRoundRect(board.x - FRAME_WIDTH, board.y - FRAME_WIDTH,
                board.width + 2 * FRAME_WIDTH, board.height + 2 * FRAME_WIDTH,
                FRAME_RADIUS, FRAME_RADIUS);
        g.setColor(boardColor);
        g.fillRect(board.x, board.y, board.width, board.height);

        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                int x = board.x + col * tile;
                int y = board.y + row * tile;
                Move cellMove = new Move(row, col);
                boolean hoveredLegalMove = hoverMove != null && hoverMove.equals(cellMove)
                        && legalMoveSet.contains(hoverMove) && currentPlayer().isHuman();

                if (hoveredLegalMove) {
                    g.setColor(new Color(59, 145, 94));
                    g.fillRect(x, y, tile, tile);
                }

                g.setColor(boardDark);
                g.setStroke(new BasicStroke(1.1f));
                g.drawRect(x, y, tile, tile);

                int value = snapshot.get(row, col);
                if (value == Board.BLACK || value == Board.WHITE) {
                    drawDisk(g, value, x, y, tile);
                } else if (currentPlayer().isHuman() && legalMoveSet.contains(cellMove)) {
                    drawLegalMoveHint(g, x, y, tile);
                }
            }
        }

        drawCoordinates(g, board, tile);
        if (game.isGameOver()) {
            drawGameOver(g, board);
        }

        g.dispose();
    }

    private void drawDisk(Graphics2D g, int value, int x, int y, int tile) {
        int margin = Math.max(8, tile / 8);
        int size = tile - 2 * margin;
        int diskX = x + margin;
        int diskY = y + margin;

        g.setColor(new Color(0, 0, 0, 60));
        g.fillOval(diskX + 3, diskY + 5, size, size);

        if (value == Board.BLACK) {
            g.setPaint(new GradientPaint(diskX, diskY, new Color(74, 78, 76),
                    diskX + size, diskY + size, new Color(8, 10, 9)));
            g.fillOval(diskX, diskY, size, size);
            g.setColor(new Color(164, 169, 165, 130));
        } else {
            g.setPaint(new GradientPaint(diskX, diskY, Color.WHITE,
                    diskX + size, diskY + size, new Color(207, 211, 207)));
            g.fillOval(diskX, diskY, size, size);
            g.setColor(new Color(55, 61, 57, 130));
        }
        g.setStroke(new BasicStroke(2f));
        g.drawOval(diskX, diskY, size, size);
    }

    private void drawLegalMoveHint(Graphics2D g, int x, int y, int tile) {
        int size = Math.max(10, tile / 5);
        int dotX = x + (tile - size) / 2;
        int dotY = y + (tile - size) / 2;
        g.setColor(new Color(highlightColor.getRed(), highlightColor.getGreen(), highlightColor.getBlue(), 190));
        g.fillOval(dotX, dotY, size, size);
    }

    private void drawCoordinates(Graphics2D g, Rectangle board, int tile) {
        g.setFont(getFont().deriveFont(Font.BOLD, 13f));
        g.setColor(new Color(42, 55, 47));
        int topBaseline = board.y - FRAME_WIDTH - COORDINATE_GAP;
        int leftLabelRightEdge = board.x - FRAME_WIDTH - COORDINATE_GAP;
        for (int index = 0; index < Board.SIZE; index++) {
            String label = Integer.toString(index);
            int labelWidth = g.getFontMetrics().stringWidth(label);
            int columnX = board.x + index * tile + tile / 2 - labelWidth / 2;
            int rowY = board.y + index * tile
                    + (tile + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent()) / 2;

            g.drawString(label, columnX, topBaseline);
            g.drawString(label, leftLabelRightEdge - labelWidth, rowY);
        }
    }

    private void drawGameOver(Graphics2D g, Rectangle board) {
        String title = "Game Over";
        String detail = game.getStatusMessage();

        g.setColor(new Color(245, 247, 242, 232));
        g.fillRoundRect(board.x + board.width / 2 - 175, board.y + board.height / 2 - 48,
                350, 96, 12, 12);
        g.setColor(new Color(35, 42, 37));
        g.setFont(getFont().deriveFont(Font.BOLD, 24f));
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, board.x + board.width / 2 - titleWidth / 2, board.y + board.height / 2 - 10);

        g.setFont(getFont().deriveFont(Font.PLAIN, 14f));
        int detailWidth = g.getFontMetrics().stringWidth(detail);
        g.drawString(detail, board.x + board.width / 2 - detailWidth / 2, board.y + board.height / 2 + 18);
    }
}
