package pl.sda.refactorapp.util;

public final class ArgumentValidator {

    public static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
