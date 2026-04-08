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

    public void joinGame(JoinGameRequest joinRequest) throws DataAccessException {
        AuthData gameToken = dataAccess.getAuthToken(joinRequest.authToken());
        if (gameToken == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        String username = gameToken.username();
        GameData game = dataAccess.getGame(joinRequest.gameID());
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }
        String teamColor = joinRequest.playerColor();
        if ("WHITE".equals(teamColor)) {
            if (game.whiteUsername() != null && !game.whiteUsername().equals(username)) {
                throw new DataAccessException("Error: already taken");
            }
            GameData updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
            dataAccess.updateGame(updatedGame);
        } else if ("BLACK".equals(teamColor)) {
            if (game.blackUsername() != null && !game.blackUsername().equals(username)) {
                throw new DataAccessException("Error: already taken");
            }
            GameData updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
            dataAccess.updateGame(updatedGame);
        } else {
            throw new DataAccessException("Error: bad request");
        }
    }

}
