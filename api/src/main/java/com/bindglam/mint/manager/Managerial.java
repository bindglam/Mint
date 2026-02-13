package com.bindglam.mint.manager;

import org.jetbrains.annotations.NotNull;

/**
 * Manager interface
 *
 * @author bindglam
 */
public interface Managerial {
    default void start(@NotNull Context context) {
    }

    default void end(@NotNull Context context) {
    }

    default @NotNull Priority priority() {
        return Priority.empty();
    }

    record Priority(int start, int end) {
        private static final Priority EMPTY = new Priority(0, 0);

        public static @NotNull Priority empty() {
            return EMPTY;
        }

        public static @NotNull Priority of(int start, int end) {
            return new Priority(start, end);
        }
    }
}
