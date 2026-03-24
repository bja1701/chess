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

}
