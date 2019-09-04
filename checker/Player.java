import java.util.*;

public class Player{
    private static int MAX_DEPTH = 4;
    private String pieceColor;
    private List<int[]> bestMove;
    
    public Player(String pieceColor) {
        this.pieceColor = pieceColor;
    }
    
    public String getPieceColor() {
        return this.pieceColor;
    }
    
    public boolean isBlackPlayer() {
        return getPieceColor() == Piece.BLACK;
    }
    
    private int searchTree(int depth, BoardState bs) {
        if (depth >= MAX_DEPTH || bs.gameOver()) {
            return bs.calcScore();
        }
        
        Player currentPlayer = bs.getCurrentPlayer();
        List<int[]> moves = getAllmoves(bs.getBoard(), currentPlayer);
        int maxScore = Integer.MIN_VALUE;
     
        for (int[] move : moves) {
            List<BoardState> newBsList = makeNewBoardState(bs, move, false);
            for (BoardState newBs: newBsList) {
                int score = searchTree(depth + 1, newBs);
                            
                if (score > maxScore) {
                    maxScore = score;
                    
                    if (depth == 0) {
                        bestMove = newBs.getMoveHistory();
                    }
                }

                BoardState parent = bs.getParent();
                        
                if (bs.isMaxNode()) {
                    bs.setAlpha(score);
                    if (parent != null && (parent.getBeta() <= bs.getAlpha())) {
                        return bs.updateScore(bs.getAlpha());
                    }
                } else {
                    bs.setBeta(score);
                    if (parent != null && (parent.getAlpha() >= bs.getBeta())) {
                        return bs.updateScore(bs.getBeta());
                    }
                }
            }
        }
        
        return bs.updateScore((bs.isMaxNode() ? bs.getAlpha() : bs.getBeta()));
    }
    
    private List<BoardState> makeNewBoardState(BoardState bs, int[] move, boolean chainJump) {
        List<BoardState> bsList = new ArrayList<>();
        Player currentPlayer = bs.getCurrentPlayer();
        Player rootPlayer = bs.getRootPlayer();
        
        Board board = bs.getBoard().cloneBoard();
        int result = board.move(move[0], move[1], move[2], move[3], currentPlayer);
 
        if (result == Board.MOVE_CHAIN_JUMP) {
            Piece piece = board.getPiece(move[2], move[3]);
            for (int [] jump: piece.getAvailableJumps(board, this)) {
                int[] curJump = new int[]{move[2], move[3], jump[0], jump[1]};
                BoardState parent = (chainJump ? bs.getParent() : bs);
                BoardState newBs = new BoardState(currentPlayer, board, rootPlayer, parent);
                cloneMoveHistory(bs, newBs);
                newBs.addToMoveHistory(move);
                
                List<BoardState> jumpBsList = makeNewBoardState(newBs, curJump, true);
                bsList.addAll(jumpBsList); 
            }
        } else if (result == Board.MOVE_COMPLETED) {
            if (chainJump) {
                BoardState newBs = new BoardState(bs.getOppoPlayer(currentPlayer), board, rootPlayer, bs.getParent());
                cloneMoveHistory(bs, newBs);
                newBs.addToMoveHistory(move);
                bsList.add(newBs);
            } else {          
                BoardState newBs = new BoardState(bs.getOppoPlayer(currentPlayer), board, rootPlayer, bs);
                newBs.addToMoveHistory(move);
                bsList.add(newBs);
            }
        }
                 
        return bsList;
    }
    
    private void cloneMoveHistory(BoardState pre, BoardState cur) {
        for (int[] preMove: pre.getMoveHistory()) {
            cur.addToMoveHistory(preMove);
        }
    }
    
    public List<int[]> getBestMove(Board board) {
        BoardState bs = new BoardState(this, board, this, null);
        searchTree(0, bs);

        return bestMove;
    }
    
    public List<int[]> getAllmoves(Board board, Player currentPlayer) {
        List<int[]> moves = new ArrayList<>();
        for (Piece piece : board.getPieces(currentPlayer.getPieceColor())) {
            for (int[] move: piece.getAvailableMovesAndJumps(board, currentPlayer)) {
                moves.add(new int[]{piece.getX(), piece.getY(), move[0], move[1]});
            }
        }
        
        return moves;
    }
       
    public static void main(String[] args) {
        Game game = new Game();
        game.simulatePlay();
    }
}