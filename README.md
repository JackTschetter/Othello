# Othello

![Java](https://img.shields.io/badge/Java-8%2B-blue.svg)  
![Build](https://img.shields.io/badge/build-javac%20%2B%20make-green.svg)  
![AI](https://img.shields.io/badge/AI-minimax%20%2B%20alpha--beta-purple.svg)

<p align="center">
  <img src="src/assets/othello-gui.png" alt="Othello AI Swing GUI showing the starting board, legal move hints, score tracking, and configurable AI opponents" width="900">
</p>

## Table of Contents

- [About](#about)
- [Tools Used](#tools-used)
- [Background: Othello](#background-othello)
  - [Objective](#objective)
  - [Board and Coordinates](#board-and-coordinates)
  - [Legal Moves](#legal-moves)
  - [Disk Flipping](#disk-flipping)
  - [Passing and End Conditions](#passing-and-end-conditions)
- [Minimax Search and Alpha-Beta Pruning](#minimax-search-and-alpha-beta-pruning)
  - [Othello as an Adversarial Search Problem](#othello-as-an-adversarial-search-problem)
  - [Minimax Search](#minimax-search)
  - [Alpha-Beta Pruning](#alpha-beta-pruning)
  - [Heuristic Evaluation](#heuristic-evaluation)
  - [Supported Strategies](#supported-strategies)
- [Project Architecture](#project-architecture)
  - [System Overview](#system-overview)
  - [Code Organization](#code-organization)
  - [Model Layer](#model-layer)
  - [AI Layer](#ai-layer)
  - [Interface Layer](#interface-layer)
  - [Testing](#testing)
- [Building and Running](#building-and-running)
  - [Requirements](#requirements)
  - [Build](#build)
  - [Run the GUI](#run-the-gui)
  - [Run the CLI](#run-the-cli)
  - [Run Simulations](#run-simulations)
  - [Run Tests](#run-tests)
  - [Clean Build Output](#clean-build-output)
- [Future Work](#future-work)
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
## Background : Othello

### Objective

### Board and Coordinates

### Legal Moves

### Disk Flipping

### Passing and End Conditions
---
## Minimax Search and Alpha-Beta Pruning

### Othello as an Adversarial Search Problem

### Minimax Search

### Alpha-Beta Pruning

### Heuristic Evaluation

### Supported Strategies
---

## Project Architecture

### Code Organization

```text
.
|-- assets
|   `-- images
|       `-- othello-gui.png
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

## Building and Running

## Future Work

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
