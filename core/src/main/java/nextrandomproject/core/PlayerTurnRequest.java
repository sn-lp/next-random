package nextrandomproject.core;

public class PlayerTurnRequest {
    private int playerId;
    private int column;

    public PlayerTurnRequest() {
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getColumn() {
        return this.column;
    }

    public int getPlayerId() {
        return this.playerId;
    }
}
