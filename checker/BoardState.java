import java.util.*;

public class BoardState{
    private Player currentPlayer, oppoPlayer, rootPlayer;
    private Board currentBoard;
    private BoardState parent;
    private List<int[]> moveHistory;
    private int alpha = Integer.MIN_VALUE;
    private int beta = Integer.MAX_VALUE;
    private int score;
    
    public BoardState(Player currentPlayer, Board board, Player rootPlayer, BoardState parent) {
        this.currentPlayer = currentPlayer;
        this.oppoPlayer = getOppoPlayer(currentPlayer);
        this.rootPlayer = rootPlayer;
        this.currentBoard = board.cloneBoard();
        this.moveHistory = new ArrayList<>();
        this.score = isMaxNode() ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        this.parent = parent;
    }
    
    public Player getOppoPlayer(Player player) {
        return player.isBlackPlayer() ? 
            Game.getInstance().getWhitePlayer() : Game.getInstance().getBlackPlayer();
    }
    
    public int calcScore() {
        int blackScore = 0, whiteScore = 0;
        int blackMen = 0, whiteMen = 0, blackKing = 0, whiteKing = 0;
        
        for (int row = 0; row < Board.BOARD_ROWS; row++) {
            for (int col = 0; col < Board.BOARD_COLUMNS; col++) {
                Piece piece = currentBoard.getPiece(row, col);
                
                if (piece == null) {
                    continue;
                }
                
                if (piece.isBlack()) {
                    if (piece.isKing()) {
                        blackKing++;
                    } else {
                        blackMen++;
                    }
                } else {
                    if (piece.isKing()) {
                        whiteKing++;
                    } else {
                        whiteMen++;
                    }
                }     
            }
        }
        
        blackScore = blackMen + 2 * blackKing - whiteMen - 2 * whiteKing;
        whiteScore = -blackScore;
        score = (rootPlayer.getPieceColor() == Piece.BLACK ? blackScore : whiteScore);

        return score;
    }
    
    public void addToMoveHistory(int[] move){
        moveHistory.add(move);
    }
    
    public List<int[]> getMoveHistory() {
        return moveHistory;
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public Player getRootPlayer() {
        return rootPlayer;
    }
    
    public Board getBoard(){
        return currentBoard;
    }
    
    public boolean isMaxNode() {
        return currentPlayer == rootPlayer;
    }
    
    public int getAlpha() {
        return alpha;
    }
    
    public int getBeta() {
        return beta;
    }
    
    public void setAlpha(int alpha) {
        this.alpha = Math.max(alpha, this.alpha);
    }
    
    public void setBeta(int beta) {
        this.beta = Math.min(beta, this.beta);
    }
    
    public int getScore() {
        return score;
    }
    
    public int updateScore(int score) {
        this.score = score;
        return this.score;
    } 
    
    public BoardState getParent() {
        return parent;
    }

    public boolean gameOver() {
        return currentBoard.getPieces(Piece.BLACK).size() == 0 ||
            currentBoard.getPieces(Piece.WHITE).size() == 0;
    }
}