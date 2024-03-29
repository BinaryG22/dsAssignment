package dslab.protocol;

import dslab.mailbox.MailboxServer;
import dslab.util.Config;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class DmaProtocol {
    public String[] getMessagesById() {
        return messagesById;
    }

    private String[] messagesById;
    private String loginAttempt_errorMessage;

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
                response = list(parts);
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
        return "ok bye";
    }

    private String delete(String[] parts) {
        if (parts.length != 2){
            return PROTOCOL_ERROR;
        }else {
            if (MailboxServer.getConcurrentHashMap_messages().get(userName) != null) {
                ConcurrentHashMap<Integer, String[]> hashMap = MailboxServer.getConcurrentHashMap_messages().get(userName);
                if (isNumeric(parts[1])) {
                    if (hashMap.containsKey(Integer.valueOf(parts[1]))) {
                        hashMap.remove(Integer.valueOf(parts[1]));
                        return DEFAULT_RESPONSE;
                    } else return "could not find message with message id " + parts[1];
                } return "id must be a number";
            }return "list empty";
        }
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private String show(String[] parts) {
        if (!isLoggedIn){
            return "error you must be logged in first";
        }
        if (parts.length != 2){
            return PROTOCOL_ERROR;
        }
        if (!isNumeric(parts[1])){
            return "error id must be a number";
        }
        if (MailboxServer.getConcurrentHashMap_messages().get(userName) != null){
            ConcurrentHashMap<Integer, String[]> hashMap = MailboxServer.getConcurrentHashMap_messages().get(userName);
            if (!hashMap.containsKey(Integer.parseInt(parts[1]))){
                return "error can not find message with this id " + parts[1];
            }
            //sender, subject, data
            String[] fromHashMap = hashMap.get(Integer.parseInt(parts[1]));
            String receiver = "to " + userName + "@" + config.getString("domain");
            String sender = "from " + fromHashMap[0];
            String subject = "subject " + fromHashMap[1];
            String data = "data " + fromHashMap[2];
            messagesById = new String[]{receiver, sender, subject, data};
            return DEFAULT_RESPONSE;
        }
        return "error can not find message with this id " + parts[1];
    }

    private String list(String[] parts) {
        if (parts.length != 1){
            return PROTOCOL_ERROR;
        }
        if (isLoggedIn) {
            if (MailboxServer.getConcurrentHashMap_messages().get(userName) != null) {
                ConcurrentHashMap<Integer, String[]> hashMap = MailboxServer.getConcurrentHashMap_messages().get(userName);
                for (Integer k : hashMap.keySet()
                ) {
                    String[] message = hashMap.get(k);
                    allMessages.add(new String[]{k.toString(), message[0], message[1]});
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
                return DEFAULT_RESPONSE;
            } return loginAttempt_errorMessage;
        }else return PROTOCOL_ERROR;
    }

    private boolean checkUser(String username, String pw) {
        String user_config_location = config.getString("users.config");
        Config users_config = new Config(user_config_location);

        if (users_config.containsKey(username)) {
            String password = users_config.getString(username);
            if (password == null) {
                return false;
            }

            if (!password.equals(pw)){
                loginAttempt_errorMessage = "error wrong password";
            }
            return password.equals(pw);
        }else  {
            loginAttempt_errorMessage = "error unknown user";
            return false;
        }
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
