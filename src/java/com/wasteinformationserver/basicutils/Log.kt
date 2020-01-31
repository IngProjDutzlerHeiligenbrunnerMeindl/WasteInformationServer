package com.wasteinformationserver.basicutils

import java.text.SimpleDateFormat
import java.util.*

class Log {
    companion object Log{
        val CRITICAL_ERROR = 6
        val ERROR = 5
        val WARNING = 4
        val INFO = 3
        val MESSAGE = 2
        val DEBUG = 1

        private val ANSI_RESET = "\u001B[0m"
        private val ANSI_BLACK = "\u001B[30m"
        private val ANSI_RED = "\u001B[31m"
        private val ANSI_GREEN = "\u001B[32m"
        private val ANSI_YELLOW = "\u001B[33m"
        private val ANSI_BLUE = "\u001B[34m"
        private val ANSI_PURPLE = "\u001B[35m"
        private val ANSI_CYAN = "\u001B[36m"
        private val ANSI_WHITE = "\u001B[37m"

        private var Loglevel = 0

        /**
         * Log critical Error
         *
         * @param msg message
         */
        fun criticalerror(msg: Any) {
            if (Loglevel <= CRITICAL_ERROR) log(msg, CRITICAL_ERROR)
        }

        /**
         * Log basic Error
         *
         * @param msg message
         */
        fun error(msg: Any) {
            if (Loglevel <= ERROR) log(msg, ERROR)
        }

        /**
         * Log warning
         *
         * @param msg message
         */
        fun warning(msg: Any) {
            if (Loglevel <= WARNING) log(msg, WARNING)
        }

        /**
         * Log info
         *
         * @param msg message
         */
        fun info(msg: Any) {
            if (Loglevel <= INFO) log(msg, INFO)
        }

        /**
         * Log basic message
         *
         * @param msg message
         */
        fun message(msg: Any) {
            if (Loglevel <= MESSAGE) log(msg, MESSAGE)
        }

        /**
         * Log debug Message
         *
         * @param msg message
         */
        fun debug(msg: Any) {
            if (Loglevel <= DEBUG) log(msg, DEBUG)
        }

        /**
         * Log as defined
         *
         * @param msg   message
         * @param level Loglevel --> static vals defined
         */
        fun log(msg: Any, level: Int) {
            val iswindows = System.getProperty("os.name").contains("Windows")
            val builder = StringBuilder()
            if (!iswindows) {
                when (level) {
                    INFO -> builder.append(ANSI_CYAN)
                    WARNING -> builder.append(ANSI_YELLOW)
                    ERROR -> builder.append(ANSI_RED)
                    CRITICAL_ERROR -> builder.append(ANSI_RED)
                    MESSAGE -> builder.append(ANSI_WHITE)
                    DEBUG -> builder.append(ANSI_BLUE)
                }
            }
            builder.append("[")
            builder.append(calcDate(System.currentTimeMillis()))
            builder.append("]")
            builder.append(" [")
            builder.append(Exception().stackTrace[2].className)
            builder.append("]")
            builder.append(" [")
            builder.append(colors[level])
            builder.append("]")
            if (!iswindows) {
                builder.append(ANSI_WHITE)
            }
            builder.append(" - ")
            builder.append(msg.toString())
            if (!iswindows) {
                builder.append(ANSI_RESET)
            }
            println(builder.toString())
        }

        /**
         * define Loglevel call on startup or at runtime
         * default: 0[DEBUG] --> Max logging
         *
         * @param level Loglevel --> static vals defined
         */
        fun setLevel(level: Int) {
            Loglevel = level
        }
        private val colors = ArrayList(Arrays.asList("", "DEBUG", "MESSAGE", "INFO", "WARNING", "ERROR", "CRITICAL_ERROR"))



        private fun calcDate(millisecs: Long): String? {
            val date_format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val resultdate = Date(millisecs)
            return date_format.format(resultdate)
        }
    }
}