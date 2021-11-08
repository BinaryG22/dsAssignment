package dslab.protocol;

import dslab.mailbox.MailboxServer;
import dslab.util.Config;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class DmaProtocol {
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    private boolean isLoggedIn;
    final static String PROTOCOL_TYPE = "DMAP";
    final static String DEFAULT_RESPONSE = "ok";
    final static String PROTOCOL_ERROR = "Error protocol error";
    private Config config;
    private boolean connectionIsEstablished;
    private String userName = null;
    private ArrayList<String[]> allMessages;

    //k = message ID; v = user?
    private int messageID;
    ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();

    public DmaProtocol(Config config) {
        isLoggedIn = false;
        connectionIsEstablished = false;
        this.config = config;
        allMessages = new ArrayList<>();
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
        if (isLoggedIn) {
            if (MailboxServer.getConcurrentHashMap_messages().get(userName) != null) {
                ConcurrentHashMap<Integer, String[]> hashMap = MailboxServer.getConcurrentHashMap_messages().get(userName);
                for (Integer k : hashMap.keySet()
                ) {
                    String[] message = hashMap.get(k);
                    allMessages.add(new String[]{k.toString(), message[0], message[1]});
                }
                System.out.println(allMessages.size());
                for (String[] messages : allMessages
                ) {
                    System.out.println(Arrays.toString(messages));
                }
                return "ok";
            }else return "currently no entries";
        }else return "you must be logged in first";
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

    public ArrayList<String[]> getAllMessages() {
        return allMessages;
    }

    public void clearMessages() {
        allMessages.clear();
    }
}
