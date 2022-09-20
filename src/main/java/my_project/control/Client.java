package my_project.control;

import my_project.view.GUI;

public class Client extends KAGO_framework.model.abitur.netz.Client {

    private static final char[] keyword = "SKATIN".toCharArray();
    private String name;
    private GUI gui;

    public Client() {
        super("localhost", 25565);
        System.out.println(isConnected());
    }

    @Override
    public void processMessage(String pMessage) {
        if(gui!=null) {
            String[] args=pMessage.split("_");
            switch(args[0]){
                case "MESSAGE" -> {
                    if (args.length > 2)
                        gui.showText( args[1] + ": " + decode(args[2]));
                }
                case "DM" -> {
                    if(args.length > 3) {
                        if(args[1].equals("RECEIVED")) {
                            gui.showText(args[2] + " --> You: " + decode(args[3]));
                        } else if(args[1].equals("SENT")) {
                            gui.showText("You --> " + args[2] + ": " + decode(args[3]));
                        }
                    }
                }
                case "CHANGED-NAME" -> gui.showText("System: " + args[1] + " changed their name to " + args[2]);
                case "ERR" -> {
                    switch (args[1]) {
                        case "ALREADY-JOINED" -> gui.showText("System: You are already in the room");
                        case "NOT-CONNECTED", "NOT-IN-ROOM" -> gui.showText("System: You are not in the room");
                        case "USER-NOT-FOUND" -> gui.showText("System: User not found");
                        case "DUPLICATE-NAME" -> gui.showText("System: Duplicate name");
                        case "INVALID-INPUT" -> gui.showText("System: Invalid command");
                    }
                }
                case "NAME-SET" -> gui.showText("System: Name set to " + name);
                case "JOINED" -> gui.showText("System: " + args[1] + " joined the chat");
                case "LEFT" -> gui.showText("System: " + args[1] + " left");
            }
        }else System.err.println("GUI ist null");
    }

    public void setName(String name) {
        this.name = name;
    }

    public void sendMessage(String message){
        String[] m = message.split(" ");
        switch(m[0]) {
            case "/setname" -> {
                if (m[1].matches("^[a-zA-Z]+$") && m.length == 2) {
                    send("SETNAME_" + m[1]);
                    setName(m[1]);
                } else
                    System.err.println("INVALID_NAME");
            }
            case "/join" -> send("JOIN");
            case "/leave" -> send("LEAVE");
            case "/whisper", "/dm", "/msg", "/message" -> {
                if(m.length>2) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 2; i < m.length; i++)
                        sb.append(m[i]).append(" ");
                    send("WHISPER_" + m[1] + "_" + encode(sb.toString()));
                }
            }
            default -> send("MESSAGE_" + encode(message));
        }
    }

    public void setGui(GUI gui){
        this.gui=gui;
    }

    private String encode(String message) {
        if(message != null && !message.equals("")) {
            StringBuilder result = new StringBuilder();
            var charMessage = message.toCharArray();

            int j = 0;
            for (char c : charMessage) {
                if (j == keyword.length - 1)
                    j = 0;
                result.append((char) ((int) c + (int) keyword[j]));
                j++;
            }
            return result.toString();
        }
        return null;
    }

    private String decode(String message) {
        if(message != null && !message.equals("")) {
            StringBuilder result = new StringBuilder();
            var charMessage = message.toCharArray();

            int j = 0;
            for (char c : charMessage) {
                if (j == keyword.length - 1)
                    j = 0;
                result.append((char) ((int) c - (int) keyword[j]));
                j++;
            }
            return result.toString();
        }
        return null;
    }
}
