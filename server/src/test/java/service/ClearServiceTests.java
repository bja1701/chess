package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.RegisterRequest;
import model.RegisterResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClearServiceTests {
    private UserService userService;
    private ClearService clearService;

    @BeforeEach
    public void setup() throws DataAccessException {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        clearService = new ClearService(dataAccess);
        clearService.clear();
    }

    @Test
    public void clearGood() throws DataAccessException {
        RegisterRequest regReq = new RegisterRequest("brighton", "pass", "email@test.com");
        userService.register(regReq);

        clearService.clear();

        RegisterResult result = userService.register(regReq);
        Assertions.assertNotNull(result.authToken());
    }

}
