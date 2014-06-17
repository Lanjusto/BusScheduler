package ru.lanjusto.busscheduler.server.dbupdater.application;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.google.inject.Guice;
import org.json.simple.parser.ParseException;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;

/**
 * Стартёр.
 */
public class ApplicationStarter {
    public static void main(String[] args) throws IOException, ParseException {
        configureLogging();

        final Application app = Guice.createInjector(new DbModule("production")).getInstance(Application.class);
        app.startUp();
    }

    private static void configureLogging() {
        configureLogback();
        suppressJulDefaultConsoleHandler();
        //SLF4JBridgeHandler.install();
        //GlobalExceptionHandler.install();
        //AwtExceptionHandler.install();
    }

    private static void configureLogback() {
        final ILoggerFactory slf4jLoggerFactory = LoggerFactory.getILoggerFactory();

        if (!(slf4jLoggerFactory instanceof LoggerContext)) {
            return;
        }

        final LoggerContext logbackContext = (LoggerContext) slf4jLoggerFactory;
        logbackContext.reset();

        final URL logbackXml = ApplicationStarter.class.getResource("logback.xml");

        final JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(logbackContext);

        try {
            configurator.doConfigure(logbackXml);
        } catch (JoranException ignore) { // StatusPrinter will handle this
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(logbackContext);
    }

    private static void suppressJulDefaultConsoleHandler() {
        final java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                rootLogger.removeHandler(handler);
            }
        }
    }
}
