package com.jefff.exercise;

import com.jefff.exercise.io.input.LineReader;
import com.jefff.exercise.io.output.JsonPrinterFactory;
import com.jefff.exercise.io.output.OutputManager;
import com.jefff.exercise.parse.LogParser;
import com.jefff.exercise.parse.errors.ErrorDetector;
import com.jefff.exercise.utility.ArgParser;
import com.jefff.exercise.utility.ResourceUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * Unit test for simple App.
 */
@Slf4j
public class ProcessorTest {
    public static final String TEST_OUTPUT_FILE = "expected.json";
    public static final String EXPECTED_OUTPUT_FILE = "expected.json";

    LineReader mockLineReader;
    ErrorDetector mockErrorDetector;
    LogParser mockLogParser;
    OutputManager mockOutputManager;
    LogEntry mockLogEntry;

    Processor createProcessorFromMocks() {
        mockLineReader = mock(LineReader.class);
        mockErrorDetector = mock(ErrorDetector.class);
        mockLogParser = mock(LogParser.class);
        mockOutputManager = mock(OutputManager.class);
        mockLogEntry = mock(LogEntry.class);
        Processor processor = new Processor(0,
                                            mockLineReader,
                                            mockLogParser,
                                            mockErrorDetector,
                                            mockOutputManager);
        return processor;
    }

    Processor createRealProcessor() {
        LineReader lineReader = new LineReader(ArgParser.DEFAULT_INPUT_FILE);
        LogParser logParser = new LogParser();
        ErrorDetector errorDetector = new ErrorDetector();
        JsonPrinterFactory jsonPrinterFactory = new JsonPrinterFactory("temp.json");
        OutputManager outputManager = new OutputManager(TEST_OUTPUT_FILE,
                                                        new LogEntryWindow(),
                                                        jsonPrinterFactory);
        Processor processor = new Processor(0, lineReader, logParser, errorDetector, outputManager);
        return processor;
    }

    @Test
    public void integrationTest() throws Exception {
        final Processor realProcessor = createRealProcessor();
        realProcessor.run();
        diffOutputVsExpected();
    }

    @Test
    public void testOneLineSuccessScenario() throws IOException {
        String inputLine = "abc";
        String logMessage = "def";
        String nullLine = null;
        final Processor processor = createProcessorFromMocks();
        when(mockLineReader.readLine()).thenReturn(inputLine, nullLine);
        when(mockLogParser.parseLogEntry(inputLine, 1)).thenReturn(mockLogEntry);
        when(mockLogEntry.getMessage()).thenReturn(logMessage);
        when(mockErrorDetector.hasErrors(logMessage)).thenReturn(true);
        processor.run();
        verify(mockOutputManager, times(1)).addLogEntry(true, mockLogEntry, 1);
        verify(mockLineReader, times(1)).close();
        verify(mockOutputManager, times(1)).close(false);
    }

    @Test
    public void testRunWithLineReaderOpenException() throws Exception {
        final Processor processor = createProcessorFromMocks();
        doThrow(new RuntimeException()).when(mockLineReader).open();
        processor.run();
        verifyNoInteractions(mockErrorDetector);
        verifyNoInteractions(mockOutputManager);
        verifyNoInteractions(mockLogParser);
    }

    @Test
    public void testRunWithOutputManagerOpenException() throws IOException {
        final Processor processor = createProcessorFromMocks();
        doThrow(new RuntimeException()).when(mockOutputManager).open();
        processor.run();
        verify(mockLineReader, times(1)).close();
        verifyNoInteractions(mockErrorDetector);
        verify(mockOutputManager, times(0)).addLogEntry(anyBoolean(), any(LogEntry.class), anyInt());
        verify(mockOutputManager, times(0)).close(anyBoolean());
        verifyNoInteractions(mockLogParser);
    }

    @Test
    public void testRunWithOutputManagerAddLogException() throws IOException {
        String inputLine = "abc";
        String logMessage = "def";
        String nullLine = null;
        final Processor processor = createProcessorFromMocks();
        when(mockLineReader.readLine()).thenReturn(inputLine, nullLine);
        when(mockLogParser.parseLogEntry(inputLine, 1)).thenReturn(mockLogEntry);
        when(mockLogEntry.getMessage()).thenReturn(logMessage);
        when(mockErrorDetector.hasErrors(logMessage)).thenReturn(true);
        doThrow(new RuntimeException()).when(mockOutputManager).addLogEntry(true, mockLogEntry, 1);
        processor.run();
        verify(mockOutputManager, times(1)).addLogEntry(true, mockLogEntry, 1);
        verify(mockLineReader, times(1)).close();
        verify(mockOutputManager, times(1)).close(true);
    }

    @Test
    public void testRunWithLineReaderReadLineException() throws IOException {
        final Processor processor = createProcessorFromMocks();
        when(mockLineReader.readLine()).thenThrow(new RuntimeException());
        processor.run();
        verifyNoInteractions(mockErrorDetector);
        verify(mockOutputManager, times(0)).addLogEntry(true, mockLogEntry, 1);
        verify(mockLineReader, times(1)).close();
        verify(mockOutputManager, times(1)).close(true);
    }


    void diffOutputVsExpected() throws Exception {
        LineReader actualReader = new LineReader(TEST_OUTPUT_FILE);
        final File resourceFile = ResourceUtility.getResourceFile(EXPECTED_OUTPUT_FILE);
        LineReader expectedReader = new LineReader(resourceFile);

        actualReader.open();
        expectedReader.open();

        int lineCount = 0;
        while (true) {
            String actualLine = actualReader.readLine();
            String expectedLine = expectedReader.readLine();
            if (actualLine == null && expectedLine == null) {
                break;
            }
            lineCount++;
            Assert.assertNotNull(actualLine);
            Assert.assertNotNull(expectedLine);
            Assert.assertEquals(expectedLine.trim(), actualLine.trim());
        }
        log.info("Successfully compared {} lines of actual output to expected output", lineCount);
    }
}
