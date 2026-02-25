package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryDataAccess implements DataAccess{

    HashMap<String, UserData> users = new HashMap<>();
    HashMap<Integer, GameData> games = new HashMap<>();
    HashMap<String, AuthData> authTokens = new HashMap<>();
    int nextID = 1;

    @Override
    public int createGame(String gameName) throws DataAccessException {
        int gameID = nextID;
        nextID = nextID +1;
        GameData game = new GameData(gameID, null, null, gameName, new ChessGame());
        games.put(gameID, game);
        return gameID;
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        return games.get(id);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public void createAuthToken(AuthData authData) throws DataAccessException {
        authTokens.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuthToken(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authTokens.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        games.clear();
        authTokens.clear();
        nextID= 1;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        users.put(userData.username(), userData);
    }
}
