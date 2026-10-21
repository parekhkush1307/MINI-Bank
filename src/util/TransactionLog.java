package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Lab 11 – Appends a single transaction line to an append-only log file.
 * Creates the file and its parent directory if they do not already exist.
 */
public final class TransactionLog {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private TransactionLog() { /* utility class */ }

    /**
     * Appends {@code line} with a timestamp to {@code file}.
     *
     * @param file the log file path
     * @param line the log entry (e.g. "DEPOSIT AC0001 500")
     * @throws IOException on any I/O failure
     */
    public static void append(Path file, String line) throws IOException {
        Files.createDirectories(file.getParent());
        String entry = "[" + LocalDateTime.now().format(FMT) + "] " + line + System.lineSeparator();
        Files.writeString(file, entry,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
