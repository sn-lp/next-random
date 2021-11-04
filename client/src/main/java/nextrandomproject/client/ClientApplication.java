package nextrandomproject.client;

import java.util.Scanner;

import nextrandomproject.core.Player;
import nextrandomproject.core.GameState;
import nextrandomproject.core.Status;
import nextrandomproject.core.PlayerTurnRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class ClientApplication {

	private static Player currentPlayer;

	public static void main(String[] args) {
		// catch CTRL+C to disconnect player from the game
		Signal.handle(new Signal("INT"), new SignalHandler() {
			public void handle(Signal sig) {
				if (ClientApplication.currentPlayer != null) {
					PlayerDisconnected();
				}
				System.out.println("Exiting and disconnecting playter due to CTRL+C");
				System.exit(0);
			}
		});

		Scanner scanner = new Scanner(System.in);
		System.out.println("Starting Connect 5 game!");
		System.out.println("First, enter your name:");
		String playerName = scanner.nextLine();

		ResponseEntity<Player> createPlayerResponse = CreatePlayer(playerName);
		if (createPlayerResponse == null || createPlayerResponse.getStatusCode() != HttpStatus.CREATED
				|| createPlayerResponse.getBody() == null) {
			System.out.println("Cannot create player to play Connect 5 game, exiting game.");
			scanner.close();
			return;
		}
		ClientApplication.currentPlayer = createPlayerResponse.getBody();

		Boolean gameIsRunning = true;
		Boolean waitingForNextTurn = false;
	
		while (gameIsRunning) {
			ResponseEntity<GameState> response = GetGameState();
			if (response == null || response.getStatusCode() != HttpStatus.OK) {
				System.out.println("Cannot get game state for Connect 5 game, exiting game.");
				scanner.close();
				return;
			}
			GameState gameState = response.getBody();
			// game is waiting for one player?
			if (gameState.getStatus() == Status.WaitingForOnePlayer) {
				try {
					if (!waitingForNextTurn) {
						waitingForNextTurn = true;
					} else {
						continue;
					}
					DisplayBoard(gameState.getBoard());
					System.out.println("Waiting for another player to join");
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					gameIsRunning = false;	 			
				} finally {
					continue;
				}
			}
			// game is running?
			else if (gameState.getStatus() == Status.Running) {
				if (gameState.getNextPlayerId() == ClientApplication.currentPlayer.getId()) {
					waitingForNextTurn = false;
					DisplayBoard(gameState.getBoard());
					int column;
					Boolean playSucceeded = false;
					while (!playSucceeded) {
						do {
							System.out.println("It's your turn, " + playerName + ", please enter column (1-9)");
							while (!scanner.hasNextInt()) {
								System.out.println("That's not a valid column, please try again!");
								scanner.next();
							}
							column = scanner.nextInt();
						} while (column < 1 || column > 9);

						ResponseEntity<Boolean> playerTurnResponse = MakePlay(ClientApplication.currentPlayer.getId(),
								column);
						if (playerTurnResponse == null || playerTurnResponse.getStatusCode() != HttpStatus.OK
								|| !playerTurnResponse.getBody()) {
							System.out.println("Play is not valid or server failed, please enter a new column.");
						} else {
							playSucceeded = true;
						}
					}
				} else {
					if (!waitingForNextTurn) {
						waitingForNextTurn = true;
					} else {
						continue;
					}
					DisplayBoard(gameState.getBoard());
					System.out.println("Waiting for opponent to play, will sleep for 2 seconds...");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						gameIsRunning = false;
					}
				}
			}
			// game is running?
			else if (gameState.getStatus() == Status.PlayerDisconnected) {
				System.out.println("The other player disconnected, ending the game :(");
				gameIsRunning = false;
			}
			// finished by tie, or because one player won
			else {
				DisplayBoard(gameState.getBoard());
				int playerId = ClientApplication.currentPlayer.getId();

				if (gameState.getStatus() == Status.Finished_PlayerOneWon && playerId == 1
						|| gameState.getStatus() == Status.Finished_PlayerTwoWon && playerId == 2) {
					System.out.println("\nYAY, YOU WON!!!\n");
				} else if (gameState.getStatus() == Status.Finished_Tie) {
					System.out.println("\nTie!\n");
				} else {
					System.out.println("\nYou lost, better luck next time!\n");
				}
				gameIsRunning = false;
			}
		}

		// game stopped and we need to close the Scanner
		scanner.close();
	}

	public static ResponseEntity<GameState> GetGameState() {
		try {
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<GameState> response = restTemplate.getForEntity("http://localhost:8080/game_status",
					GameState.class);
			return response;
		} catch (RestClientException e) {
			return null;
		}
	}

	public static ResponseEntity<Player> CreatePlayer(String playerName) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> request = new HttpEntity<>(playerName);
			ResponseEntity<Player> response = restTemplate.postForEntity("http://localhost:8080/create_player", request,
					Player.class);
			return response;
		} catch (RestClientException e) {
			return null;
		}
	}

	public static ResponseEntity<Boolean> MakePlay(int playerId, int column) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			PlayerTurnRequest playerTurnRequest = new PlayerTurnRequest();
			playerTurnRequest.setColumn(column);
			playerTurnRequest.setPlayerId(playerId);
			HttpEntity<PlayerTurnRequest> playerTurnHttpRequest = new HttpEntity<PlayerTurnRequest>(playerTurnRequest);
			return restTemplate.postForEntity("http://localhost:8080/player_turn", playerTurnHttpRequest,
					Boolean.class);
		} catch (RestClientException e) {
			return null;
		}
	}

	public static ResponseEntity<Boolean> PlayerDisconnected() {
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<Integer> request = new HttpEntity<>(ClientApplication.currentPlayer.getId());
			return restTemplate.postForEntity("http://localhost:8080/player_disconnect", request, Boolean.class);
		} catch (RestClientException e) {
			return null;
		}
	}

	public static void DisplayBoard(String[][] board) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				String cell = board[i][j];
				if (cell == "") {
					cell = " ";
				}
				System.out.print(String.format("[%s]", cell));
			}
			System.out.println();
		}
	}
}
