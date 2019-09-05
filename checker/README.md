# Tunnel Checker



This doc contains five Java files.

`Game.java`: The entry for the game. The main function sets up the environment and calls `play` to start a Human-AI game. Unless the game over conditions are satisfied, it calls `getHumanMove` and `getAIMove` alternately to take input from human and to get move from the AI. It also provides `simulatePlay` to simulate a AI-AI game.

`Player.java`: The minimax and alpha-beta pruning algorithms behind the AI player. The method `getBestMove` initializes a new BoardState and calls `serachTree` with depth zero. Inside `searchTree`, it stores alpha, beta value for each node and cuts off search accordingly.

`Board.java`: The Board for the checker game. The method `move` takes the current location and the goal location, moves the relevant pieces and updates the board. The method `isValidMove` checks whether a given move is valid. Other methods like `getAvailableMoves` reply on this method to get the collection of valid moves.

`BoardState.java`: The BoardState for minimax and alpha-beta pruning algorithms. The method `calcScore` calculate the score for the board which the max nodes want to maximize and the min nodes want to minimize.

`Piece.java`: The Piece for the checker game. The method `getAvailableMoves` returns all available moves and the method `getAvailableJumps` returns all available jumps.


## Getting Started
To get started you can simply run the `Game.java`. It will start a Human-AI game.

In order to see the AI-AI game, please run Player class instead. 

The Board row and column start from “0” and the format of playing the Human- AI game is to type 4 digit number such as 2130 (which means move the piece on position row 2 column 1 to position row 3 column 0) in the command line.

## Tunnel Checker Special Rules Implementation
We handle special rules for tunnel checker inside `isValidMove` and `getAvailableMovesAndJumps`. For `isValidMove`, we check whether the move is crossing the left/right border. If it is, we use different formula to perform calculation. For `getAvailableMovesAndJumps`, if the piece is located near left/right border, we will check  cross border moves.
