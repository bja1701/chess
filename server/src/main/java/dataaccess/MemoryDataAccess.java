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
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData data) throws DataAccessException {

    }

    @Override
    public void createAuthToken(AuthData data) throws DataAccessException {

    }

    @Override
    public AuthData getAuthToken(String token) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void createUser(UserData data) throws DataAccessException {
        users.put(data.username(), data);
    }
}
