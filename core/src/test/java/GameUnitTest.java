
import org.junit.Before;
import org.junit.Assert;
import org.junit.Test;

import nextrandomproject.core.Game;
import nextrandomproject.core.Status;
import nextrandomproject.core.Player;

public class GameUnitTest {

    static Game game;

    @Before
    public void setUp() {
        game = new Game();
    }

    @Test
    public void testInitialGameState() {
        Assert.assertEquals(Status.WaitingForPlayers, game.GetStatus());
        Assert.assertEquals(1, game.GetNextPlayerId());

        String[][] board = game.GetBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Assert.assertEquals("", board[i][j]);
            }
        }
    }

    @Test
    public void testAddPlayers() {
        Player playerOne = game.AddPlayer("Alice");
        Assert.assertNotNull(playerOne);
        Assert.assertEquals("Alice", playerOne.getName());
        Assert.assertEquals(1, playerOne.getId());

        Assert.assertEquals(Status.WaitingForOnePlayer, game.GetStatus());

        Player playerTwo = game.AddPlayer("Bob");
        Assert.assertNotNull(playerTwo);
        Assert.assertEquals("Bob", playerTwo.getName());
        Assert.assertEquals(2, playerTwo.getId());

        Assert.assertEquals(Status.Running, game.GetStatus());

        Player playerThree = game.AddPlayer("Eve");
        Assert.assertNull(playerThree);

    }

    @Test
    public void testWrongMakePlay() {
        Player playerOne = game.AddPlayer("Alice");
        Assert.assertNotNull(playerOne);

        Player playerTwo = game.AddPlayer("Bob");
        Assert.assertNotNull(playerTwo);

        // column is out of bounds
        Assert.assertFalse(game.MakePlay(1, -10));

        // column is out of bounds
        Assert.assertFalse(game.MakePlay(1, 10));

        // column is out of bounds
        Assert.assertFalse(game.MakePlay(1, 0));

        // wrong player to play
        Assert.assertFalse(game.MakePlay(2, 1));
    }

    @Test
    public void testAlternatePlay() {
        Player playerOne = game.AddPlayer("Alice");
        Assert.assertNotNull(playerOne);

        Player playerTwo = game.AddPlayer("Bob");
        Assert.assertNotNull(playerTwo);

        Assert.assertTrue(game.GetNextPlayerId() == 1);
        Assert.assertTrue(game.MakePlay(1, 2));
        Assert.assertTrue(game.GetBoard()[5][1] == "X");

        Assert.assertTrue(game.GetNextPlayerId() == 2);
        Assert.assertTrue(game.MakePlay(2, 1));
        Assert.assertTrue(game.GetBoard()[5][0] == "O");

        Assert.assertTrue(game.GetNextPlayerId() == 1);
        Assert.assertTrue(game.MakePlay(1, 8));
        Assert.assertTrue(game.GetBoard()[5][7] == "X");
    }

    @Test
    public void testHorizontalWin() {
        Player playerOne = game.AddPlayer("Alice");
        Assert.assertNotNull(playerOne);

        Player playerTwo = game.AddPlayer("Bob");
        Assert.assertNotNull(playerTwo);

        Assert.assertTrue(game.GetNextPlayerId() == 1);
        Assert.assertTrue(game.MakePlay(1, 1));
        Assert.assertTrue(game.GetBoard()[5][0] == "X");

        Assert.assertTrue(game.GetNextPlayerId() == 2);
        Assert.assertTrue(game.MakePlay(2, 1));
        Assert.assertTrue(game.GetBoard()[4][0] == "O");

        Assert.assertTrue(game.GetNextPlayerId() == 1);
        Assert.assertTrue(game.MakePlay(1, 2));
        Assert.assertTrue(game.GetBoard()[5][1] == "X");

        Assert.assertTrue(game.GetNextPlayerId() == 2);
        Assert.assertTrue(game.MakePlay(2, 2));
        Assert.assertTrue(game.GetBoard()[4][1] == "O");

        Assert.assertTrue(game.GetNextPlayerId() == 1);
        Assert.assertTrue(game.MakePlay(1, 3));
        Assert.assertTrue(game.GetBoard()[5][2] == "X");

        Assert.assertTrue(game.GetNextPlayerId() == 2);
        Assert.assertTrue(game.MakePlay(2, 3));
        Assert.assertTrue(game.GetBoard()[4][2] == "O");

        Assert.assertTrue(game.GetNextPlayerId() == 1);
        Assert.assertTrue(game.MakePlay(1, 4));
        Assert.assertTrue(game.GetBoard()[5][3] == "X");

        Assert.assertTrue(game.GetNextPlayerId() == 2);
        Assert.assertTrue(game.MakePlay(2, 4));
        Assert.assertTrue(game.GetBoard()[4][3] == "O");

        Assert.assertTrue(game.GetStatus() == Status.Running);

        Assert.assertTrue(game.GetNextPlayerId() == 1);
        Assert.assertTrue(game.MakePlay(1, 5));
        Assert.assertTrue(game.GetBoard()[5][4] == "X");

        Assert.assertTrue(game.GetStatus() == Status.Finished_PlayerOneWon);
    }

    @Test
    public void testVerticalWin() {
        Player playerOne = game.AddPlayer("Alice");
        Assert.assertNotNull(playerOne);

        Player playerTwo = game.AddPlayer("Bob");
        Assert.assertNotNull(playerTwo);

        Assert.assertTrue(game.GetNextPlayerId() == 1);
        Assert.assertTrue(game.MakePlay(1, 1));
        Assert.assertTrue(game.GetBoard()[5][0] == "X");

        Assert.assertTrue(game.GetNextPlayerId() == 2);
        Assert.assertTrue(game.MakePlay(2, 5));
        Assert.assertTrue(game.GetBoard()[5][4] == "O");

        Assert.assertTrue(game.GetNextPlayerId() == 1);
        Assert.assertTrue(game.MakePlay(1, 2));
        Assert.assertTrue(game.GetBoard()[5][1] == "X");

        Assert.assertTrue(game.GetNextPlayerId() == 2);
        Assert.assertTrue(game.MakePlay(2, 5));
        Assert.assertTrue(game.GetBoard()[4][4] == "O");

        Assert.assertTrue(game.GetNextPlayerId() == 1);
        Assert.assertTrue(game.MakePlay(1, 1));
        Assert.assertTrue(game.GetBoard()[4][0] == "X");

        Assert.assertTrue(game.GetNextPlayerId() == 2);
        Assert.assertTrue(game.MakePlay(2, 5));
        Assert.assertTrue(game.GetBoard()[3][4] == "O");

        Assert.assertTrue(game.GetNextPlayerId() == 1);
        Assert.assertTrue(game.MakePlay(1, 4));
        Assert.assertTrue(game.GetBoard()[5][3] == "X");

        Assert.assertTrue(game.GetNextPlayerId() == 2);
        Assert.assertTrue(game.MakePlay(2, 5));
        Assert.assertTrue(game.GetBoard()[2][4] == "O");

        Assert.assertTrue(game.GetStatus() == Status.Running);

        Assert.assertTrue(game.GetNextPlayerId() == 1);
        Assert.assertTrue(game.MakePlay(1, 7));
        Assert.assertTrue(game.GetBoard()[5][6] == "X");

        Assert.assertTrue(game.GetNextPlayerId() == 2);
        Assert.assertTrue(game.MakePlay(2, 5));
        Assert.assertTrue(game.GetBoard()[1][4] == "O");

        Assert.assertTrue(game.GetStatus() == Status.Finished_PlayerTwoWon);
    }

    @Test
    public void testDiagonallWin() {
        Player playerOne = game.AddPlayer("Alice");
        Assert.assertNotNull(playerOne);

        Player playerTwo = game.AddPlayer("Bob");
        Assert.assertNotNull(playerTwo);

        Assert.assertTrue(game.MakePlay(1, 1));
        Assert.assertTrue(game.MakePlay(2, 1));
        Assert.assertTrue(game.MakePlay(1, 1));
        Assert.assertTrue(game.MakePlay(2, 1));
        Assert.assertTrue(game.MakePlay(1, 1));
        
        Assert.assertTrue(game.MakePlay(2, 2));
        Assert.assertTrue(game.MakePlay(1, 2));
        Assert.assertTrue(game.MakePlay(2, 2));
        Assert.assertTrue(game.MakePlay(1, 2));

        Assert.assertTrue(game.MakePlay(2, 6));
        Assert.assertTrue(game.MakePlay(1, 3));
        Assert.assertTrue(game.MakePlay(2, 3));
        Assert.assertTrue(game.MakePlay(1, 3));

        Assert.assertTrue(game.MakePlay(2, 4));
        Assert.assertTrue(game.MakePlay(1, 4));
        Assert.assertTrue(game.MakePlay(2, 6));

        Assert.assertTrue(game.GetStatus() == Status.Running);
        Assert.assertTrue(game.MakePlay(1, 5));

        Assert.assertTrue(game.GetStatus() == Status.Finished_PlayerOneWon);
    }

    @Test
    public void testDiagonallInOtherDirectionWin() {
        Player playerOne = game.AddPlayer("Alice");
        Assert.assertNotNull(playerOne);

        Player playerTwo = game.AddPlayer("Bob");
        Assert.assertNotNull(playerTwo);

        Assert.assertTrue(game.MakePlay(1, 1));
        Assert.assertTrue(game.MakePlay(2, 2));
        Assert.assertTrue(game.MakePlay(1, 2));
        Assert.assertTrue(game.MakePlay(2, 3));
        Assert.assertTrue(game.MakePlay(1, 3));
        
        Assert.assertTrue(game.MakePlay(2, 4));
        Assert.assertTrue(game.MakePlay(1, 3));
        Assert.assertTrue(game.MakePlay(2, 4));
        Assert.assertTrue(game.MakePlay(1, 5));
        Assert.assertTrue(game.MakePlay(2, 4));
        Assert.assertTrue(game.MakePlay(1, 4));
        Assert.assertTrue(game.MakePlay(2, 5));
        Assert.assertTrue(game.MakePlay(1, 5));
        Assert.assertTrue(game.MakePlay(2, 5));

        Assert.assertTrue(game.GetStatus() == Status.Running);
        Assert.assertTrue(game.MakePlay(1, 5));
	
        Assert.assertTrue(game.GetStatus() == Status.Finished_PlayerOneWon);
    }
}
