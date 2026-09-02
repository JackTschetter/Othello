package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Board;
import model.ComputerPlayer;
import model.Game;
import model.HumanPlayer;
import model.Player;
import model.RandomPlayer;

public class OptionPanel extends JPanel {

    private static final long serialVersionUID = -4763875452164030755L;

    private static final String HUMAN = "Human";
    private static final String RANDOM = "Random";
    private static final String MINIMAX_2 = "Minimax depth 2";
    private static final String ALPHA_BETA_3 = "Alpha-beta depth 3";
    private static final String ALPHA_BETA_4 = "Alpha-beta depth 4";
    private static final String ALPHA_BETA_5 = "Alpha-beta depth 5";

    private final OthelloWindow window;
    private final JComboBox<String> blackOptions;
    private final JComboBox<String> whiteOptions;
    private final JButton newGameButton;
    private final JButton undoButton;
    private final JLabel turnValue;
    private final JLabel legalMovesValue;
    private final JLabel blackScoreValue;
    private final JLabel whiteScoreValue;

    public OptionPanel(OthelloWindow window) {
        this.window = window;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(230, 560));
        setBackground(new Color(249, 250, 246));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 214, 204)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel heading = new JLabel("Match");
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 20f));
        heading.setForeground(new Color(32, 39, 34));
        heading.setAlignmentX(LEFT_ALIGNMENT);

        String[] options = {HUMAN, RANDOM, MINIMAX_2, ALPHA_BETA_3, ALPHA_BETA_4, ALPHA_BETA_5};
        blackOptions = new JComboBox<>(options);
        whiteOptions = new JComboBox<>(options);
        whiteOptions.setSelectedItem(ALPHA_BETA_4);

        blackOptions.addActionListener(event -> window.setBlackPlayer(createPlayer((String) blackOptions.getSelectedItem())));
        whiteOptions.addActionListener(event -> window.setWhitePlayer(createPlayer((String) whiteOptions.getSelectedItem())));

        newGameButton = new JButton("New Game");
        undoButton = new JButton("Undo");
        newGameButton.addActionListener(event -> window.restart());
        undoButton.addActionListener(event -> window.undo());

        turnValue = valueLabel();
        legalMovesValue = valueLabel();
        blackScoreValue = valueLabel();
        whiteScoreValue = valueLabel();

        add(heading);
        add(Box.createVerticalStrut(18));
        add(selectorPanel("Black", blackOptions));
        add(Box.createVerticalStrut(12));
        add(selectorPanel("White", whiteOptions));
        add(Box.createVerticalStrut(18));
        add(buttonPanel());
        add(Box.createVerticalStrut(22));
        add(statPanel());
        add(Box.createVerticalGlue());
    }

    public void refresh(Game game) {
        turnValue.setText(game.isGameOver() ? "-" : Board.colorName(game.getCurrentPlayer()));
        legalMovesValue.setText(Integer.toString(game.isGameOver() ? 0 : game.legalMoves().size()));
        blackScoreValue.setText(Integer.toString(game.blackScore()));
        whiteScoreValue.setText(Integer.toString(game.whiteScore()));
        undoButton.setEnabled(game.canUndo());
    }

    private JPanel selectorPanel(String label, JComboBox<String> options) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 0, 6, 0);
        JLabel title = new JLabel(label);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));
        panel.add(title, constraints);

        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        options.setFocusable(false);
        panel.add(options, constraints);
        return panel;
    }

    private JPanel buttonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, 0, 8);
        panel.add(newGameButton, constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
        panel.add(undoButton, constraints);
        return panel;
    }

    private JPanel statPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 0, 10, 0);
        JLabel title = new JLabel("Position");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 17f));
        title.setForeground(new Color(32, 39, 34));
        panel.add(title, constraints);

        addStat(panel, constraints, "Turn", turnValue);
        addStat(panel, constraints, "Legal moves", legalMovesValue);
        addStat(panel, constraints, "Black disks", blackScoreValue);
        addStat(panel, constraints, "White disks", whiteScoreValue);
        return panel;
    }

    private void addStat(JPanel panel, GridBagConstraints constraints, String label, JLabel value) {
        constraints.gridy++;
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, 8, 0);
        JLabel name = new JLabel(label);
        name.setForeground(new Color(88, 96, 90));
        panel.add(name, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0;
        value.setForeground(new Color(32, 39, 34));
        panel.add(value, constraints);
    }

    private JLabel valueLabel() {
        JLabel label = new JLabel("-");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        return label;
    }

    private Player createPlayer(String type) {
        if (RANDOM.equals(type)) {
            return new RandomPlayer();
        }
        if (MINIMAX_2.equals(type)) {
            return new ComputerPlayer(2, ComputerPlayer.Strategy.MINIMAX);
        }
        if (ALPHA_BETA_3.equals(type)) {
            return new ComputerPlayer(3, ComputerPlayer.Strategy.ALPHA_BETA);
        }
        if (ALPHA_BETA_5.equals(type)) {
            return new ComputerPlayer(5, ComputerPlayer.Strategy.ALPHA_BETA);
        }
        if (ALPHA_BETA_4.equals(type)) {
            return new ComputerPlayer(4, ComputerPlayer.Strategy.ALPHA_BETA);
        }
        return new HumanPlayer();
    }
}
