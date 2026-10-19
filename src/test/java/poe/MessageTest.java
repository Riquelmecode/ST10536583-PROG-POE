package poe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageTest {

    // Test data from the brief
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String MESSAGE_1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String RECIPIENT_2 = "08575975889";
    private static final String MESSAGE_2 = "Hi Keegan, did you receive the payment?";

    @TempDir
    Path tempDir;

    private Path storageFile;

    @BeforeEach
    public void setUp() {
        Message.resetSession();
        storageFile = tempDir.resolve("stored_messages.json");
        Message.setStorageFile(storageFile);
    }

    @AfterEach
    public void tearDown() {
        Message.resetSession();
        Message.setStorageFile(Paths.get(Message.DEFAULT_STORAGE_FILE));
    }

    @Test
    public void testMessageLengthSuccess() {
        assertEquals("Message ready to send.", Message.checkMessageLength(MESSAGE_1));
    }

    @Test
    public void testMessageLengthExactlyTwoHundredFiftyIsAllowed() {
        assertEquals("Message ready to send.", Message.checkMessageLength("a".repeat(250)));
    }

    @Test
    public void testMessageLengthFailure() {
        String tooLong = "a".repeat(260);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                Message.checkMessageLength(tooLong));
    }

    @Test
    public void testRecipientNumberCorrectlyFormatted() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Cell phone number successfully captured.", message.checkRecipientCell());
    }

    @Test
    public void testRecipientNumberIncorrectlyFormatted() {
        Message message = new Message(1, RECIPIENT_2, MESSAGE_2);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.", message.checkRecipientCell());
    }

    @Test
    public void testMessageHashIsCorrect() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    public void testMessageHashesInLoop() {
        String[] ids = {"0012345678", "4598765432"};
        String[] recipients = {RECIPIENT_1, RECIPIENT_2};
        String[] texts = {MESSAGE_1, MESSAGE_2};
        String[] expectedHashes = {"00:0:HITONIGHT", "45:1:HIPAYMENT"};

        for (int i = 0; i < texts.length; i++) {
            Message message = new Message(ids[i], i, recipients[i], texts[i]);
            assertEquals(expectedHashes[i], message.createMessageHash());
        }
    }

    @Test
    public void testMessageIDIsCreated() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        String id = message.getMessageID();

        assertTrue(message.checkMessageID());
        assertTrue(id.matches("\\d{10}"));
        assertEquals("Message ID generated: " + id, message.messageIDCreated());
    }

    @Test
    public void testMessageIDTooLong() {
        Message message = new Message("12345678901", 0, RECIPIENT_1, MESSAGE_1);
        assertFalse(message.checkMessageID());
    }

    @Test
    public void testMessageSentSend() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Message successfully sent.", message.sentMessage(Message.OPTION_SEND));
    }

    @Test
    public void testMessageSentDisregard() {
        Message message = new Message(1, RECIPIENT_2, MESSAGE_2);
        assertEquals("Press 0 to delete the message.", message.sentMessage(Message.OPTION_DISREGARD));
    }

    @Test
    public void testMessageSentStore() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Message successfully stored.", message.sentMessage(Message.OPTION_STORE));
    }

    @Test
    public void testStoreMessageWritesJsonFile() throws IOException {
        Message first = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        Message second = new Message("4598765432", 1, RECIPIENT_2, MESSAGE_2);

        assertTrue(first.storeMessage());
        assertTrue(second.storeMessage());

        JsonArray stored = JsonParser.parseString(Files.readString(storageFile)).getAsJsonArray();
        assertEquals(2, stored.size());

        JsonObject saved = stored.get(0).getAsJsonObject();
        assertEquals("0012345678", saved.get("messageID").getAsString());
        assertEquals("00:0:HITONIGHT", saved.get("messageHash").getAsString());
        assertEquals(RECIPIENT_1, saved.get("recipient").getAsString());
        assertEquals(MESSAGE_1, saved.get("message").getAsString());
    }

    @Test
    public void testReturnTotalMessagesWithTestData() {
        // Message 1: Select Send. Message 2: Select Discard.
        new Message(0, RECIPIENT_1, MESSAGE_1).sentMessage(Message.OPTION_SEND);
        new Message(1, RECIPIENT_2, MESSAGE_2).sentMessage(Message.OPTION_DISREGARD);

        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    public void testStoredMessageIsNotCountedAsSent() {
        new Message(0, RECIPIENT_1, MESSAGE_1).sentMessage(Message.OPTION_STORE);
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    public void testPrintMessagesShowsDetailsInOrder() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        message.sentMessage(Message.OPTION_SEND);

        assertEquals("Message ID: 0012345678"
                + "\nMessage Hash: 00:0:HITONIGHT"
                + "\nRecipient: " + RECIPIENT_1
                + "\nMessage: " + MESSAGE_1, Message.printMessages());
    }

    @Test
    public void testPrintMessagesWhenNoneSent() {
        assertEquals("No messages have been sent.", Message.printMessages());
    }
}
