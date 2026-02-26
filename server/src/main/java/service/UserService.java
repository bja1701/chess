package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;

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
            throw new DataAccessException("Error: Bad Request");
        }
        if(dataAccess.getUser(registerRequest.username()) != null){
            throw new DataAccessException("Error: Username Already Taken");
        }
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        dataAccess.createUser(user);
        String authToken = java.util.UUID.randomUUID().toString();
        model.AuthData authData = new model.AuthData(authToken, registerRequest.username());
        dataAccess.createAuthToken(authData);

        return new RegisterResult(registerRequest.username(), authToken);
    }
//    public LoginResult login(LoginRequest loginRequest) {}
//    public void logout(LogoutRequest logoutRequest) {}
}
