package com.wasteinformationserver.basicutils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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

    public static final int CRITICAL_ERROR = 6;
    public static final int ERROR = 5;
    public static final int WARNING = 4;
    public static final int INFO = 3;
    public static final int MESSAGE = 2;
    public static final int DEBUG = 1;

    private static ArrayList<String> colors = new ArrayList<String>(Arrays.asList("", "DEBUG", "MESSAGE", "INFO", "WARNING", "ERROR", "CRITICAL_ERROR"));

    public static void criticalerror(String msg) {
        log(msg, CRITICAL_ERROR);
    }

    public static void error(String msg) {
        log(msg, ERROR);
    }

    public static void warning(String msg) {
        log(msg, WARNING);
    }

    public static void info(String msg) {
        log(msg, INFO);
    }

    public static void message(String msg) {
        log(msg, MESSAGE);
    }

    public static void debug(String msg) {
        log(msg, DEBUG);
    }


    public static void log(String msg, int level) {
        StringBuilder builder = new StringBuilder();
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
        builder.append("[");
        builder.append(calcDate(System.currentTimeMillis()));
        builder.append("]");

        builder.append(" [");
        builder.append(new Exception().getStackTrace()[2].getClassName());
        builder.append("]");

        builder.append(" [");
        builder.append(colors.get(level));
        builder.append("]");

        builder.append(ANSI_WHITE);
        builder.append(" - ");
        builder.append(msg);

        builder.append(ANSI_RESET);
        System.out.println(builder.toString());
    }

    private static String calcDate(long millisecs) {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date resultdate = new Date(millisecs);
        return date_format.format(resultdate);
    }

}
