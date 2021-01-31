package de.judgeman.H2SpringFx.Tests.ServiceTests;

import de.judgeman.H2SpringFx.Services.LogService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.*;
import java.util.*;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class LogServiceTests {

    private static final String TEST_LOG_DIRECTORY = "testLogsWithLock";
    private static final String TEST_LOG_FILE_NAME = "testLogFile.log";

    private static final String TEST_TEXT_1 = "Sam isst gerade ein 'Samich'.";
    private static final String TEST_TEXT_2 = "Ivy macht den Dancing Dog. ";
    private static final String TEST_TEXT_3 = "Malina lacht mit Mama.";
    private static final String TEST_TEXT_4 = String.format("%s%s",TEST_TEXT_2, TEST_TEXT_3);
    private static final String TEST_TEXT_5 = "One Coffee please C|_|";
    private static final String TEST_TEXT_6 = "Wie nennt man ein verschwundenes Rindtier?"; // Oxford :D

    private static HashMap<String, Integer> searchMap;

    @BeforeAll
    public static void setupTestData() {
        searchMap = new HashMap<>();
        searchMap.put(TEST_TEXT_1, 0);
        searchMap.put(TEST_TEXT_2, 0);
        searchMap.put(TEST_TEXT_3, 0);
        searchMap.put(TEST_TEXT_4, 0);
        searchMap.put(TEST_TEXT_5, 0);
        searchMap.put(TEST_TEXT_6, 0);
    }

    @Test
    public void loggerTest() {
        File currentDir = new File("");
        String fileName = LogService.generateNewFileName();
        File logFile = new File(String.format("%s/%s/%s", currentDir.getAbsolutePath(), LogService.LOG_DIRECTORY_NAME, fileName));

        LogService.setLogFilePrintStream(LogService.createNewLogFileAndPrintStream(LogService.LOG_DIRECTORY_NAME, fileName, true));

        Logger logger = LogService.getLogger(LogServiceTests.class);
        logger.info(TEST_TEXT_1);

        searchInLastLogFile(logFile.getAbsolutePath(), searchMap);

        Assertions.assertSame(searchMap.get(TEST_TEXT_1), 1);
    }

    @Test
    public void writeToConsoleViaLogServiceTest() {
        File currentDir = new File("");
        String fileName = LogService.generateNewFileName();
        File logFile = new File(String.format("%s/%s/%s", currentDir.getAbsolutePath(), LogService.LOG_DIRECTORY_NAME, fileName));

        LogService.setLogFilePrintStream(LogService.createNewLogFileAndPrintStream(LogService.LOG_DIRECTORY_NAME, fileName, true));

        LogService.printToLogFile(TEST_TEXT_2, false);
        LogService.printToLogFile(TEST_TEXT_3, true);

        searchInLastLogFile(logFile.getAbsolutePath(), searchMap);

        Assertions.assertSame(1, searchMap.get(TEST_TEXT_2));
        Assertions.assertSame(1, searchMap.get(TEST_TEXT_3));
        Assertions.assertSame(1, searchMap.get(TEST_TEXT_4));
    }

    @Test
    public void tieSystemOutAndErrToFileLoggingTest() {
        File currentDir = new File("");
        String fileName = LogService.generateNewFileName();
        File logFile = new File(String.format("%s/%s/%s", currentDir.getAbsolutePath(), LogService.LOG_DIRECTORY_NAME, fileName));

        LogService.setLogFilePrintStream(LogService.createNewLogFileAndPrintStream(LogService.LOG_DIRECTORY_NAME, fileName, true));

        System.out.println(TEST_TEXT_5);

        searchInLastLogFile(logFile.getAbsolutePath(), searchMap);
        Assertions.assertSame(searchMap.get(TEST_TEXT_5), 0);

        LogService.tieSystemOutAndErrToFileLogging();
        System.out.println(TEST_TEXT_5);

        searchInLastLogFile(logFile.getAbsolutePath(), searchMap);
        Assertions.assertSame(searchMap.get(TEST_TEXT_5), 1);
    }

    @Test
    public void createNewLogFileAndPrintStreamTest() {
        File currentDirectory = new File("");
        File tempDirectory = new File(String.format("%s/%s", currentDirectory.getAbsolutePath(), TEST_LOG_DIRECTORY));
        File testLogFile = new File(String.format("%s%s%s",tempDirectory, "/", TEST_LOG_FILE_NAME));

        removeTestDirectoryAndTestLogFile(tempDirectory, testLogFile);
        Assertions.assertFalse(testLogFile.exists());

        PrintStream printStream = LogService.createNewLogFileAndPrintStream(TEST_LOG_DIRECTORY, TEST_LOG_FILE_NAME, false);
        LogService.setLogFilePrintStream(printStream);
        Assertions.assertNull(printStream);

        LogService.printToLogFile(TEST_TEXT_6, true);
        searchInLastLogFile(testLogFile.getPath(), searchMap);
        Assertions.assertSame(0, searchMap.get(TEST_TEXT_6));

        try {
            printStream = LogService.createNewLogFileAndPrintStream(TEST_LOG_DIRECTORY, TEST_LOG_FILE_NAME, true);
            Assertions.assertTrue(testLogFile.exists());

            assert printStream != null;
            printStream.println(TEST_TEXT_6);

            searchInLastLogFile(testLogFile.getAbsolutePath(), searchMap);
            Assertions.assertSame(1, searchMap.get(TEST_TEXT_6));

            LogService.setLogFilePrintStream(printStream);

            LogService.printToLogFile(TEST_TEXT_6, true);
            searchInLastLogFile(testLogFile.getAbsolutePath(), searchMap);
            // second time the search text is twice in the file and we are already found one line in the last search
            Assertions.assertSame( 3, searchMap.get(TEST_TEXT_6));
        } finally {
            if (printStream != null) {
                printStream.close();
            }

            removeTestDirectoryAndTestLogFile(tempDirectory, testLogFile);
        }
    }

    private void removeTestDirectoryAndTestLogFile(File tempDirectory, File testLogFile) {
        if (testLogFile.exists() && testLogFile.isFile()) {
            Assertions.assertTrue(testLogFile.delete());
        }

        if (tempDirectory.exists() && tempDirectory.isDirectory()) {
            Assertions.assertTrue(tempDirectory.delete());
        }
    }

    private void searchInLastLogFile(String path, HashMap<String, Integer> searchMap) {
        System.out.println("search in : " + path);
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(path));
            String line;
            do {
                line = reader.readLine();
                checkKeyFromSearchMapInLine(line, searchMap);
            } while (line != null);
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void checkKeyFromSearchMapInLine(String line, HashMap<String, Integer> searchMap) {
        if (line == null) {
            return;
        }

        for (String key: searchMap.keySet()) {
            if (line.contains(key)) {
                searchMap.put(key, searchMap.get(key) + 1);
            }
        }
    }
}
