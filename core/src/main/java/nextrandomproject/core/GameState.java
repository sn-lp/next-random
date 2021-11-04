package nextrandomproject.core;

public class GameState {
    private Status status;
    private int nextPlayerId;
    private String[][] board;

    public GameState() {
    }

    public Status getStatus() {
        return status;
    }

    public int getNextPlayerId() {
        return nextPlayerId;
    }

    public String[][] getBoard() {
        return board;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setNextPlayerId(int nextPlayerId) {
        this.nextPlayerId = nextPlayerId;
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }
}
