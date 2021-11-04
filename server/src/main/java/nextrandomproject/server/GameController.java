package nextrandomproject.server;

import nextrandomproject.core.Player;
import nextrandomproject.core.PlayerTurnRequest;
import nextrandomproject.core.Game;
import nextrandomproject.core.GameState;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.stereotype.Controller;


@Controller
public class GameController {
    private Game game = new Game();

    // create new player
    @RequestMapping(value="/create_player", method=RequestMethod.POST)
    public ResponseEntity<Player> createPlayer(@RequestBody String playerName){
        if(!game.CanAddPlayer(playerName)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Player newPlayer = game.AddPlayer(playerName);
        if (newPlayer == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Player>(newPlayer, HttpStatus.CREATED);
    }

    // make a playyer's turn on a game id
    @RequestMapping(value="/player_turn",method=RequestMethod.POST) 
	public ResponseEntity<Boolean> playerTurn(@RequestBody PlayerTurnRequest playerTurnRequest) {
        Boolean playerTurnExecuted = this.game.MakePlay(playerTurnRequest.getPlayerId(), playerTurnRequest.getColumn());

        return new ResponseEntity<Boolean>(playerTurnExecuted, HttpStatus.OK);
	}

    // get current game status
    @RequestMapping(value="/game_status",method=RequestMethod.GET) 
	public ResponseEntity<GameState> gameStatus() {
		GameState gameStatus = new GameState();
        gameStatus.setBoard(this.game.GetBoard());
        gameStatus.setNextPlayerId(this.game.GetNextPlayerId());
        gameStatus.setStatus(this.game.GetStatus());

        return new ResponseEntity<GameState>(gameStatus, HttpStatus.OK);
	}

    // make a playyer's turn on a game id
    @RequestMapping(value="/player_disconnect",method=RequestMethod.POST) 
	public ResponseEntity<Boolean> disconnectPlayer(@RequestBody int playerId) {
        Boolean playerDisconnected = this.game.DisconnectPlayer(playerId);

        return new ResponseEntity<Boolean>(playerDisconnected, HttpStatus.OK);
	}
}
