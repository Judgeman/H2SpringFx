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

    private static PrintStream logFilePrintStream = createNewLogFileAndPrintStream();

    private static final String LOG_DIRECTORY = "logs";
    private static final String LOG_NAME_PREFIX = "h2Springfx_";

    public static void tieSystemOutAndErrToFileLogging() {
        System.setOut(createLoggingProxy(System.out));
        System.setErr(createLoggingProxy(System.err));
    }

    public static void printToLogFile(String text, boolean breakLine) {
        logFilePrintStream.print(text);
        if(breakLine) {
            logFilePrintStream.println();
        }
    }

    private static PrintStream createLoggingProxy(final PrintStream realPrintStream) {
        return new PrintStream(realPrintStream) {
            public void print(final String string) {
                realPrintStream.print(string);
                printToLogFile(string, true);
            }
        };
    }

    private static PrintStream createNewLogFileAndPrintStream() {
        String logFileName = generateNewLogFileName();
        File newLogFile = new File(logFileName);
        File logsDir = new File(newLogFile.getParent());
        logsDir.mkdirs();

        try {
            return new PrintStream(new BufferedOutputStream(new FileOutputStream(logFileName)), true);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static String generateNewLogFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");
        return LOG_DIRECTORY + "/" + LOG_NAME_PREFIX + dateFormat.format(new Date()) + ".log";
    }
}
