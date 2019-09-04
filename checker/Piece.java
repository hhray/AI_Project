import java.util.*;

public class Piece{
    private int x, y;
    private boolean king;
    private String color;
    private String displayColor;
    private boolean kingRowReached = false;

    public static String BLACK = "b";
    public static String KING_BLACK = "B";
    public static String WHITE = "w";
    public static String KING_WHITE = "W";
    
    private List<Integer> jumpStartColumns = Arrays.asList(0, 1, 6, 7);
    private List<Integer> jumpEndColumns = Arrays.asList(6, 7, 0, 1);
    
    private List<Integer> moveStartColumns = Arrays.asList(0, 7);
    private List<Integer> moveEndColumns = Arrays.asList(7, 0);
    
    public Piece(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.displayColor = color;
        this.king = false;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setX(int x) {
        int kingRow = isBlack() ? Board.BOARD_ROWS - 1 : 0;
        String kingColor = isBlack() ? KING_BLACK : KING_WHITE;
        if (x == kingRow && !kingRowReached) {
            this.king = true;
            this.displayColor = kingColor;
            this.kingRowReached = true;
        }
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public String getColor() {
        return color;
    }
    
    public String getDisplayColor() {
        return displayColor;
    }
    
    public boolean isBlack() {
        return this.color == Piece.BLACK;
    }
    
    public boolean isKing() {
        return king;
    }
    
    public boolean hasAvailableMoves(Board board, Player currentPlayer) {
        return getAvailableMovesAndJumps(board, currentPlayer).size() > 0;
    }
    
    public List<int[]> getAvailableJumps(Board board, Player currentPlayer) {
        return getAvailableMoves(board, true, currentPlayer);
    }
    
    public List<int[]> getAvailableMovesAndJumps(Board board, Player currentPlayer) {
        List<int[]> result = new ArrayList<>(getAvailableJumps(board, currentPlayer));
        result.addAll(getAvailableMoves(board, false, currentPlayer));
        return result;
    }
    
    private void addToMoves(Board board, List<int[]> result, int x, int y, int newX, int newY, Player currentPlayer) {
        if (board.isValidMove(x, y, newX, newY, currentPlayer)) {
            result.add(new int[]{newX, newY});
        }
    }
    
    public List<int[]> getAvailableMoves(Board board, boolean jump, Player currentPlayer) {
        List<int[]> result = new ArrayList<>();
        int start = jump ? -2 : -1;
        int end = jump ? 2 : 1;
        int step = jump? 4 : 2;
        
        List<Integer> startColumns = jump? jumpStartColumns : moveStartColumns;
        List<Integer> endColumns = jump? jumpEndColumns : moveEndColumns;
        
        for (int i = start; i <= end; i += step) {
            for (int j = start; j <= end; j += step) {
                addToMoves(board, result, this.x, this.y, this.x + i, this.y + j, currentPlayer);
            }
        }
        
        if (isCrossBorderPossible(this.y, jump)) {
            for (int deltaX: new int[]{start, end}) {
                addToMoves(board, result, this.x, this.y, this.x + deltaX, 
                           endColumns.get(startColumns.indexOf(this.y)), currentPlayer);
            }
        }
            
        return result;
    }
    
    private boolean isCrossBorderPossible(int y, boolean jump) {
        List<Integer> startColumns = jump? jumpStartColumns : moveStartColumns;
        return startColumns.indexOf(y) != -1;
    }
    
    public Piece clonePiece() {
        return new Piece(getX(), getY(), getColor());
    }
}