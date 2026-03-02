package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTests {
    private UserService userService;

    @BeforeEach
    public void setup() throws DataAccessException{
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        ClearService clearService = new ClearService(dataAccess);
        clearService.clear();
    }

    @Test
    public void registerGood() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("brighton", "pass", "email@test.com");
        RegisterResult result = userService.register(request);

        Assertions.assertNotNull(result.authToken());
        Assertions.assertEquals("brighton", result.username());
    }

    @Test
    public void registerBad() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("brighton", "pass", "email@test.com");
        userService.register(request);

        Assertions.assertThrows(DataAccessException.class, () -> {
            userService.register(request);
        });
    }

    @Test
    public void loginGood() throws DataAccessException {
        RegisterRequest regReq = new RegisterRequest("brighton", "pass", "email@test.com");
        userService.register(regReq);

        LoginRequest request = new LoginRequest("brighton", "pass");
        LoginResult result = userService.login(request);

        Assertions.assertNotNull(result.authToken());
        Assertions.assertEquals("brighton", result.username());
    }

    @Test
    public void loginBad() throws DataAccessException {
        RegisterRequest regReq = new RegisterRequest("brighton", "pass", "email@test.com");
        userService.register(regReq);

        LoginRequest request = new LoginRequest("brighton", "wrong_pass");

        Assertions.assertThrows(DataAccessException.class, () -> {
            userService.login(request);
        });
    }

    @Test
    public void logoutGood() throws DataAccessException {
        RegisterRequest regReq = new RegisterRequest("brighton", "pass", "email@test.com");
        RegisterResult result = userService.register(regReq);

        LogoutRequest request = new LogoutRequest(result.authToken());

        userService.logout(request);

        Assertions.assertNotNull(result.authToken());
        Assertions.assertEquals("brighton", result.username());
    }

    @Test
    public void logoutBad() throws DataAccessException {
        RegisterRequest regReq = new RegisterRequest("brighton", "pass", "email@test.com");
        RegisterResult result = userService.register(regReq);

        LogoutRequest request = new LogoutRequest("bad_auth_token");

        Assertions.assertThrows(DataAccessException.class, () -> {
            userService.logout(request);
        });
    }
}
