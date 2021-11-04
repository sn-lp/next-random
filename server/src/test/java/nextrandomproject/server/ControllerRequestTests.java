package nextrandomproject.server;


import nextrandomproject.core.GameState;
import nextrandomproject.core.Player;
import nextrandomproject.core.PlayerTurnRequest;
import nextrandomproject.core.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ControllerRequestTests {

    @LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void getInitialGameStatus() throws Exception {
		GameState gameState =this.restTemplate.getForObject("http://localhost:" + port + "/game_status",GameState.class);
        assertEquals(gameState.getNextPlayerId(), 1);
	}

    @Test
	public void createNewPlayersAndPlay() throws Exception {
        System.out.println("createNewPlayers");
        GameState gameState =this.restTemplate.getForObject("http://localhost:" + port + "/game_status",GameState.class);
        assertEquals(gameState.getStatus(), Status.WaitingForPlayers);
        HttpEntity<String> request = new HttpEntity<>("Alice");
		Player player = this.restTemplate.postForObject("http://localhost:" + port + "/create_player", request, Player.class);
        assertEquals(player.getId(), 1);
        assertEquals(player.getName(), "Alice");

        HttpEntity<String> bobRequest = new HttpEntity<>("Bob");
		player = this.restTemplate.postForObject("http://localhost:" + port + "/create_player", bobRequest, Player.class);
        assertEquals(player.getId(), 2);
        assertEquals(player.getName(), "Bob");

        gameState =this.restTemplate.getForObject("http://localhost:" + port + "/game_status",GameState.class);
        assertEquals(gameState.getStatus(), Status.Running);

        PlayerTurnRequest playerTurnRequest = new PlayerTurnRequest();
        playerTurnRequest.setColumn(2);
        playerTurnRequest.setPlayerId(1);
        HttpEntity<PlayerTurnRequest> entityRequest = new HttpEntity<>(playerTurnRequest);

        Boolean madePlay = this.restTemplate.postForObject("http://localhost:" + port + "/player_turn", entityRequest, Boolean.class);
        assertTrue(madePlay);
	}
}
