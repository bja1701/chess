package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest)
            throws DataAccessException {
        boolean username = registerRequest.username() == null
                || registerRequest.username().isEmpty();
        boolean password = registerRequest.password() == null
                || registerRequest.password().isEmpty();
        boolean email = registerRequest.email() == null
                || registerRequest.email().isEmpty();
        if (username || password || email){
            throw new DataAccessException("Error: bad request");
        }
        if(dataAccess.getUser(registerRequest.username()) != null){
            throw new DataAccessException("Error: already taken");
        }
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        dataAccess.createUser(user);
        String authToken = java.util.UUID.randomUUID().toString();
        model.AuthData authData = new model.AuthData(authToken, registerRequest.username());
        dataAccess.createAuthToken(authData);

        return new RegisterResult(registerRequest.username(), authToken);
    }
    public LoginResult login(LoginRequest loginRequest)
            throws DataAccessException {
        boolean username = loginRequest.username() == null
                || loginRequest.username().isEmpty();
        boolean password = loginRequest.password() == null
                || loginRequest.password().isEmpty();
        if (username || password){
            throw new DataAccessException("Error: bad request");
        }
        UserData user = dataAccess.getUser(loginRequest.username());
        if(user == null){
            throw new DataAccessException("Error: unauthorized");
        }
        if (!BCrypt.checkpw(loginRequest.password(), user.password())) {
            throw new DataAccessException("Error: unauthorized");
        }
        String authToken = java.util.UUID.randomUUID().toString();
        model.AuthData authData = new model.AuthData(authToken, loginRequest.username());
        dataAccess.createAuthToken(authData);

        return new LoginResult(loginRequest.username(), authToken);
    }


    public void logout(LogoutRequest logoutRequest)
            throws DataAccessException {
        AuthData token = dataAccess.getAuthToken(logoutRequest.authToken());
        if (token == null){
            throw new DataAccessException("Error: unauthorized");
        }
        dataAccess.deleteAuth(logoutRequest.authToken());
    }
}
