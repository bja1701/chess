package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS user (
          `username` VARCHAR(255) NOT NULL,
          `password` VARCHAR(255) NOT NULL,
          `email` VARCHAR(255) NOT NULL,
          PRIMARY KEY (`username`)
        )
        """,
            """
        CREATE TABLE IF NOT EXISTS auth (
          `authToken` VARCHAR(255) NOT NULL,
          `username` VARCHAR(255) NOT NULL,
          PRIMARY KEY (`authToken`)
        )
        """,
            """
        CREATE TABLE IF NOT EXISTS game (
          `gameID` INT NOT NULL AUTO_INCREMENT,
          `whiteUsername` VARCHAR(255),
          `blackUsername` VARCHAR(255),
          `gameName` VARCHAR(255) NOT NULL,
          `game` TEXT NOT NULL,
          PRIMARY KEY (`gameID`)
        )
        """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    switch (params[i]) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case null -> ps.setNull(i + 1, java.sql.Types.NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {

    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        return 0;
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
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public void createAuthToken(AuthData authData) throws DataAccessException {

    }

    @Override
    public AuthData getAuthToken(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }
}