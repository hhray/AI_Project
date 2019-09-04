import java.util.*;

public class Game{
    private static Game instance;
    private Board board;
    private int turn;
    private Player currentPlayer, blackPlayer, whitePlayer;

    public Game() {
        board = new Board();
        blackPlayer = new Player(Piece.BLACK);
        whitePlayer = new Player(Piece.WHITE);
        currentPlayer = blackPlayer;
        instance = this;
    }
    
    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }
    
    public void play() {
        turn = 1;
        while (!gameOver()) {
            getHumanMove();
            if(gameOver()) break;
            switchPlayer();
            
            getAIMove();
            switchPlayer();
            
            turn++;
        }
    }
    
    public void getAIMove() {
        List<int[]> moves = currentPlayer.getBestMove(board);
        for (int[] move: moves) {
            int x = move[0], y = move[1], goalX = move[2], goalY = move[3];
            int result = board.move(x, y, goalX, goalY, currentPlayer);
            
            printMove(x, y, goalX, goalY);
            board.printBoard();
            
            if (result == Board.MOVE_ILLEGAL) {
                System.out.println("Illegal move. Ignored");
            }
        }
    }
    
    public void getHumanMove() {
        String color = isBlackTurn() ? "Black" : "white";
        System.out.println(String.format("Enter your move (%s): ", color));
        Scanner scanner = new Scanner(System.in);
        int move[] = Arrays.stream(scanner.nextLine().split(""))
                           .mapToInt(Integer::parseInt).toArray();
        
        int x = move[0], y = move[1], goalX = move[2], goalY = move[3];
        int result = board.move(x, y, goalX, goalY, currentPlayer);
            
        printMove(x, y, goalX, goalY);
        board.printBoard();
        
        if (result == Board.MOVE_CHAIN_JUMP) {
            getHumanMove();
        } else if (result == Board.MOVE_ILLEGAL) {
            System.out.println("Illegal move. Ignored");
        }
    }

    public void simulatePlay(){
        turn = 1;
        while (!gameOver()) {
            getAIMove();
            if(gameOver()) break;
            switchPlayer();
            
            getAIMove();
            switchPlayer();
            turn++;
        }
        board.printBoard();
    }
    
    public boolean isBlackTurn(){
        return currentPlayer == blackPlayer;
    }
    
    public void switchPlayer() {
        currentPlayer = isBlackTurn() ? whitePlayer : blackPlayer;
    }
    
    public boolean gameOver() {
        List<Piece> blackPieces = board.getPieces(Piece.BLACK);
        List<Piece> whitePieces = board.getPieces(Piece.WHITE);
        
        if (blackPieces.size() == 0 || whitePieces.size() == 0) {
            return true;
        }
        
        List<Piece> currentPieces = isBlackTurn() ? blackPieces : whitePieces;
        
        for (Piece piece : currentPieces) {
            if (piece.hasAvailableMoves(board, currentPlayer)){
                return false;
            }
        }
        
        return true;
    }
    
    public Player getWhitePlayer() {
        return whitePlayer;
    }
    
    public Player getBlackPlayer() {
        return blackPlayer;
    }
    
    private void printMove(int x, int y, int goalX, int goalY) {
        System.out.println(String.format("Turn %d: %d,%d -> %d,%d", turn, x, y, goalX, goalY));
    }
    
    public static void main(String[] args) {
        Game game = new Game();
        game.play();
    }
}