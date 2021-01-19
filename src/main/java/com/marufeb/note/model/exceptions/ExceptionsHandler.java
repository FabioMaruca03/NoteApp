package com.marufeb.note.model.exceptions;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Consumer;

public class ExceptionsHandler {
    private static final Deque<Exception> exceptions = new LinkedList<>();
    private static final HashMap<Class<? extends Exception>, Consumer<Exception>> mode;
    private static Consumer<Exception> operation;

    static {
        mode = new HashMap<>();
        operation = e -> {
            System.err.println("[E] - "+e.getMessage());
        };
    }

    public static void register(Exception e) {
        operation.accept(e);
        final Consumer<Exception> consumer = mode.get(e.getClass());
        if (consumer != null) consumer.accept(e);
        exceptions.add(e);
    }

    public static Exception poll() {
        return exceptions.pollLast();
    }

    public static Exception first() {
        return exceptions.pollFirst();
    }

    public static void onRegister(Consumer<Exception> operation) {
        ExceptionsHandler.operation = operation;
    }

    public static void onSpecificRegister(Consumer<Exception> operation, Class<? extends Exception> c) {
        if (mode.containsKey(c))
            mode.merge(c, operation, (o, n) -> n);
        else mode.putIfAbsent(c, operation);
    }

}
