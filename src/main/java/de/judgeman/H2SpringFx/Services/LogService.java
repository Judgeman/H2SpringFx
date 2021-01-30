package de.judgeman.H2SpringFx.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Paul Richter on Wed 30/03/2020
 */
public class LogService {

    public static Logger getLogger(Class clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    private LogService() {
        // do nothing
    }

    private static PrintStream logFilePrintStream = createNewLogFileAndPrintStream();

    public static final String LOG_DIRECTORY_NAME = "logs";
    public static final String LOG_NAME_PREFIX = "h2SpringFx_";

    public static void tieSystemOutAndErrToFileLogging() {
        System.setOut(createLoggingProxy(System.out));
        System.setErr(createLoggingProxy(System.err));
    }

    public static void printToLogFile(String text, boolean breakLine) {
        if (logFilePrintStream == null) {
            return;
        }

        logFilePrintStream.print(text);
        if(breakLine) {
            logFilePrintStream.println();
        }
    }

    public static void setLogFilePrintStream(PrintStream printStream) {
        logFilePrintStream = printStream;
    }

    private static PrintStream createLoggingProxy(final PrintStream realPrintStream) {
        return new PrintStream(realPrintStream) {
            public void print(final String string) {
                realPrintStream.print(string);
                printToLogFile(string, true);
            }
        };
    }

    public static PrintStream createNewLogFileAndPrintStream() {
        return createNewLogFileAndPrintStream(LOG_DIRECTORY_NAME, generateNewFileName(), true);
    }

    public static PrintStream createNewLogFileAndPrintStream(String logDirectoryName, String fileName, boolean createDirectoriesToPath) {
        File logsDir = new File("");
        logsDir = new File(String.format("%s/%s", logsDir.getAbsolutePath(), logDirectoryName));

        String logFileName = generateNewLogFilePath(logsDir.getAbsolutePath(), fileName);
        if (createDirectoriesToPath) {
            logsDir.mkdirs();
        }

        try {
            System.out.println("New PrintStreamFile: " + logFileName);
            return new PrintStream(new BufferedOutputStream(new FileOutputStream(logFileName)), true);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String generateNewFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");
        return String.format("%s%s%s", LOG_NAME_PREFIX, dateFormat.format(new Date()), ".log");
    }

    public static String generateNewLogFilePath(String logDirectoryName, String fileName) {
        return String.format("%s/%s", logDirectoryName, fileName);
    }
}
