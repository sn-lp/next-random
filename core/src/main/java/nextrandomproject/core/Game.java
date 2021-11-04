package nextrandomproject.core;

import java.util.HashMap;

public class Game {

    private Status status;
    private int nextPlayerId;
    private HashMap<Integer, Player> players = new HashMap<>();
    private String[][] board = new String[6][9];

    public Game() {
        this.status = Status.WaitingForPlayers;
        this.nextPlayerId = 1;

        // initialize board
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = "";
            }
        }
    }

    private Boolean ValidateTurn(int playerId, int column) {
        if (!CanPlayerIdPlay(playerId)) {
            return false;
        }
        if (column < 1 || column > 9) {
            return false;
        }
        if (this.status != Status.Running) {
            return false;
        }
        // validate column is not full
        String boardPosition = this.board[0][column - 1];
        return boardPosition.isEmpty();
    }

    public Boolean MakePlay(int playerId, int column) {
        if (!ValidateTurn(playerId, column)) {
            return false;
        }

        for (int row = this.board.length - 1; row >= 0; row--) {
            if (this.board[row][column - 1].isEmpty()) {
                this.board[row][column - 1] = GetPlayerToken();
                break;
            }
        }
        nextPlayerId = CalculateNextPlayerId();

        String findWinner = FindWinner();
        if (findWinner == "X") {
            this.status = Status.Finished_PlayerOneWon;
        } else if (findWinner == "O") {
            this.status = Status.Finished_PlayerTwoWon;
        } else if (GameBoardIsFull()) {
            this.status = Status.Finished_Tie;
        }

        return true;
    }

    public Boolean DisconnectPlayer(int playerId) {
        System.out.println(players);
        if (playerId > 2 || playerId < 0 || !this.players.containsKey(playerId)) {
            return false;
        }

        this.status = Status.PlayerDisconnected;
        return true;
    }

    public Boolean CanAddPlayer(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        if (this.status == Status.Running || this.status == Status.Finished_PlayerOneWon
                || this.status == Status.Finished_PlayerTwoWon || this.status == Status.Finished_Tie) {
            return false;
        }
        if (this.players.size() == 2) {
            return false;
        }
        return true;
    }

    public Player AddPlayer(String name) {
        if (!CanAddPlayer(name)) {
            return null;
        }
        int playerId = this.players.size() + 1;
        Player player = new Player();
        player.setName(name);
        player.setId(playerId);
        this.players.put(playerId, player);

        if (this.players.size() == 1) {
            this.status = Status.WaitingForOnePlayer;
        } else if (this.players.size() == 2) {
            this.status = Status.Running;
        }
        return player;
    }

    public Status GetStatus() {
        return status;
    }

    public int GetNextPlayerId() {
        return nextPlayerId;
    }

    public String[][] GetBoard() {
        return board;
    }

    private String GetPlayerToken() {
        if (this.nextPlayerId == 1) {
            return "X";
        }
        return "O";
    }

    private int CalculateNextPlayerId() {
        if (this.nextPlayerId == 1) {
            return 2;
        }
        return 1;
    }

    private Boolean CanPlayerIdPlay(int playerId) {
        return playerId == this.nextPlayerId;
    }

    private String FindWinner() {
        // check horizontally
        String findWinnerInHorizontal = FindWinnerInHorizontal();
        if (findWinnerInHorizontal != "") {
            return findWinnerInHorizontal;
        }
        // check vertically
        String findWinnerInVertical = FindWinnerInVertical();
        if (findWinnerInVertical != "") {
            return findWinnerInVertical;
        }
        // check diagonally
        String findWinnerInDiagonal = FindWinnerInDiagonal();
        if (findWinnerInDiagonal != "") {
            return findWinnerInDiagonal;
        }
        return "";
    }

    private String FindWinnerInHorizontal() {
        String findWinner = "";
        int countXHorizontal = 0;
        int countOHorizontal = 0;
        for (int row = 0; row < this.board.length; row++) {
            for (int column = 0; column < this.board[row].length; column++) {
                if (countXHorizontal == 5) {
                    findWinner = "X";
                    break;
                }
                if (countOHorizontal == 5) {
                    findWinner = "O";
                    break;
                }
                if (this.board[row][column].isEmpty()) {
                    countXHorizontal = 0;
                    countOHorizontal = 0;
                    continue;
                }
                if (this.board[row][column] == "X") {
                    countXHorizontal += 1;
                } else {
                    countOHorizontal += 1;
                }
            }
            if (findWinner != "") {
                break;
            }
        }
        return findWinner;
    }

    private String FindWinnerInVertical() {
        String findWinner = "";
        int countXVertical = 0;
        int countOVertical = 0;

        // for each column go to each line
        for (int column = 0; column < this.board[0].length; column++) {
            for (int row = 0; row < this.board.length; row++) {
                if (countXVertical == 5) {
                    findWinner = "X";
                    break;
                }
                if (countOVertical == 5) {
                    findWinner = "O";
                    break;
                }
                if (this.board[row][column].isEmpty()) {
                    countXVertical = 0;
                    countOVertical = 0;
                    continue;
                }
                if (this.board[row][column] == "X") {
                    countXVertical += 1;
                } else {
                    countOVertical += 1;
                }
            }
            if (findWinner != "") {
                break;
            }
        }
        return findWinner;
    }

    private String FindWinnerInDiagonal() {
        String winner = FindWinnerInDiagonalLeft();
        if (winner != "") {
            return winner;
        }
        return FindWinnerInDiagonalRight();
    }

    private String FindWinnerInDiagonalRight() {
        String findWinner = "";
        int countXRightDiagonal = 0;
        int countORightDiagonal = 0;

        // only possible to have 5 in right diagonal using the last 2 rows
        for (int row = 0; row < 2; row++) {
            int middleRowPosition = (int) Math.floor(this.board[row].length / 2);
            for (int column = this.board[row].length - 1; column >= middleRowPosition; column--) {
                if (countXRightDiagonal == 5) {
                    findWinner = "X";
                    break;
                }
                if (countORightDiagonal == 5) {
                    findWinner = "O";
                    break;
                }
                if (this.board[row][column].isEmpty()) {
                    countXRightDiagonal = 0;
                    countORightDiagonal = 0;
                }
                if (this.board[row][column] == "X") {
                    countXRightDiagonal += 1;
                } else {
                    countORightDiagonal += 1;
                }

                int nextColumn = column - 1;
                for (int nextRow = row + 1; nextRow < this.board.length; nextRow++) {
                    if (nextColumn < 0) {
                        break;
                    }
                    if (countXRightDiagonal == 5) {
                        findWinner = "X";
                        break;
                    }
                    if (countORightDiagonal == 5) {
                        findWinner = "O";
                        break;
                    }
                    if (this.board[nextRow][nextColumn] == "X") {
                        countXRightDiagonal += 1;
                    } else if (this.board[nextRow][nextColumn] == "O") {
                        countORightDiagonal += 1;
                    } else if (this.board[nextRow][nextColumn].isEmpty()) {
                        countXRightDiagonal = 0;
                        countORightDiagonal = 0;
                    }
                    nextColumn--;
                }
            }
            if (findWinner != "") {
                break;
            }
        }
        return findWinner;
    }

    private String FindWinnerInDiagonalLeft() {
        String findWinner = "";
        int countXLeftDiagonal = 0;
        int countOLeftDiagonal = 0;

        // only possible to have 5 in left diagonal counting from 2 first rows
        for (int row = 0; row < 2; row++) {
            int middleRowPosition = (int) Math.floor(this.board[row].length / 2);
            for (int column = 0; column <= middleRowPosition; column++) {
                if (countXLeftDiagonal == 5) {
                    findWinner = "X";
                    break;
                }
                if (countOLeftDiagonal == 5) {
                    findWinner = "O";
                    break;
                }
                if (this.board[row][column].isEmpty()) {
                    countXLeftDiagonal = 0;
                    countOLeftDiagonal = 0;
                }
                if (this.board[row][column] == "X") {
                    countXLeftDiagonal += 1;
                } else {
                    countOLeftDiagonal += 1;
                }

                int nextColumn = column + 1;
                for (int nextRow = row + 1; nextRow < this.board.length; nextRow++) {
                    if (nextColumn > this.board[0].length - 1) {
                        break;
                    }
                    if (countXLeftDiagonal == 5) {
                        findWinner = "X";
                        break;
                    }
                    if (countOLeftDiagonal == 5) {
                        findWinner = "O";
                        break;
                    }
                    if (this.board[nextRow][nextColumn] == "X") {
                        countXLeftDiagonal += 1;
                    } else if (this.board[nextRow][nextColumn] == "O") {
                        countOLeftDiagonal += 1;
                    } else if (this.board[nextRow][nextColumn].isEmpty()) {
                        countXLeftDiagonal = 0;
                        countOLeftDiagonal = 0;
                    }
                    nextColumn++;
                }
            }
            if (findWinner != "") {
                break;
            }
        }
        return findWinner;
    }

    private Boolean GameBoardIsFull() {
        Boolean tie = true;
        for (int row = 0; row < this.board.length; row++) {
            for (int column = 0; column < this.board[row].length; column++) {
                if (this.board[row][column] == "") {
                    tie = false;
                    break;
                }
            }
            if (!tie) {
                break;
            }
        }
        return tie;
    }

}
