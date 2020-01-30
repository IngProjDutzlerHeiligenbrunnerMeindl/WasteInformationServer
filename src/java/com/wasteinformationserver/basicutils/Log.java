package com.wasteinformationserver.basicutils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * fancy debug and Logging messages
 *
 * @author Lukas Heiligenbrunner
 */
public class Log {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    // TODO: 30.01.20 update to enum 
//    public enum Kind{
//        CRITICAL_ERROR,
//        ERROR,
//        WARNING,
//        INFO,
//        MESSAGE,
//        DEBUG
//    }
    public static final int CRITICAL_ERROR = 6;
    public static final int ERROR = 5;
    public static final int WARNING = 4;
    public static final int INFO = 3;
    public static final int MESSAGE = 2;
    public static final int DEBUG = 1;

    private static int Loglevel = 0;

    private static ArrayList<String> colors = new ArrayList<String>(Arrays.asList("", "DEBUG", "MESSAGE", "INFO", "WARNING", "ERROR", "CRITICAL_ERROR"));

    /**
     * Log critical Error
     *
     * @param msg message
     */
    public static void criticalerror(Object msg) {
        if (Loglevel <= CRITICAL_ERROR)
            log(msg, CRITICAL_ERROR);
    }

    /**
     * Log basic Error
     *
     * @param msg message
     */
    public static void error(Object msg) {
        if (Loglevel <= ERROR)
            log(msg, ERROR);
    }

    /**
     * Log warning
     *
     * @param msg message
     */
    public static void warning(Object msg) {
        if (Loglevel <= WARNING)
            log(msg, WARNING);
    }

    /**
     * Log info
     *
     * @param msg message
     */
    public static void info(Object msg) {
        if (Loglevel <= INFO)
            log(msg, INFO);
    }

    /**
     * Log basic message
     *
     * @param msg message
     */
    public static void message(Object msg) {
        if (Loglevel <= MESSAGE)
            log(msg, MESSAGE);
    }

    /**
     * Log debug Message
     *
     * @param msg message
     */
    public static void debug(Object msg) {
        if (Loglevel <= DEBUG)
            log(msg, DEBUG);
    }

    /**
     * Log as defined
     *
     * @param msg   message
     * @param level Loglevel --> static vals defined
     */
    public static void log(Object msg, int level) {
        boolean iswindows = System.getProperty("os.name").contains("Windows");
        StringBuilder builder = new StringBuilder();
        if (!iswindows) {
            switch (level) {
                case INFO:
                    builder.append(ANSI_CYAN);
                    break;
                case WARNING:
                    builder.append(ANSI_YELLOW);
                    break;
                case ERROR:
                    builder.append(ANSI_RED);
                    break;
                case CRITICAL_ERROR:
                    builder.append(ANSI_RED);
                    break;
                case MESSAGE:
                    builder.append(ANSI_WHITE);
                    break;
                case DEBUG:
                    builder.append(ANSI_BLUE);
                    break;
            }

        }
        builder.append("[");
        builder.append(calcDate(System.currentTimeMillis()));
        builder.append("]");

        builder.append(" [");
        builder.append(new Exception().getStackTrace()[2].getClassName());
        builder.append("]");

        builder.append(" [");
        builder.append(colors.get(level));
        builder.append("]");

        if (!iswindows) {
            builder.append(ANSI_WHITE);
        }

        builder.append(" - ");
        builder.append(msg.toString());

        if (!iswindows) {
            builder.append(ANSI_RESET);
        }
        System.out.println(builder.toString());
    }

    private static String calcDate(long millisecs) {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date resultdate = new Date(millisecs);
        return date_format.format(resultdate);
    }

    /**
     * define Loglevel call on startup or at runtime
     * default: 0[DEBUG] --> Max logging
     *
     * @param level Loglevel --> static vals defined
     */
    public static void setLevel(int level) {
        Loglevel = level;
    }

}
