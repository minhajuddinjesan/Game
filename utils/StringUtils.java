package be.kdg.oopswetried.utils;

import be.kdg.oopswetried.model.Player;

import java.util.Arrays;

public final class StringUtils {
    private StringUtils() {
        throw new AssertionError("This constructor should never be called!");
    }

    public static void printRushHourLogo() {
        String logo = """
                   ___    _   _    ___    _  _             _  _     ___    _   _    ___  \s
                  | _ \\  | | | |  / __|  | || |     o O O | || |   / _ \\  | | | |  | _ \\ \s
                  |   /  | |_| |  \\__ \\  | __ |    o      | __ |  | (_) | | |_| |  |   / \s
                  |_|_\\   \\___/   |___/  |_||_|   TS__[O] |_||_|   \\___/   \\___/   |_|_\\ \s
                _|""\"""|_|""\"""|_|""\"""|_|""\"""| {======|_|""\"""|_|""\"""|_|""\"""|_|""\"""|\s
                "`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'./o--000'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'
                """;

        System.out.println(logo);
    }

    public static void printRushHourLogo(Player player) {
        printRushHourLogo();
        System.out.println("You are " + player.getUsername() + "!\n");
    }

    public static void emptyTerminal() {
        System.out.println("\n".repeat(100));
    }

    // ChatGPT generated code for printing the tables more pretty
    public static void printTable(String[] headers, String[][] data, boolean includeRanking) {
        // Filter out empty rows (rows with all null or blank fields)
        data = Arrays.stream(data)
                .filter(row -> Arrays.stream(row).anyMatch(cell -> cell != null && !cell.trim().isEmpty()))
                .toArray(String[][]::new);

        // If ranking is included, prepend a "Nr." column to headers and data
        String[] finalHeaders = headers;
        String[][] finalData = data;

        if (includeRanking) {
            finalHeaders = new String[headers.length + 1];
            finalHeaders[0] = "Nr.";
            System.arraycopy(headers, 0, finalHeaders, 1, headers.length);

            finalData = new String[data.length][headers.length + 1];
            for (int i = 0; i < data.length; i++) {
                finalData[i][0] = String.valueOf(i + 1); // Add ranking number
                System.arraycopy(data[i], 0, finalData[i], 1, data[i].length);
            }
        }

        // Determine column widths (Username fixed to 30, others based on content)
        int[] columnWidths = new int[finalHeaders.length];
        for (int i = 0; i < finalHeaders.length; i++) {
            columnWidths[i] = finalHeaders[i].length(); // Start with header width
        }

        for (String[] row : finalData) {
            for (int i = 0; i < row.length; i++) {
                if (row[i] != null && row[i].length() > columnWidths[i]) {
                    columnWidths[i] = row[i].length();
                }
            }
        }

        // Force "Username" column to 30 characters wide
        for (int i = 0; i < finalHeaders.length; i++) {
            if (finalHeaders[i].equalsIgnoreCase("Username")) {
                columnWidths[i] = 30;
            }
        }

        // Build the format string for each row
        StringBuilder formatBuilder = new StringBuilder("| ");
        for (int width : columnWidths) {
            formatBuilder.append("%-").append(width).append("s | ");
        }
        String format = formatBuilder.toString();

        // Print table
        printTableBorder(columnWidths);
        System.out.printf(format + "%n", (Object[]) finalHeaders); // Print headers
        printTableBorder(columnWidths);

        for (String[] row : finalData) {
            System.out.printf(format + "%n", (Object[]) row); // Print data rows
        }
        printTableBorder(columnWidths);
    }

    public static void printTableBorder(int[] columnWidths) {
        StringBuilder borderBuilder = new StringBuilder("+");
        for (int width : columnWidths) {
            borderBuilder.append("-".repeat(width + 2)).append("+");
        }
        System.out.println(borderBuilder);
    }
}
