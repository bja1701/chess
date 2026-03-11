package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

public class MySqlDataAccessTests {

    private MySqlDataAccess dataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
        dataAccess.clear();
    }

    @Test
    public void clearTest() throws DataAccessException {
        UserData user = new UserData("testUser", "testPass", "test@email.com");
        dataAccess.createUser(user);

        dataAccess.clear();
        Assertions.assertNull(dataAccess.getUser("testUser"));
    }

    @Test
    public void createUserPositive() throws DataAccessException {
        UserData user = new UserData("newUser", "newPass", "new@email.com");
        dataAccess.createUser(user);
        UserData retrievedUser = dataAccess.getUser("newUser");
        Assertions.assertNotNull(retrievedUser);
        Assertions.assertEquals("newUser", retrievedUser.username());
    }

    @Test
    public void createUserNegative() throws DataAccessException {
        UserData user = new UserData("duplicateUser", "pass", "email");
        dataAccess.createUser(user);
        Assertions.assertThrows(DataAccessException.class, () -> dataAccess.createUser(user));
    }

    @Test
    public void getUserPositive() throws DataAccessException {
        UserData user = new UserData("findMe", "pass", "email@mail.com");
        dataAccess.createUser(user);

        UserData found = dataAccess.getUser("findMe");
        Assertions.assertNotNull(found);
        Assertions.assertEquals("findMe", found.username());
    }

    @Test
    public void getUserNegative() throws DataAccessException {
        UserData found = dataAccess.getUser("nonExistentUser");
        Assertions.assertNull(found);
    }

    @Test
    public void createAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("myToken", "myUser");
        dataAccess.createAuthToken(auth);

        AuthData found = dataAccess.getAuthToken("myToken");
        Assertions.assertNotNull(found);
        Assertions.assertEquals("myUser", found.username());
    }

    @Test
    public void createAuthNegative() throws DataAccessException {
        AuthData auth = new AuthData("duplicateToken", "myUser");
        dataAccess.createAuthToken(auth);
        Assertions.assertThrows(DataAccessException.class, () -> dataAccess.createAuthToken(auth));
    }

    @Test
    public void getAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("token123", "user123");
        dataAccess.createAuthToken(auth);
        AuthData found = dataAccess.getAuthToken("token123");
        Assertions.assertNotNull(found);
        Assertions.assertEquals("token123", found.authToken());
    }

    @Test
    public void getAuthNegative() throws DataAccessException {
        AuthData found = dataAccess.getAuthToken("fakeToken");
        Assertions.assertNull(found);
    }

    @Test
    public void deleteAuthPositive() throws DataAccessException {
        AuthData auth = new AuthData("deleteMe", "user");
        dataAccess.createAuthToken(auth);
        dataAccess.deleteAuth("deleteMe");
        AuthData found = dataAccess.getAuthToken("deleteMe");
        Assertions.assertNull(found);
    }

    @Test
    public void deleteAuthNegative() {
        Assertions.assertDoesNotThrow(() -> dataAccess.deleteAuth("fakeTokenThatDoesNotExist"));
    }

    @Test
    public void createGamePositive() throws DataAccessException {
        int gameId = dataAccess.createGame("My Awesome Game");
        Assertions.assertTrue(gameId > 0);
    }

    @Test
    public void createGameNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> dataAccess.createGame(null));
    }

    @Test
    public void getGamePositive() throws DataAccessException {
        int gameId = dataAccess.createGame("Find This Game");
        GameData found = dataAccess.getGame(gameId);
        Assertions.assertNotNull(found);
        Assertions.assertEquals("Find This Game", found.gameName());
    }

    @Test
    public void getGameNegative() throws DataAccessException {
        GameData found = dataAccess.getGame(9999);
        Assertions.assertNull(found);
    }

    @Test
    public void listGamesPositive() throws DataAccessException {
        dataAccess.createGame("Game 1");
        dataAccess.createGame("Game 2");
        dataAccess.createGame("Game 3");
        var games = dataAccess.listGames();
        Assertions.assertEquals(3, games.size());
    }

    @Test
    public void listGamesNegative() throws DataAccessException {
        var games = dataAccess.listGames();
        Assertions.assertNotNull(games);
        Assertions.assertTrue(games.isEmpty());
    }

    @Test
    public void updateGamePositive() throws DataAccessException {
        int gameId = dataAccess.createGame("Update Me");
        GameData original = dataAccess.getGame(gameId);
        GameData updated = new GameData(gameId, "WhitePlayer", original.blackUsername(), original.gameName(), original.game());
        dataAccess.updateGame(updated);
        GameData fetchedAgain = dataAccess.getGame(gameId);
        Assertions.assertEquals("WhitePlayer", fetchedAgain.whiteUsername());
    }

    @Test
    public void updateGameNegative() throws DataAccessException {
        int gameId = dataAccess.createGame("Valid Game");
        GameData badUpdate = new GameData(gameId, "White", "Black", null, new chess.ChessGame());
        Assertions.assertThrows(DataAccessException.class, () -> dataAccess.updateGame(badUpdate));
    }
}
