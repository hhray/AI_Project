import java.util.*;

public class Board{
    public static int BOARD_ROWS = 8;
    public static int BOARD_COLUMNS = 8;
    public static int PIECE_ROWS = 3;
    
    public static int MOVE_CHAIN_JUMP = 2;
    public static int MOVE_COMPLETED = 1;
    public static int MOVE_ILLEGAL = 0;
    
    private Piece[][] board;
   
    public Board() {
        board = new Piece[BOARD_ROWS][BOARD_COLUMNS];
        for (int i = 0; i < PIECE_ROWS; i++) {
            for (int j = 0; j < BOARD_COLUMNS; j++) {
                if ((i + j) % 2 != 0) {
                    board[i][j] = new Piece(i, j, Piece.BLACK);
                }
            }
        }
            
        
        for (int i = BOARD_ROWS - PIECE_ROWS; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLUMNS; j++) {
                if ((i + j) % 2 != 0) {
                    board[i][j] = new Piece(i, j, Piece.WHITE);
                }
            }
        }
    }
    
    public int move(int x, int y, int goalX, int goalY, Player currentPlayer) {
        if (isValidMove(x, y, goalX, goalY, currentPlayer)) {
            setPiece(goalX, goalY, getPiece(x, y));
            setPiece(x, y, null);
            
            if (isJump(x, y, goalX, goalY)) {
                int midX = calcMidX(x, goalX), midY = calcMidY(y, goalY);
                setPiece(midX, midY, null);
                Piece piece = getPiece(goalX, goalY);
                List<int[]> availableJumps = piece.getAvailableJumps(this, currentPlayer);
                int availableJumpsCount = availableJumps.size();
                if (availableJumpsCount > 1) {
                    return MOVE_CHAIN_JUMP;
                } else if (availableJumpsCount == 1) {
                    int[] goal = availableJumps.get(0);
                    move(goalX, goalY, goal[0], goal[1], currentPlayer);
                }
            }
            return MOVE_COMPLETED;
        } else {
            return MOVE_ILLEGAL;
        }
    }
    
    public boolean isValidMove(int x, int y, int goalX, int goalY, Player currentPlayer) {
        if (!inBounds(x, y) || !inBounds(goalX, goalY)) {
            return false;
        }
            
        Piece start = getPiece(x, y);
        Piece end = getPiece(goalX, goalY);
        
        if (start == null || end != null) {
            return false;
        }
        
        if (start.getColor() != currentPlayer.getPieceColor()) {
            return false;
        }

        int deltaX = calcDeltaX(x, goalX), deltaY = calcDeltaY(y, goalY);
        int absDeltaX = Math.abs(deltaX), absDeltaY = Math.abs(deltaY);
        boolean jump = isJump(x, y, goalX, goalY);
        int maxDistance = jump ? 2 : 1;
        
        if (absDeltaX != maxDistance || absDeltaY != maxDistance) {
            return false;
        }
        
        if (!start.isKing()) {
            if (start.isBlack() && deltaX >= 0) {
                return false;
            } else if (!start.isBlack() && deltaX <= 0) {
                return false;
            }
        }
        
        return jump ? isValidJump(x, y, goalX, goalY) : true;
    }
    
    private boolean isValidJump(int x, int y, int goalX, int goalY) {
        int midX = calcMidX(x, goalX), midY = calcMidY(y, goalY);
        if (!inBounds(midX, midY)) {
            return false;
        }
        
        Piece midPiece = getPiece(midX, midY);
        Piece start = getPiece(x, y), end = getPiece(goalX, goalY);
        if (midPiece == null) {
            return false;
        }
        
        if (midPiece.getColor() == start.getColor()) {
            return false;
        }
        
        if ((x + 1 == midX) && (x + 2 != goalX)) {
            return false;
        }
        
        if ((x - 1 == midX) && (x - 2 != goalX)) {
            return false;
        }
        return true;
    }
    
    private static boolean isJump(int x, int y, int goalX, int goalY) {
        int absDeltaX = Math.abs(calcDeltaX(x, goalX));
        int absDeltaY = Math.abs(calcDeltaY(y, goalY));
        return absDeltaX == 2 && absDeltaY == 2;
    }

    private static int calcDeltaX(int x, int goalX) {
        return x - goalX;
    }
    
    private static int calcDeltaY(int y, int goalY) {
        int deltaY = y - goalY, absDeltaY = Math.abs(deltaY);
        int tunnelDeltaY = deltaY < 0 ? absDeltaY - BOARD_COLUMNS : BOARD_COLUMNS - absDeltaY; 
        return absDeltaY > 2 ? tunnelDeltaY : deltaY;
    }
    
    private int calcMidX(int x, int goalX) {
        return (x + goalX) / 2;
    }
    
    private int calcMidY(int y, int goalY) {
        int midY;
        if (crossRightBorderJump(y, goalY)) {
            midY = (y + 1) % BOARD_COLUMNS;
        } else if (crossLeftBorderJump(y, goalY)) {
            midY = (y - 1 + BOARD_COLUMNS) % BOARD_COLUMNS;
        } else {
            midY = (y + goalY) / 2;
        }
        return midY;
    }
    
    private boolean crossRightBorderJump(int y, int goalY) {
        return (y == 6 && goalY == 0) || (y == 7 && goalY == 1);
    }
    
    private boolean crossLeftBorderJump(int y, int goalY) {
        return (y == 0 && goalY == 6) || (y == 1 && goalY == 7);
    }
    
    public Piece getPiece(int x, int y) {
        return inBounds(x, y) ? board[x][y] : null;
    }
    
    public void setPiece(int x, int y, Piece piece) {
        if (inBounds(x, y)) {
            board[x][y] = piece;
            if (piece != null) {
                piece.setX(x);
                piece.setY(y);
            }
        }
    }
    
    private static boolean inBounds(int x, int y) {
        return x < BOARD_ROWS && x >= 0 && y < BOARD_COLUMNS && y >= 0;
    }
    
    public List<Piece> getPieces(String pieceColor) {
        List<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLUMNS; j++) {
                Piece piece = getPiece(i, j);
                if (piece != null && piece.getColor() == pieceColor) {
                    pieces.add(piece);
                }
            }
        }
        return pieces; 
    }

    public void printBoard() {
        for (int i = 0; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLUMNS; j++) {
                Piece p = getPiece(i, j);
                if (p instanceof Piece) {
                    System.out.print("|" + p.getDisplayColor());
                } else {
                    System.out.print("| ");
                }
            }
            
            System.out.print("|\n");
        }
    }
    
    public Board cloneBoard(){
       Board newBoard = new Board();
       for (int i = 0; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLUMNS; j++) {
                Piece piece = getPiece(i, j);
                newBoard.setPiece(i, j, (piece == null ? null : piece.clonePiece()));
            }
       }
       return newBoard;
    }
    
    public static void main(String[] args) {
        Board b = new Board();
    }
}