package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import model.Board;
import model.Game;
import model.Player;

public class OthelloWindow extends JFrame {

    private static final long serialVersionUID = 8782122389400590079L;

    private final OthelloBoard boardPanel;
    private final OptionPanel optionPanel;
    private final JLabel statusLabel;
    private final JLabel scoreLabel;

    public OthelloWindow() {
        super("Othello AI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(860, 680));
        setResizable(true);
        setLocationByPlatform(true);

        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBackground(new Color(238, 239, 233));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel header = new JPanel(new BorderLayout(12, 6));
        header.setOpaque(false);

        JLabel title = new JLabel("Othello AI");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 30f));
        title.setForeground(new Color(32, 39, 34));

        scoreLabel = new JLabel("", SwingConstants.RIGHT);
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 18f));
        scoreLabel.setForeground(new Color(32, 39, 34));

        statusLabel = new JLabel("Black to move.");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 15f));
        statusLabel.setForeground(new Color(75, 82, 77));

        header.add(title, BorderLayout.WEST);
        header.add(scoreLabel, BorderLayout.EAST);
        header.add(statusLabel, BorderLayout.SOUTH);

        boardPanel = new OthelloBoard(this);
        boardPanel.setBorder(BorderFactory.createLineBorder(new Color(27, 55, 40), 1));

        optionPanel = new OptionPanel(this);

        root.add(header, BorderLayout.NORTH);
        root.add(boardPanel, BorderLayout.CENTER);
        root.add(optionPanel, BorderLayout.EAST);

        setContentPane(root);
        refresh();
        pack();
        boardPanel.runCurrentPlayerIfNeeded();
    }

    public OthelloBoard getBoardPanel() {
        return boardPanel;
    }

    public Game getGame() {
        return boardPanel.getGame();
    }

    public void setBlackPlayer(Player player) {
        boardPanel.setBlackPlayer(player);
    }

    public void setWhitePlayer(Player player) {
        boardPanel.setWhitePlayer(player);
    }

    public void restart() {
        boardPanel.restart();
    }

    public void undo() {
        boardPanel.undo();
    }

    public void refresh() {
        Game game = boardPanel.getGame();
        int black = game.blackScore();
        int white = game.whiteScore();
        scoreLabel.setText("Black " + black + "  |  White " + white);
        statusLabel.setText(game.getStatusMessage());
        optionPanel.refresh(game);
        repaint();
    }

    public Color currentTurnColor() {
        return getGame().getCurrentPlayer() == Board.BLACK ? Color.BLACK : Color.WHITE;
    }
}
