package dslab.DMTP;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class DmtProtocol {
    private boolean connectionIsEstablished;
    private boolean messageIsStarted;
    private boolean recipientIsSet;
    private boolean senderIsSet;
    private boolean subjectIsSet;
    private boolean dataIsSet;
    private boolean isSend;
    private boolean toQuit;

    private ArrayList<String> recipients = new ArrayList<>();

    public ArrayList<String> getRecipients() {
        return recipients;
    }

    public String getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }

    public String getData() {
        return data;
    }

    private String sender = "from";
    private String subject;
    private String data;
    private String finaleMessage;

    final static String PROTOCOL_TYPE = "DMTP";
    final static String DEFAULT_RESPONSE = "ok";
    final static String PROTOCOL_ERROR = "Error protocol error";

    public DmtProtocol() {
        connectionIsEstablished = false;
        messageIsStarted = false;
        recipientIsSet = false;
        senderIsSet = false;
        subjectIsSet = false;
        dataIsSet = false;
        isSend = false;
        toQuit = false;
    }


    public String validateRequest(String request) {
        String response = null;
        String[] parts = request.split("\\s");

        switch (parts[0]) {
            case "begin":
                response = startMessage(parts);
                break;
            case "to":
                response = setRecipients(parts);
                break;
            case "from":
                response = setSender(parts);
                break;
            case "subject":
                response = setSubject(parts);
                break;
            case "data":
                response = setMessage(parts);
                break;
            case "send":
                response = sendMessage();
                break;
            case "quit":
                response = quit();
                break;
            default:
                response = PROTOCOL_ERROR;
        }
        return response;
    }

    private String sendMessage() {
        if (!senderIsSet) return "error no Sender";
        if (!recipientIsSet) return "error no recipients";
        if (!subjectIsSet) return "error no subject";

        isSend = true;
        finaleMessage = this.toString();

        return finaleMessage;
    }

    private String quit() {
        return "call quit() in dmtp";
    }

    private String setRecipients(String[] parts) {
        recipients.clear();
        if (parts.length == 1){
            return "recipients not set";
        }else{
            recipientIsSet = true;
            String[] addresses = parts[1].split(",");
            recipients.addAll(Arrays.asList(addresses));
            return DEFAULT_RESPONSE + " " + recipients.size();
        }
    }

    private String setMessage(String[] parts) {
        if (parts.length == 1){
            dataIsSet = true;
            data = "";
        }else {
            dataIsSet = true;
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i<parts.length; i++) {
                if (i < parts.length-1) {
                    sb.append(parts[i]).append(" ");
                }else sb.append(parts[i]);
            }
            data = sb.toString();
            return DEFAULT_RESPONSE;
        }
        return "message not set";
    }

    private String setSubject(String[] parts) {
        if (parts.length == 1){
            subjectIsSet = true;
            subject = "";
        }else {
            subjectIsSet = true;
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i<parts.length; i++) {
                if (i < parts.length-1) {
                    sb.append(parts[i]).append(" ");
                }else sb.append(parts[i]);
            }
            subject = sb.toString();
            return DEFAULT_RESPONSE;
        }
        return "subject not set";
    }

    private String setSender(String[] parts) {
        if (parts.length != 2){
            return "use exactly one sender";
        }else {
            senderIsSet = true;
            sender = parts[1];
            return DEFAULT_RESPONSE;
        }
    }

    private String startMessage(String[] parts) {
        if (!messageIsStarted) {
            if (parts.length == 1) {
                messageIsStarted = true;
                return DEFAULT_RESPONSE;
            } else return "'begin <?>' command too long, try only 'begin'";
        } else return "message already started";
    }

    public String checkConnection(Socket client){
        if (client.isConnected()){
            connectionIsEstablished = true;
            return DEFAULT_RESPONSE + " " + PROTOCOL_TYPE;
        }
        else return "Error DMTP connection error";
    }

    @Override
    public String toString() {
        return "DmtProtocol{" +
                "connectionIsEstablished=" + connectionIsEstablished +
                ", messageIsStarted=" + messageIsStarted +
                ", recipientIsSet=" + recipientIsSet +
                ", senderIsSet=" + senderIsSet +
                ", subjectIsSet=" + subjectIsSet +
                ", dataIsSet=" + dataIsSet +
                ", isSend=" + isSend +
                ", toQuit=" + toQuit +
                ", recipients=" + recipients +
                ", sender='" + sender + '\'' +
                ", subject='" + subject + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
