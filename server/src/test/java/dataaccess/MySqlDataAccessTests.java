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
        Assertions.assertThrows(DataAccessException.class, () -> {
            dataAccess.createUser(user);
        });
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
        Assertions.assertThrows(DataAccessException.class, () -> {
            dataAccess.createAuthToken(auth);
        });
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
    public void deleteAuthNegative() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> {
            dataAccess.deleteAuth("fakeTokenThatDoesNotExist");
        });
    }

}
