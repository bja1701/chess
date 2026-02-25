package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import dataaccess.DataAccessException;

import java.util.Collection;

public interface DataAccess {

    void clear() throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData data) throws DataAccessException;

    int createGame(String gameName)throws DataAccessException;

    GameData getGame(int id) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(GameData data) throws DataAccessException;

    void createAuthToken(AuthData data) throws DataAccessException;

    AuthData getAuthToken(String token) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;
}


