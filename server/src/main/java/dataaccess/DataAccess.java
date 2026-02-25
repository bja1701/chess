package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {

    void clear() throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData userData) throws DataAccessException;

    int createGame(String gameName)throws DataAccessException;

    GameData getGame(int id) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;

    void createAuthToken(AuthData authData) throws DataAccessException;

    AuthData getAuthToken(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;
}


