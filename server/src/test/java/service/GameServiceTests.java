package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameServiceTests {
    private UserService userService;
    private GameService gameService;

    @BeforeEach
    public void setup() throws DataAccessException {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        ClearService clearService = new ClearService(dataAccess);
        clearService.clear();
    }

    @Test
    public void createGameGood() throws DataAccessException {
        RegisterRequest regReq = new RegisterRequest("brighton", "pass", "email@test.com");
        RegisterResult regRes = userService.register(regReq);

        CreateGameRequest gameReq = new CreateGameRequest(regRes.authToken(), "My Chess Game");
        CreateGameResult gameRes = gameService.createGame(gameReq);

        Assertions.assertTrue(gameRes.gameID() > 0);
    }

    @Test
    public void createGameBad() {
        CreateGameRequest gameReq = new CreateGameRequest("bad_token", "My Chess Game");

        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(gameReq));
    }

    @Test
    public void listGamesGood() throws DataAccessException {
        RegisterRequest regReq = new RegisterRequest("brighton", "pass", "email@test.com");
        RegisterResult regRes = userService.register(regReq);

        CreateGameRequest gameReq = new CreateGameRequest(regRes.authToken(), "test_game");
        gameService.createGame(gameReq);

        ListGamesRequest listReq = new ListGamesRequest(regRes.authToken());
        ListGamesResult listRes = gameService.listGames(listReq);

        Assertions.assertFalse(listRes.games().isEmpty());
    }

    @Test
    public void listGamesBad() {
        ListGamesRequest listReq = new ListGamesRequest("bad_token");
        Assertions.assertThrows(DataAccessException.class, () -> gameService.listGames(listReq));
    }

    @Test
    public void joinGameGood() throws DataAccessException {
        RegisterRequest regReq = new RegisterRequest("brighton", "pass", "email@test.com");
        RegisterResult regRes = userService.register(regReq);

        CreateGameRequest gameReq = new CreateGameRequest(regRes.authToken(), "test_game");
        CreateGameResult gameRes = gameService.createGame(gameReq);

        JoinGameRequest joinReq = new JoinGameRequest(gameReq.authToken(), "WHITE", gameRes.gameID());

        gameService.joinGame(joinReq);

        Assertions.assertNotNull(joinReq.gameID());
    }

    @Test
    public void joinGameBad() throws DataAccessException {
        RegisterRequest regReq = new RegisterRequest("brighton", "pass", "email@test.com");
        RegisterResult regRes = userService.register(regReq);

        JoinGameRequest joinReq = new JoinGameRequest(regRes.authToken(), "WHITE", 9999);

        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(joinReq));
    }
}
