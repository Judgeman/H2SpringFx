package de.judgeman.H2SpringFx.Appenders;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import de.judgeman.H2SpringFx.Services.LogService;
import org.slf4j.LoggerFactory;

/**
 * Created by Paul Richter on Thu 30/03/2020
 */
public class ConsoleAppender extends AppenderBase<ILoggingEvent> {

    private final PatternLayout layout;

    public ConsoleAppender() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        layout = new PatternLayout();
        layout.setContext(loggerContext);
    }

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        String message = layout.doLayout(iLoggingEvent);
        LogService.printToLogFile(message, false);
    }

    // need for initialization of the logback
    public void setPattern(String pattern) {
        if(layout.isStarted()) {
            layout.stop();
        }

        layout.setPattern(pattern);
        layout.start();
    }
}
