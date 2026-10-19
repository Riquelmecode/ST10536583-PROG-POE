package poe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A single QuickChat message, plus the session-wide record of messages
 * sent while the program is running.
 * JSON storage uses Google's Gson library:
 * https://github.com/google/gson/blob/main/UserGuide.md
 */
public class Message {

    public static final int MAX_MESSAGE_LENGTH = 250;
    public static final int MESSAGE_ID_LENGTH = 10;
    public static final String DEFAULT_STORAGE_FILE = "stored_messages.json";

    public static final int OPTION_SEND = 1;
    public static final int OPTION_DISREGARD = 2;
    public static final int OPTION_STORE = 3;

    private static final Random RANDOM = new Random();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Session-wide state: every message sent while the program is running.
    private static final List<Message> sentMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;
    private static Path storageFile = Paths.get(DEFAULT_STORAGE_FILE);

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;

    /**
     * Creates a message with a randomly generated ten-digit ID.
     */
    public Message(int messageNumber, String recipient, String messageText) {
        this(generateMessageID(), messageNumber, recipient, messageText);
    }

    /**
     * Creates a message with a known ID (used when loading or testing).
     */
    public Message(String messageID, int messageNumber, String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    public static String generateMessageID() {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < MESSAGE_ID_LENGTH; i++) {
            id.append(RANDOM.nextInt(10));
        }
        return id.toString();
    }

    public boolean checkMessageID() {
        return messageID != null && !messageID.isEmpty() && messageID.length() <= MESSAGE_ID_LENGTH;
    }

    public String messageIDCreated() {
        return "Message ID generated: " + messageID;
    }

    /**
     * Reuses the Part 1 cell phone validation (+27 followed by 9-10 digits).
     */
    public String checkRecipientCell() {
        if (new Login().checkCellPhoneNumber(recipient)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.";
    }

    public static String checkMessageLength(String messageText) {
        int length = messageText == null ? 0 : messageText.length();
        if (length <= MAX_MESSAGE_LENGTH) {
            return "Message ready to send.";
        }
        return "Message exceeds " + MAX_MESSAGE_LENGTH + " characters by " + (length - MAX_MESSAGE_LENGTH)
                + "; please reduce the size.";
    }

    /**
     * First two digits of the ID, the message number and the first and last
     * words of the message (punctuation removed), all in capitals.
     * Example: 00:0:HITONIGHT
     */
    public String createMessageHash() {
        String idPart = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        String[] words = messageText.trim().split("\\s+");
        String firstWord = stripPunctuation(words[0]);
        String lastWord = stripPunctuation(words[words.length - 1]);
        return (idPart + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    private static String stripPunctuation(String word) {
        return word.replaceAll("[^a-zA-Z0-9]", "");
    }

    /**
     * Applies the user's choice: 1 send, 2 disregard, 3 store for later.
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case OPTION_SEND:
                sentMessages.add(this);
                totalMessagesSent++;
                return "Message successfully sent.";
            case OPTION_DISREGARD:
                return "Press 0 to delete the message.";
            case OPTION_STORE:
                if (storeMessage()) {
                    return "Message successfully stored.";
                }
                return "Message could not be stored.";
            default:
                return "Invalid option. Please choose 1, 2 or 3.";
        }
    }

    /**
     * Appends this message to the JSON storage file, creating it if needed.
     */
    public boolean storeMessage() {
        try {
            JsonArray stored = new JsonArray();
            if (Files.exists(storageFile)) {
                String existing = Files.readString(storageFile, StandardCharsets.UTF_8);
                if (!existing.isBlank()) {
                    stored = JsonParser.parseString(existing).getAsJsonArray();
                }
            }
            stored.add(toJson());
            Files.writeString(storageFile, GSON.toJson(stored), StandardCharsets.UTF_8);
            return true;
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("messageID", messageID);
        json.addProperty("messageNumber", messageNumber);
        json.addProperty("messageHash", messageHash);
        json.addProperty("recipient", recipient);
        json.addProperty("message", messageText);
        return json;
    }

    public String getDetails() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + messageText;
    }

    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent.";
        }
        StringBuilder output = new StringBuilder();
        for (Message message : sentMessages) {
            if (output.length() > 0) {
                output.append("\n\n");
            }
            output.append(message.getDetails());
        }
        return output.toString();
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    public static void setStorageFile(Path file) {
        storageFile = file;
    }

    /**
     * Clears the session's sent messages (used between unit tests).
     */
    public static void resetSession() {
        sentMessages.clear();
        totalMessagesSent = 0;
    }

    public String getMessageID() {
        return messageID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageHash() {
        return messageHash;
    }
}
