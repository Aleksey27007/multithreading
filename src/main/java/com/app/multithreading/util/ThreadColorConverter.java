package com.app.multithreading.util;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;

import java.util.HashMap;
import java.util.Map;

@Plugin(name = "ThreadColor", category = PatternConverter.CATEGORY)
@ConverterKeys({"threadColor"})
public class ThreadColorConverter extends LogEventPatternConverter {

    private static final String[] COLORS = {
            "\u001B[31m", // RED
            "\u001B[32m", // GREEN
            "\u001B[33m", // YELLOW
            "\u001B[34m", // BLUE
            "\u001B[35m", // MAGENTA
            "\u001B[36m", // CYAN
            "\u001B[91m", // BRIGHT RED
            "\u001B[92m", // BRIGHT GREEN
            "\u001B[93m", // BRIGHT YELLOW
            "\u001B[94m", // BRIGHT BLUE
            "\u001B[95m", // BRIGHT MAGENTA
            "\u001B[96m", // BRIGHT CYAN
    };

    private static final String RESET = "\u001B[0m";

    private static final Map<String, String> threadColorMap = new HashMap<>();

    protected ThreadColorConverter(String[] options) {
        super("ThreadColor", "threadColor");
    }

    public static ThreadColorConverter newInstance(final String[] options) {
        return new ThreadColorConverter(options);
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        String threadName = event.getThreadName();
        String color = getColorForThread(threadName);
        toAppendTo.append(color).append(threadName).append(RESET);
    }

    private String getColorForThread(String threadName) {
        return threadColorMap.computeIfAbsent(threadName, name -> {
            int hash = Math.abs(name.hashCode());
            return COLORS[hash % COLORS.length];
        });
    }
}
