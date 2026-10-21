package util;

import model.Account;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Lab 11 – Handles serialization and deserialization of the accounts array.
 * Uses try-with-resources for safe stream management.
 */
public final class StatePersister {

    private StatePersister() { /* utility class */ }

    /**
     * Saves the account array to {@code file} using Java object serialization.
     *
     * @param accounts the array of accounts to save
     * @param file     the target file path
     * @throws IOException on any I/O failure
     */
    public static void save(Account[] accounts, Path file) throws IOException {
        Files.createDirectories(file.getParent());
        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(file))) {
            oos.writeObject(accounts);
            System.out.println("[StatePersister] Saved " + accounts.length
                    + " account(s) to " + file);
        }
    }

    /**
     * Loads and returns the account array from {@code file}.
     *
     * @param file the source file path
     * @return the deserialized account array
     * @throws IOException            on any I/O failure
     * @throws ClassNotFoundException if the class definition is not found
     */
    public static Account[] load(Path file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(file))) {
            Account[] accounts = (Account[]) ois.readObject();
            System.out.println("[StatePersister] Loaded " + accounts.length
                    + " account(s) from " + file);
            return accounts;
        }
    }
}
