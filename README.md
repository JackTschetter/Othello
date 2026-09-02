# Othello AI

Java implementation of Othello/Reversi with a playable Swing GUI, a command-line mode, and AI opponents powered by minimax search with alpha-beta pruning.

This project began as a CSCI 5511 Artificial Intelligence assignment at the University of Minnesota. The current version keeps the original AI-search spirit while making the repository self-contained, easier to run, and better suited for portfolio review.

## Features

- Complete Othello rules: legal move generation, disk flipping, turn passing, terminal detection, and scoring.
- Play modes: human vs. AI, human vs. human, AI vs. random, and AI vs. AI.
- AI strategies: random play, depth-limited minimax, and alpha-beta pruning.
- Heuristic evaluation using mobility, coin parity, corner control, positional weights, and frontier disks.
- Polished Swing interface with legal-move hints, score tracking, status messaging, undo, and player selection.
- CLI simulation mode for quick AI smoke tests.

## Run

Requires JDK 8 or newer.

```bash
make gui
```

```bash
make cli
```

You can also run simulations from the CLI:

```bash
make build
java -cp out cli.Main --simulate 20 --black alphabeta:4 --white random
```

## Test

```bash
make test
```

The test harness validates the initial position, legal moves, disk flipping, illegal-move rejection, automatic passes, AI move legality, and a complete simulated game.

## Project Structure

```text
src/model   Core Othello state, rules, players, and heuristics
src/logic   Thin move-generation and validation wrappers
src/ui      Swing GUI
src/cli     Command-line runner
src/test    Lightweight no-dependency test harness
```

## Historical Note

The original CLI assignment used `javafx.util.Pair`, which is not available in current standard JDK installs. This version removes that dependency so the project builds with plain `javac`.
