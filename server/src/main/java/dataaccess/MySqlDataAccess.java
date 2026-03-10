package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
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
            throw new DataAccessException(String.format(
                    "Unable to configure database: %s", ex.getMessage()));
        }
    }

    private void executeUpdate(
            String statement, Object... params) throws DataAccessException {
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
            throw new DataAccessException(String.format(
                    "unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    @Override
    public void clear() throws DataAccessException {
        executeUpdate("TRUNCATE user");
        executeUpdate("TRUNCATE auth");
        executeUpdate("TRUNCATE game");
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM user WHERE username = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    String foundUsername = rs.getString("username");
                    String foundPassword = rs.getString("password");
                    String foundEmail = rs.getString("email");
                    return new UserData(foundUsername, foundPassword, foundEmail);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, userData.username(), hashedPassword, userData.email());
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        ChessGame newGame = new ChessGame();
        String gameJson = new Gson().toJson(newGame);
        String sql = "INSERT INTO game (gameName, game) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, gameName);
            ps.setString(2, gameJson);
            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    int foundId = rs.getInt("gameID");
                    String foundWhite = rs.getString("whiteUsername");
                    String foundBlack = rs.getString("blackUsername");
                    String foundName = rs.getString("gameName");
                    String gameJson = rs.getString("game");
                    ChessGame foundGame = new Gson().fromJson(gameJson, ChessGame.class);
                    return new GameData(foundId, foundWhite, foundBlack, foundName, foundGame);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
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
        String sql = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeUpdate(sql, authData.authToken(), authData.username());
    }

    @Override
    public AuthData getAuthToken(String authToken) throws DataAccessException {
        String sql = "SELECT authToken, username FROM auth WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, authToken);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    String foundToken = rs.getString("authToken");
                    String foundUser = rs.getString("username");
                    return new AuthData(foundToken, foundUser);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth WHERE authToken = ?";
        executeUpdate(sql, authToken);
    }
}