package dslab.DMAP;

import dslab.util.Config;

import java.net.Socket;

public class DmaProtocol {
    private boolean isLoggedIn;
    final static String PROTOCOL_TYPE = "DMAP";
    final static String DEFAULT_RESPONSE = "ok";
    final static String PROTOCOL_ERROR = "Error protocol error";
    private Config config;
    private boolean connectionIsEstablished;
    private String userName = null;

    public DmaProtocol(Config config) {
        isLoggedIn = false;
        connectionIsEstablished = false;
        this.config = config;
    }

    public String validateRequest(String request) {
        String response = null;
        String[] parts = request.split("\\s");

        switch (parts[0]) {
            case "login":
                response = login(parts);
                break;
            case "list":
                response = list();
                break;
            case "show":
                response = show(parts);
                break;
            case "delete":
                response = delete(parts);
                break;
            case "logout":
                response = logout();
                break;
            case "quit":
                response = quit();
                break;
            default:
                response = PROTOCOL_ERROR;
        }
        return response;
    }

    private String logout() {
        if(isLoggedIn){
            userName = null;
            isLoggedIn = false;
            return DEFAULT_RESPONSE;
        }else{
            return "you are not logged in";
        }
    }

    private String quit() {
        return null;
    }

    private String delete(String[] parts) {
        return null;

    }

    private String show(String[] parts) {
        return null;

    }

    private String list() {
        return null;

    }

    private String login(String[] parts) {
        if (parts.length == 3) {
            if (isLoggedIn) {
                return "You are already logged in!";
            }

            if (checkUser(parts[1], parts[2])) {
                userName = parts[1];
                isLoggedIn = true;
                return "Successfully logged in!";
                //Todo: why if checkuser return false im not landing in the return statement below
            } return "Wrong username/password combination!";
        }else return PROTOCOL_ERROR;
    }

    private boolean checkUser(String username, String pw) {
        System.out.println("username[1]: " + username);
        System.out.println("password[2]: " + pw);

        String user_config_location = config.getString("users.config");
        System.out.println(user_config_location);
        Config users_config = new Config(user_config_location);
        System.out.println(users_config.listKeys());

        if (users_config.containsKey(username)) {
            String password = users_config.getString(username);
            System.out.println("received password: " + password);
            if (password == null) {
                return false;
            }

            System.out.println(password.equals(pw));
            return password.equals(pw);
        }else  return false;
    }

    public String checkConnection(Socket client) {
        if (client.isConnected()) {
            connectionIsEstablished = true;
            return DEFAULT_RESPONSE + " " + PROTOCOL_TYPE;
        } else return "Error DMAP connection error";
    }
}
