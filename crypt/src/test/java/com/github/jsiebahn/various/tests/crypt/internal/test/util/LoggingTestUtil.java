package com.github.jsiebahn.various.tests.crypt.internal.test.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Utility to modify the logger in tests.
 *
 * {@code LoggingTestUtil} can modify the log level when testing errors to avoid spammed log files
 * in the build process when logs are expected.
 *
 * Usage in unit test method:
 * <pre>
 *   Level before = setLogLevel(Level.OFF, TestedClass.class);
 *   try {
 *       // a test case with error situations
 *   }
 *   finally {
 *       setLogLevel(before, TestedClass.class);
 *   }
 * </pre>
 *
 * {@code LoggingTestUtil} offers a
 *
 * @author jsiebahn
 * @since 28.10.14 07:51
 */
public class LoggingTestUtil {

    /**
     * Used to switch off logging for testing corner cases to avoid output of error logs in the
     * build process. This implementation depends on using logback.
     *
     * @param level the new log level to set. Use {@link ch.qos.logback.classic.Level#OFF} to
     *              disable logging.
     * @param clazz the {@code Class} which logger should be modified
     * @return the previous configured log level. The log level should be reset in a finally block
     *          after the test case with this return value.
     */
    public static Level setLogLevel(Level level, Class clazz) {
        Logger logger = (Logger) LoggerFactory.getLogger(clazz);
        Level before = logger.getLevel();
        logger.setLevel(level);
        return before;
    }

    public static LogAssert logToTestAppender(Class clazz) {
        Logger logger = (Logger) LoggerFactory.getLogger(clazz);
        return new LogAssert(new TestAppender(logger));
    }

    public static class LogAssert {

        private TestAppender testAppender;

        public LogAssert(TestAppender testAppender) {
            this.testAppender = testAppender;
        }

        public void resetLogger() {
            testAppender.reset();
        }

        public void assertWarnings(int count) {
            assertWarnings(null, count);
        }

        public void assertWarnings(String message, int count) {
            int warnings = 0;
            for (ILoggingEvent event : testAppender.getLog()) {
                if (Level.WARN.equals(event.getLevel())) {
                    warnings++;
                }
            }
            assertEquals(message, count, warnings);
        }

        public void assertErrors(int count) {
            assertErrors(null, count);
        }

        public void assertErrors(String message, int count) {
            int warnings = 0;
            for (ILoggingEvent event : testAppender.getLog()) {
                if (Level.ERROR.equals(event.getLevel())) {
                    warnings++;
                }
            }
            assertEquals(message, count, warnings);
        }

    }

    private static class TestAppender extends AppenderBase<ILoggingEvent> {

        private List<ILoggingEvent> log = new ArrayList<>();

        private List<Appender<ILoggingEvent>> originalAppenders = new ArrayList<>();

        private Logger logger;

        private boolean wasAdditive;

        public TestAppender(Logger logger) {
            super();
            this.start();
            this.logger = logger;
            wasAdditive = this.logger.isAdditive();
            this.logger.setAdditive(false);
            this.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
            this.setName("TestAppender for " + logger.getName());
            Iterator<Appender<ILoggingEvent>> appenderIterator = this.logger.iteratorForAppenders();
            while (appenderIterator.hasNext()) {
                Appender<ILoggingEvent> appender = appenderIterator.next();
                originalAppenders.add(appender);
                this.logger.detachAppender(appender);
            }
            logger.addAppender(this);
        }

        protected void reset() {
            this.stop();
            this.logger.detachAppender(this);
            this.logger.setAdditive(wasAdditive);
            this.stop();
            for (Appender<ILoggingEvent> appender : originalAppenders) {
                this.logger.addAppender(appender);
            }
        }

        @Override
        protected void append(ILoggingEvent eventObject) {
            log.add(eventObject);
        }

        protected List<ILoggingEvent> getLog() {
            return this.log;
        }
    }

}
