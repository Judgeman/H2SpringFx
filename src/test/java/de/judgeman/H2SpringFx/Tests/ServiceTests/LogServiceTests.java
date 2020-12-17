package de.judgeman.H2SpringFx.Tests.ServiceTests;

import de.judgeman.H2SpringFx.Services.LogService;
import org.junit.Assert;
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

    private static final String PATH_LOG_DIRECTORY = String.format("%s%s", "./", LogService.LOG_DIRECTORY_NAME);
    private static final String TEST_LOG_DIRECTORY = "testLogsWithLock";
    private static final String TEST_LOG_FILE_NAME = "testLogFile.log";

    private static final String TEST_TEXT_1 = "Sam isst gerade ein 'Samich'.";
    private static final String TEST_TEXT_2 = "Ivy macht den Dancing Dog. ";
    private static final String TEST_TEXT_3 = "Malina lacht mit Mama.";
    private static final String TEST_TEXT_4 = String.format("%s%s",TEST_TEXT_2, TEST_TEXT_3);
    private static final String TEST_TEXT_5 = "One Coffee please C|_|";
    private static final String TEST_TEXT_6 = "Wie nennt man ein verschwundenes Rindtier? — Oxford.";

    private static HashMap<String, Integer> searchMap;

    @BeforeAll
    public static void setupTestGUI() {
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
        Logger logger = LogService.getLogger(LogServiceTests.class);
        logger.info(TEST_TEXT_1);

        String pathOfLastLogFile = getPathOfLastLogfile();
        searchInLastLogFile(pathOfLastLogFile, searchMap);

        Assert.assertSame(searchMap.get(TEST_TEXT_1), 1);
    }

    @Test
    public void writeToConsoleViaLogServiceTest() {
        LogService.printToLogFile(TEST_TEXT_2, false);
        LogService.printToLogFile(TEST_TEXT_3, true);

        String pathOfLastLogFile = getPathOfLastLogfile();
        searchInLastLogFile(pathOfLastLogFile, searchMap);

        Assert.assertSame(searchMap.get(TEST_TEXT_2), 1);
        Assert.assertSame(searchMap.get(TEST_TEXT_3), 1);
        Assert.assertSame(searchMap.get(TEST_TEXT_4), 1);
    }

    @Test
    public void tieSystemOutAndErrToFileLoggingTest() {
        System.out.println(TEST_TEXT_5);

        String pathOfLastLogFile = getPathOfLastLogfile();
        searchInLastLogFile(pathOfLastLogFile, searchMap);
        Assert.assertSame(searchMap.get(TEST_TEXT_5), 0);

        LogService.tieSystemOutAndErrToFileLogging();
        System.out.println(TEST_TEXT_5);

        searchInLastLogFile(pathOfLastLogFile, searchMap);
        Assert.assertSame(searchMap.get(TEST_TEXT_5), 1);
    }

    @Test
    public void createNewLogFileAndPrintStreamTest() {
        PrintStream printStream = LogService.createNewLogFileAndPrintStream(TEST_LOG_DIRECTORY, TEST_LOG_FILE_NAME, false);
        Assert.assertNull(printStream);

        LogService.printToLogFile(TEST_TEXT_6, true);

        File tempDirectory = new File(String.format("%s%s" ,"./", TEST_LOG_DIRECTORY));
        File testLogFile = new File(String.format("%s%s%s",tempDirectory, "/", TEST_LOG_FILE_NAME));

        try {
            printStream = LogService.createNewLogFileAndPrintStream(TEST_LOG_DIRECTORY, TEST_LOG_FILE_NAME, true);

            printStream.println(TEST_TEXT_6);

            searchInLastLogFile(testLogFile.getPath(), searchMap);
            Assert.assertSame(searchMap.get(TEST_TEXT_6), 1);

            LogService.printToLogFile(TEST_TEXT_6, true);
            searchInLastLogFile(testLogFile.getPath(), searchMap);
            Assert.assertSame(searchMap.get(TEST_TEXT_6), 2);
        } finally {
            if (printStream != null) {
                printStream.close();
            }

            testLogFile.delete();
            tempDirectory.delete();
        }
    }

    private String getPathOfLastLogfile() {
        File logFolder = new File(PATH_LOG_DIRECTORY);
        Assert.assertTrue(logFolder.isDirectory());

        File[] files = logFolder.listFiles();
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        return files[0].getPath();
    }

    private void searchInLastLogFile(String path, HashMap<String, Integer> searchMap) {
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
