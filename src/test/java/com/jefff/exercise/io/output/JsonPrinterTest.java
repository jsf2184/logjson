package com.jefff.exercise.io.output;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jefff.exercise.LogEntry;
import com.jefff.exercise.LogEntryWindow;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.jefff.exercise.parse.LogParserTest.TIMESTAMP_SECONDS;

public class JsonPrinterTest {
    ByteArrayOutputStream outputStream;

    JsonPrinter create() {
        outputStream = new ByteArrayOutputStream();
        return new JsonPrinter(outputStream);
    }

    @Test
    public void testWindowWritingScenario() throws IOException {
        final JsonPrinter jsonPrinter = create();
        jsonPrinter.start();

        LogEntryWindow window1 = new LogEntryWindow();
        Assert.assertNotNull(TIMESTAMP_SECONDS);
        window1.add(new LogEntry(TIMESTAMP_SECONDS, "MyClass", "message1"), 1);
        window1.add(new LogEntry(TIMESTAMP_SECONDS + 1, "MyClass", "message2"), 2);
        window1.add(new LogEntry(TIMESTAMP_SECONDS + 2, "MyClass", "message3"), 3);
        jsonPrinter.writeWindow(window1);

        LogEntryWindow window2 = new LogEntryWindow();
        window2.add(new LogEntry(TIMESTAMP_SECONDS + 3, "MyClass", "message4"), 4);
        window2.add(new LogEntry(TIMESTAMP_SECONDS + 4, "MyClass", "message5"), 5);
        window2.add(new LogEntry(TIMESTAMP_SECONDS + 5, "MyClass", "message6"), 6);
        jsonPrinter.writeWindow(window2);

        jsonPrinter.close();
        String content = getStreamString();

        // Verify the errorCountPlaceHolder is where it is supposed to be.
        final int errorCountOffset = jsonPrinter.getErrorCountOffset();
        final int placeHolderLength = JsonPrinter.ERROR_COUNT_PLACEHOLDER.length();
        final String errorCountPlaceholder = content.substring(errorCountOffset, errorCountOffset + placeHolderLength);
        Assert.assertEquals(JsonPrinter.ERROR_COUNT_PLACEHOLDER, errorCountPlaceholder);

        // Verify 2 windows
        Assert.assertEquals(2, jsonPrinter.getErrorCount());
        final Gson gson = jsonPrinter.getGson();
        // verify that the content string is parsable.
        final JsonObject jsonObject = gson.fromJson(content, JsonObject.class);
        Assert.assertNotNull(jsonObject);

    }

    String getStreamString() {
        final byte[] bytes = outputStream.toByteArray();
        String result = new String(bytes);
        return result;
    }
}