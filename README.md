# Othello

![Java](https://img.shields.io/badge/Java-8%2B-blue.svg)  
![Build](https://img.shields.io/badge/build-javac%20%2B%20make-green.svg)  
![AI](https://img.shields.io/badge/AI-minimax%20%2B%20alpha--beta-purple.svg)

<p align="center">
  <img src="assets/othello-gui.png" alt="Othello AI Swing GUI showing the starting board, legal move hints, score tracking, and configurable AI opponents" width="900">
</p>

<p align="center">
  <em>Java Swing Othello interface with legal move hints, score tracking, undo support, and configurable AI opponents.</em>
</p>

## Table of Contents

- [About](#about)
- [Project Goals](#project-goals)
- [Tools Used](#tools-used)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Build](#build)
  - [Run the GUI](#run-the-gui)
  - [Run the CLI](#run-the-cli)
  - [Run Simulations](#run-simulations)
  - [Run Tests](#run-tests)
- [Technical Walkthrough](#technical-walkthrough)
  - [Othello as an AI Problem](#othello-as-an-ai-problem)
  - [Rules Engine](#rules-engine)
  - [Game Flow](#game-flow)
  - [Computer Player](#computer-player)
  - [Heuristic Evaluation](#heuristic-evaluation)
  - [GUI](#gui)
  - [CLI](#cli)
  - [Testing](#testing)
- [Project Structure](#project-structure)
- [Example Commands](#example-commands)
- [Limitations and Future Work](#limitations-and-future-work)
- [Portfolio Context](#portfolio-context)
- [Historical Note](#historical-note)
- [Acknowledgments](#acknowledgments)

---

## About

A Java native implementation of Othello, trademark Reversi. The code supports a GUI with options for both human and computer players. The computer player implements 4 algorithms. Minimax depth 2, Alpha Beta depth 3,  Alpha Beta depth 4, and Alpha Beta depth 5. There is also an option for "Random" which "randomly" selects from 1 of these 4. The computer can play against itself or against a human player.

Originally created for the course CSCI 5511 (Artificial Intelligence I) at University of Minnesota Twin Cities. I took this course during the third year of my undergrad. I was noted for being the only student in the course who used Java for the projects. Everyone else used Python. I preferred Java due to, what I perceive as, its more explicit rules and sublime logic. 

I was also the only student to create GUI's for all my projects. I did this because I enjoy visuals and to challenge myself. 

---

## Tools Used

- **Programming language**: Java
- **GUI toolkit**: Java Swing
- **Build tooling**: `make`, `javac`
- **AI (Search) Algorithms**: minimax search, alpha-beta pruning, heuristic board evaluation
- **Testing approach**: lightweight Java test harness with assertions implemented in code
- **Supported runtime**: JDK 8 or newer

No JavaFX, Maven, Gradle, or third-party libraries are required.

---

## Getting Started

### Prerequisites

Install a Java Development Kit:

- JDK 8 or newer
- `make`, if using the included Makefile

Check Java from the command line:

```bash
java -version
javac -version
```

### Build

From the repository root:

```bash
make build
```

This compiles all Java source files into the `out/` directory.

### Run the GUI

```bash
make gui
```

The GUI supports:

- Human vs. human
- Human vs. computer
- Computer vs. random
- Computer vs. computer
- Minimax and alpha-beta players at multiple search depths
- Legal move hints
- Score tracking
- Undo
- New game
- Resizable board layout

### Run the CLI

```bash
make cli
```

The default CLI mode starts an interactive game where moves are entered as row-column pairs:

```text
2 3
```

Rows and columns are zero-indexed.

### Run Simulations

The CLI can also run automated AI games:

```bash
make build
java -cp out cli.Main --simulate 10 --black alphabeta:5 --white alphabeta:4
```

Supported player values:

- `human`
- `random`
- `minimax:N`
- `ai:N`
- `alphabeta:N`

For example:

```bash
java -cp out cli.Main --simulate 20 --black alphabeta:4 --white random
```

### Run Tests

```bash
make test
```

The test target builds the project with `javac -Xlint:all` and runs the no-dependency test harness.

---

## Technical Walkthrough

### Othello as an AI Problem

Othello can be modeled using the standard components of an adversarial search problem:

- **State**: the board configuration and the player to move.
- **Actions**: all legal disk placements for the current player.
- **Transition function**: place a disk and flip all bracketed opponent disks.
- **Terminal test**: the board is full or neither player has a legal move.
- **Utility**: win, loss, draw, or disk differential at terminal states.

The full game tree is too large to search exhaustively during normal play, so the computer player searches to a fixed depth and evaluates frontier positions with a heuristic function.

### Rules Engine

The core rules live in [`src/model/Board.java`](src/model/Board.java).

The board is represented as an 8x8 integer array:

- `Board.BLACK = -1`
- `Board.WHITE = 1`
- `Board.EMPTY = 0`

The rules engine supports:

- Board reset to the standard starting position.
- Legal move generation for either color.
- Directional scanning in all eight Othello directions.
- Disk flipping after a legal move.
- Disk counting.
- Winner detection.
- Compact board serialization for tests and debugging.

Legal move generation scans every square and checks whether placing a disk there would flip at least one opponent disk. The important helper is the directional flip counter: it walks outward from a candidate move, counts contiguous opponent disks, and confirms that the line is closed by one of the current player's disks.

### Game Flow

The higher-level game state lives in [`src/model/Game.java`](src/model/Game.java).

`Game` owns:

- The current board.
- The side to move.
- Move execution.
- Automatic pass turns.
- Terminal detection.
- Score reporting.
- Undo history.
- User-facing status messages.

Othello has an important pass rule: if the next player has no legal moves, the turn passes back to the opponent. If neither player has a legal move, the game is over. This logic is centralized in the game model so both the GUI and CLI behave consistently.

### Computer Player

The AI logic lives in [`src/model/ComputerPlayer.java`](src/model/ComputerPlayer.java).

The computer player supports two strategies:

- `MINIMAX`
- `ALPHA_BETA`

At a high level, the AI:

1. Gets the legal moves for the current player.
2. Applies each move to a copied board.
3. Recursively searches the resulting game tree.
4. Evaluates frontier states with the heuristic function.
5. Selects a move with the best score.

Equal-scoring moves are tie-broken randomly. This keeps repeated games from always following the exact same path when multiple moves evaluate equally.

Alpha-beta pruning uses two bounds:

- `alpha`: the best value currently guaranteed for the maximizing player.
- `beta`: the best value currently guaranteed for the minimizing player.

When a branch can no longer affect the final decision, the search stops exploring that branch. The result is the same minimax decision, but usually with fewer board states evaluated.

The implementation also orders moves by a quick heuristic score before searching them. Good move ordering can make alpha-beta pruning more effective because strong moves create tighter bounds earlier.

### Heuristic Evaluation

The evaluation function lives in [`src/model/Heuristics.java`](src/model/Heuristics.java).

The heuristic combines several common Othello signals:

- **Mobility**: how many legal moves the player has compared with the opponent.
- **Coin parity**: disk-count advantage.
- **Corner control**: ownership of stable corner squares.
- **Positional weights**: a static table that values strong squares and penalizes dangerous ones.
- **Frontier disks**: disks adjacent to empty squares, which are often more vulnerable.

Raw disk count is not enough for strong Othello play. In the early and middle game, having more disks can be misleading if those disks are unstable or give the opponent more options. This is why the heuristic weights mobility, corners, positional strength, and frontier exposure alongside coin parity.

At terminal states, the heuristic heavily rewards winning disk differentials and penalizes losing ones. In non-terminal states, it computes a weighted combination of the strategic features above.

### GUI

The Swing interface lives in [`src/ui`](src/ui).

Important files:

- [`src/ui/Main.java`](src/ui/Main.java): GUI entry point.
- [`src/ui/OthelloWindow.java`](src/ui/OthelloWindow.java): main application window.
- [`src/ui/OthelloBoard.java`](src/ui/OthelloBoard.java): board rendering and click handling.
- [`src/ui/OptionPanel.java`](src/ui/OptionPanel.java): player controls, score, and position stats.

The GUI was designed to make the AI behavior easy to inspect:

- Legal moves are highlighted for human players.
- Disks are rendered with simple shading.
- Scores update after every move.
- The current turn and number of legal moves are displayed.
- Player strategies can be changed from dropdowns.
- The board remains square as the window is resized.

The GUI does not implement separate game rules. It delegates moves to the shared `Game` and `Board` classes, which keeps behavior consistent between the interface, CLI, and tests.

### CLI

The command-line entry point lives in [`src/cli/Main.java`](src/cli/Main.java).

The CLI supports two workflows:

- Interactive row-column play.
- Automated simulation between AI players.

Simulation mode is useful for quick sanity checks because it runs complete games without the GUI. It can also be used to compare strategies across multiple runs.

### Testing

Tests live in [`src/test/OthelloRulesTest.java`](src/test/OthelloRulesTest.java).

The test harness checks:

- Standard initial position.
- Initial legal moves.
- Disk flipping after a legal move.
- Illegal move rejection.
- Automatic pass handling.
- Alpha-beta move legality.
- Complete simulated game termination.
- Basic offscreen GUI render smoke test.

The tests are intentionally lightweight and dependency-free. They are not a substitute for a full JUnit suite, but they cover the most important correctness risks for this project.

---

## Project Structure

```text
.
|-- Makefile
|-- README.md
`-- src
    |-- cli
    |   `-- Main.java
    |-- logic
    |   |-- MoveGenerator.java
    |   `-- MoveLogic.java
    |-- model
    |   |-- Board.java
    |   |-- ComputerPlayer.java
    |   |-- Game.java
    |   |-- Heuristics.java
    |   |-- HumanPlayer.java
    |   |-- Move.java
    |   |-- Player.java
    |   `-- RandomPlayer.java
    |-- test
    |   `-- OthelloRulesTest.java
    `-- ui
        |-- Main.java
        |-- OptionPanel.java
        |-- OthelloBoard.java
        `-- OthelloWindow.java
```

The `logic` package is intentionally thin. The real rule implementation lives in `model`, while `logic` provides compatibility-style wrappers for move generation and validation.

---

## Example Commands

Build the project:

```bash
make build
```

Launch the GUI:

```bash
make gui
```

Launch the CLI:

```bash
make cli
```

Run tests:

```bash
make test
```

Run AI self-play:

```bash
java -cp out cli.Main --simulate 10 --black alphabeta:5 --white alphabeta:4
```

Run alpha-beta against a random player:

```bash
java -cp out cli.Main --simulate 20 --black alphabeta:4 --white random
```

Clean generated build output:

```bash
make clean
```

---

## Limitations and Future Work

This project demonstrates classical adversarial search, but it is not intended to be a tournament-strength Othello engine.

Useful extensions would include:

- Add a transposition table to cache repeated board states.
- Add iterative deepening so the AI can search until a time budget expires.
- Add a stronger endgame solver when few empty squares remain.
- Add an opening book.
- Add better empirical evaluation across many games and search depths.
- Tune heuristic weights experimentally.
- Compare minimax, alpha-beta, Monte Carlo Tree Search, and reinforcement-learning approaches.
- Add JUnit tests if the project is expanded further.
- Add a packaged release target for easier desktop distribution.

The current version focuses on correctness, readability, reproducibility, and a clear demonstration of the core AI techniques.

---

## Portfolio Context

This repository is part of a broader effort to turn coursework and research projects into polished technical artifacts.

For hiring or research outreach, this project is meant to show:

- Comfort implementing classical AI algorithms.
- Ability to translate a formal search problem into working software.
- Java development experience.
- GUI development with Swing.
- Clean separation between model, UI, CLI, and tests.
- Practical attention to build and run instructions.
- Willingness to revisit, improve, and document older work.

A short demo video can show the GUI and AI self-play. A longer technical walkthrough can explain the search algorithm, heuristic design, implementation tradeoffs, and future research directions.

---

## Historical Note

The original CLI assignment used `javafx.util.Pair`, which is not available in many current standard JDK installs. This version removes that dependency and uses a small `Move` value object instead, so the project builds with plain `javac`.

The original GUI project also contained checkers-era model logic from an earlier codebase. This version replaces that with a complete Othello-specific model and keeps the GUI as a presentation layer over the shared game engine.

---

## Acknowledgments
• Andy Exley (awesome professor)
• Aadesh Salecha (awesome tutor) 
• Properly cite the course textbook.

This project was originally inspired by CSCI 5511 Artificial Intelligence at the University of Minnesota.

The implementation reflects standard ideas from adversarial search:

- Minimax search
- Alpha-beta pruning
- Depth-limited search
- Heuristic evaluation
- State-space modeling for deterministic games

Othello/Reversi remains a classic example because it is easy to explain, quick to play, and rich enough to reward careful search and evaluation design.
