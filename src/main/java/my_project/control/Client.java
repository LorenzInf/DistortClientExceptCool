package my_project.control;

import my_project.view.GUI;

public class Client extends KAGO_framework.model.abitur.netz.Client {

    private static final char[] keyword = "SKATIN".toCharArray(); //Has to be in capslock
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
                        gui.showText( args[1] + ": " + code(args[2],false));
                }
                case "DM" -> {
                    if(args.length > 3) {
                        if(args[1].equals("RECEIVED")) {
                            gui.showText(args[2] + " --> You: " + code(args[3],false));
                        } else if(args[1].equals("SENT")) {
                            gui.showText("You --> " + args[2] + ": " + code(args[3],false));
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
                    send("WHISPER_" + m[1] + "_" + code(sb.toString(),true));
                }
            }
            default -> send("MESSAGE_" + code(message,true));
        }
    }

    public void setGui(GUI gui){
        this.gui=gui;
    }

    //Normal
    private String code(String message, boolean encode) {
        if(message != null && !message.equals("")) { //Nur encoden, wenn die Nachricht ungleich null oder nicht leer ist

            StringBuilder result = new StringBuilder(); //StringBuilder für das Ergebnis machen
            var charMessage = message.toCharArray(); //Die Nachricht zu einem char Array machen

            int j = 0;
            for (char c : charMessage) { //Für alle chars in der Nachricht
                if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) { //Wenn der inspizierte char zwischen a - z / A - Z ist
                    if (j < keyword.length) //Jedes Mal, wenn wir am Ende des Schlüsselworts angekommen sind, wieder von vorne Anfangen
                        j = 0;

                    boolean isUppercase = Character.isUpperCase(c);
                    // 65 abziehen, wenn er upperCase ist, 97 abziehen, wenn er lowercase ist. So ist das eben in ASCII.
                    // Danach dann das Schlüsselwort -65 auf den Wert des Buchstaben addieren/subtrahieren, weil das immer uppercase ist
                    int toAppend;
                    if(encode)
                        toAppend = ((int) c - (isUppercase ? 65 : 97)) + ((int) keyword[j] - 65);
                    else
                        toAppend = ((int) c - (isUppercase ? 65 : 97)) - ((int) keyword[j] - 65);

                    if(encode) {
                        if (toAppend > 25) // Wenn das Ergebnis größer als 25 ist (und wir encoden)
                            toAppend -= 25; // Loopen wir wieder rum
                    } else {
                        if (toAppend < 0) // Wenn das Ergebnis kleiner als 25 ist (und wir decoden)
                            toAppend += 25; // Loopen wir wieder rum
                    }

                    toAppend += isUppercase ? 65 : 97; // Dann wieder den richtigen Wert addieren

                    result.append((char) toAppend); // Und appenden
                    j++;
                } else {
                    result.append(c); // Wenn es kein Buchstabe im Alphabet war einfach appenden
                }
            }
            return result.toString();
        }
        return null;
    }

    // Die beiden Methoden sind sehr ähnlich, also gibt es bestimmt einen kürzeren Weg, den haben wir jetzt aber nicht

    /* ASCII
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
    */
}
