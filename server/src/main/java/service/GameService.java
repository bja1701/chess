package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;

import java.util.Collection;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public CreateGameResult createGame (CreateGameRequest gameRequest)
            throws DataAccessException {
        AuthData gameToken = dataAccess.getAuthToken(gameRequest.authToken());
        if (gameToken == null){
            throw new DataAccessException("Error: unauthorized");
        }
        if (gameRequest.gameName() == null || gameRequest.gameName().isEmpty()){
            throw new DataAccessException("Error: bad request");
        }
        int gameID = dataAccess.createGame(gameRequest.gameName());
        return new CreateGameResult(gameID);
    }

    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException{
        AuthData gameToken = dataAccess.getAuthToken(request.authToken());
        if (gameToken == null){
            throw new DataAccessException("Error: unauthorized");
        }
        Collection<GameData> gameList = dataAccess.listGames();
        return new ListGamesResult(gameList);
    }

}
