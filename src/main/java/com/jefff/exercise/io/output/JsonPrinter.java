package com.jefff.exercise.io.output;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.jefff.exercise.LogEntry;
import com.jefff.exercise.LogEntryWindow;
import com.jefff.exercise.utility.TimeMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class JsonPrinter {

    public static final String NEWLINE = "\n";
    public static final String QUOTE = "" + '"';
    public static final String QUOTE_COLON = QUOTE + ": ";
    public static final String ERROR_COUNT_PLACEHOLDER = "999999999999,";
    public static final String ERRORCOUNT_ATTRIBUTE = QUOTE + "errorCount" + QUOTE_COLON + ERROR_COUNT_PLACEHOLDER;
    private static final int INDENTATION = 2;

    private final OutputStream outputStream;
    private final Map<Integer, String> paddingCache;
    private final String fileHeader;
    private final int errorCountOffset;
    private int errorCount;
    private final Gson gson;

    public JsonPrinter(OutputStream outputStream) {
        this.outputStream = outputStream;
        paddingCache = new HashMap<>();
        fileHeader = createFileHeader();
        errorCount = 0;
        errorCountOffset = fileHeader.indexOf(ERROR_COUNT_PLACEHOLDER);
        gson = createGson();
    }

    /**
     * getErrorCountOffset()
     * <p>
     * this method will help us "edit" the output file to update the errorCount. It gives us the offset of the
     * errorCount 'placeHolder' value.
     *
     * @return integer offset in bytes
     */
    public int getErrorCountOffset() {
        return errorCountOffset;
    }

    public void start() throws IOException {
        writeString(fileHeader);
        flush();
    }

    public Gson getGson() {
        return gson;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void close() throws IOException {
        if (errorCount > 0) {
            // write a new line to start a new line after the last window we wrote
            writeString(NEWLINE);
        }
        // close off the errors array
        writePaddedLine(1, "]", true);
        // close off the top level nesting
        writePaddedLine(0, "}", true);
        outputStream.close();
    }

    public void writeWindow(LogEntryWindow window) throws IOException {
        final String jsonWindow = gson.toJson(window);
        final String[] lines = jsonWindow.split(NEWLINE);
        if (errorCount > 0) {
            writeString("," + NEWLINE);
        }
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            // write the line with a trailing new-line (unless it is the last line)
            writePaddedLine(2, line, i < lines.length - 1);
        }
        flush();
        errorCount++;
    }

    private Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(LogEntry.class, logEntryJsonSerializer);
        return gsonBuilder.create();
    }

    private JsonSerializer<LogEntry> logEntryJsonSerializer = (logEntry, type, jsonSerializationContext) -> {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("timestamp", TimeMapper.toDateString(logEntry.getTimestamp()));
        jsonObject.addProperty("className", logEntry.getClassName());
        jsonObject.addProperty("message", logEntry.getMessage());
        return jsonObject;
    };

    private String createFileHeader() {
        String result = '{' + NEWLINE +
                getPad(1) + ERRORCOUNT_ATTRIBUTE + NEWLINE +
                getPad(1) + QUOTE + "errors" + QUOTE_COLON + "[" + NEWLINE;
        return result;
    }

    private void flush() throws IOException {
        outputStream.flush();
    }

    private void writePaddedLine(int level, String text, boolean newLine) throws IOException {
        String line = getPad(level) + text + (newLine ? NEWLINE : "");
        writeString(line);
    }

    private void writeString(String str) throws IOException {
        outputStream.write(str.getBytes());
    }

    private String getPad(int level) {
        return paddingCache.computeIfAbsent(level, l -> StringUtils.repeat(' ', level * INDENTATION));
    }

}
