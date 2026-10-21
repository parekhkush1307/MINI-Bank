package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

/**
 * Lab 11 – Walks the logs folder with {@code Files.newDirectoryStream},
 * aggregates totals, and writes an end-of-day summary report.
 */
public final class ReportGenerator {

    private ReportGenerator() { /* utility class */ }

    /**
     * Reads all {@code .log} files in {@code logsDir}, totals deposits and
     * withdrawals, and writes the report to {@code reportFile}.
     *
     * @param logsDir    directory containing {@code .log} files
     * @param reportFile path to the output report file
     * @throws IOException on any I/O failure
     */
    public static void generate(Path logsDir, Path reportFile) throws IOException {
        long totalDeposits    = 0;
        long totalWithdrawals = 0;
        int  fileCount        = 0;

        if (!Files.exists(logsDir)) {
            System.out.println("[ReportGenerator] Logs directory not found: " + logsDir);
            return;
        }

        try (DirectoryStream<Path> stream =
                     Files.newDirectoryStream(logsDir, "*.log")) {

            for (Path logFile : stream) {
                // Lab 11 Supplementary – skip empty files
                if (Files.size(logFile) == 0) continue;
                fileCount++;

                BasicFileAttributes attrs =
                        Files.readAttributes(logFile, BasicFileAttributes.class);
                System.out.printf("[ReportGenerator] Processing %s (size: %d bytes, modified: %s)%n",
                        logFile.getFileName(), attrs.size(),
                        Instant.ofEpochMilli(attrs.lastModifiedTime().toMillis()));

                try (BufferedReader reader = Files.newBufferedReader(logFile)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Line format: [timestamp] TYPE ACCOUNT AMOUNT
                        String[] parts = line.trim().split("\\s+");
                        if (parts.length < 4) continue;

                        // The type token is the second word (index 1 after timestamp)
                        String type   = parts[1];
                        String amtStr = parts[parts.length - 1]; // last token is amount
                        try {
                            long amount = Long.parseLong(amtStr);
                            if ("DEPOSIT".equalsIgnoreCase(type)) {
                                totalDeposits += amount;
                            } else if ("WITHDRAW".equalsIgnoreCase(type)
                                    || "TRANSFER".equalsIgnoreCase(type)) {
                                totalWithdrawals += amount;
                            }
                        } catch (NumberFormatException ignored) { /* skip malformed lines */ }
                    }
                }
            }
        }

        // Write the report
        Files.createDirectories(reportFile.getParent());
        String report = buildReport(fileCount, totalDeposits, totalWithdrawals);
        Files.writeString(reportFile, report);
        System.out.println("[ReportGenerator] Report written to " + reportFile);
        System.out.println(report);
    }

    private static String buildReport(int files, long deposits, long withdrawals) {
        String line = "=".repeat(45);
        return line + "\n"
             + "         END-OF-DAY SUMMARY REPORT\n"
             + line + "\n"
             + String.format("  Log files processed : %d%n", files)
             + String.format("  Total deposits      : \u20B9%,d%n", deposits)
             + String.format("  Total withdrawals   : \u20B9%,d%n", withdrawals)
             + String.format("  Net change          : \u20B9%,d%n", deposits - withdrawals)
             + line + "\n";
    }
}
