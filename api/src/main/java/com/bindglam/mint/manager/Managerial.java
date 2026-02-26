package com.bindglam.mint.manager;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Manager interface
 *
 * @author bindglam
 */
public interface Managerial {
    @ApiStatus.Internal
    default void preload(@NotNull Context context) {
    }

    @ApiStatus.Internal
    default void start(@NotNull Context context) {
    }

    @ApiStatus.Internal
    default void end(@NotNull Context context) {
    }

    @ApiStatus.Internal
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
