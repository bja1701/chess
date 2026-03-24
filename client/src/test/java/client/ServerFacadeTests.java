package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    void clear() throws Exception {
        facade.clearDatabase();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("Positive Register Test")
    void registerSuccess() throws Exception {
        AuthData authData = facade.register("player1", "password", "p1@email.com");
        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.authToken());
        Assertions.assertEquals("player1", authData.username());
    }

    @Test
    @DisplayName("Negative Register Test")
    void registerFailDuplicateUser() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertThrows(Exception.class, () -> {
            facade.register("player1", "password", "p1@email.com");
        });
    }

    @Test
    @DisplayName("Positive Login Test")
    void loginSuccess() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        AuthData authData = facade.login("player1", "password");
        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.authToken());
    }

    @Test
    @DisplayName("Negative Login Test")
    void loginFailWrongPassword() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertThrows(Exception.class, () -> {
            facade.login("player1", "badpass");
        });
    }

    @Test
    @DisplayName("Create Game Positive Test")
    void createGameSuccess() throws Exception {
        AuthData authData = facade.register("player1", "password", "p1@email.com");
        var result = facade.createGame("My Chess Game", authData.authToken());
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Create Game Negative Test")
    void createGameFailBadToken() {
        Assertions.assertThrows(Exception.class, () -> {
            facade.createGame("My Chess Game", "fakeToken");
        });
    }

    @Test
    @DisplayName("Positive Logout Test")
    void logoutSuccess() throws Exception {
        AuthData authData = facade.register("player1", "password", "p1@email.com");
        Assertions.assertDoesNotThrow(() -> {
            facade.logout(authData.authToken());
        });
    }

    @Test
    @DisplayName("Negative Logout Test")
    void logoutFailBadToken() {
        Assertions.assertThrows(Exception.class, () -> {
            facade.logout("faketoken");
        });
    }

    @Test
    @DisplayName("Positive List Games Test")
    void listGamesSuccess() throws Exception {
        AuthData authData = facade.register("player1", "password", "p1@email.com");
        facade.createGame("My Game", authData.authToken());
        var result = facade.listGames(authData.authToken());
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.games());
    }

    @Test
    @DisplayName("Negative List Games Test")
    void listGamesFailBadToken() {
        Assertions.assertThrows(Exception.class, () -> {
            facade.listGames("faketoken");
        });
    }
}
