/*
                CPCS324 - CPCS 324: Algorithms and Data Structures I
ID: 2206712 | Name: Manar Abdullah Alharbi | Email:  malharbi1414@stu.kau.edu.sa | Section: 03
ID: 2210455 | Name: Lamar Haitham Fatani   | Email:  lfatani0001@stu.kau.edu.sa  | Section: 03
ID: 2206375 | Name: Alzahra Abed Alamri    | Email:  aalamri0838@stu.kau.edu.sa  | Section: 03
 ______________________________________________________________________________________________
Brief Description:
   This program compares the Brute Force and Horspool algorithms for pattern
   searching. It reads text from an input file, generates random patterns, and
   calculates average search times. Results are stored in a file, and
   performance is analyzed for both algorithms.
 */
package cpcs2024_group10_pt1;

import java.io.*;
import java.util.*;

public class CPCS2024_Group10_pt1 {

    public static void main(String[] args) {
        // Initialize scanner to read user input
        Scanner scanner = new Scanner(System.in);

        try {
            // Get the number of lines to read from the input file
            System.out.println("How many lines you want to read from the text file?");
            int lineCount = scanner.nextInt();

            // Get the length of each generated pattern
            System.out.println("What is the length of each pattern?");
            int patternLength = scanner.nextInt();

            // Get the number of patterns to generate
            System.out.println("How many patterns to be generated?");
            int patternCount = scanner.nextInt();

            // Read lines from the text file
            String text = readInputText(lineCount);

            // Check if the text is long enough for the specified pattern length
            if (text.length() < patternLength) {
                System.out.println("The text is too short for the specified pattern length. There will be no shift table generated.");
                return;
            }

            // Get distinct characters present in the text
            Set<Character> distinctCharsInText = new HashSet<>();
            for (char c : text.toCharArray()) {
                distinctCharsInText.add(c);
            }

            // Generate random patterns from the text
            String[] patterns = generatePatterns(patternCount, patternLength, text);
            // Store the generated patterns in a file
            storePatterns(patterns);

            // Print the generated patterns
            System.out.printf("\n%d Patterns, each of length %d, have been generated in a file 'patterns.txt'\n\n", patternCount, patternLength);

            // Initialize variables to track total search times
            double totalTimeBruteForce = 0.0;
            double totalTimeHorspool = 0.0;

            // Brute Force search
            System.out.println("************* Brute Force *************");
            for (String pattern : patterns) {
                System.out.println("Pattern: " + pattern);
                // Measure time taken for brute force search
                long startTime = System.nanoTime();
                int bruteForceResult = bruteForceSearch(text, pattern);
                long endTime = System.nanoTime();
                double elapsedTime = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
                totalTimeBruteForce += elapsedTime;
                // Output the result of the brute force search
                if (bruteForceResult == -1) {
                    System.out.println("Brute Force: Pattern not found");
                } else {
                    System.out.printf("Brute Force: Pattern found at index %d\n", bruteForceResult);
                }
            }

            // Horspool search
            int tableNumber = 1;
            System.out.println("\n************* Horspool *************");
            for (String pattern : patterns) {
                System.out.println("Pattern: " + pattern);

                // Build and print the shift table for the Horspool algorithm
                Map<Character, Integer> shiftTable = buildShiftTable(pattern, distinctCharsInText);
                printShiftTable(shiftTable, pattern, tableNumber);
                tableNumber++;

                // Measure time taken for Horspool search
                long startTime = System.nanoTime();
                int horspoolResult = horspoolSearch(text, pattern, shiftTable);
                long endTime = System.nanoTime();
                double elapsedTime = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
                totalTimeHorspool += elapsedTime;
                // Output the result of the Horspool search
                if (horspoolResult == -1) {
                    System.out.println("Horspool: Pattern not found\n");
                } else {
                    System.out.printf("Horspool: Pattern found at index %d\n\n", horspoolResult);
                }
            }

            // Summary of search times
            System.out.println("*********************************************************************");
            System.out.printf("\nAverage time of search in Brute Force Approach: %.3f ms\n", totalTimeBruteForce / patterns.length);
            System.out.printf("Average time of search in Horspool Approach: %.3f ms\n", totalTimeHorspool / patterns.length);

            // Compare the two search methods and output the result
            if (totalTimeHorspool < totalTimeBruteForce) {
                System.out.println("\nFor this instance, Horspool approach is better than Brute Force approach\n");
            } else {
                System.out.println("\nFor this instance, Brute Force approach is better than Horspool approach\n");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter numerical values.");
        } finally {
            // Close the scanner to prevent resource leaks
            scanner.close();
        }
    }

    // Method to read the specified number of lines from a file
    private static String readInputText(int numLines) {
        StringBuilder sb = new StringBuilder();
        // Use Scanner to read from file
        try (Scanner fileScanner = new Scanner(new File("input.txt"))) {
            int i = 0;
            while (fileScanner.hasNextLine() && i < numLines) {
                String line = fileScanner.nextLine();
                sb.append(line.toLowerCase());
                sb.append("\n");
                i++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found.");
            e.printStackTrace();
        }
        return sb.toString();
    }

    // Method to generate random patterns from the text
    public static String[] generatePatterns(int patternCount, int patternLength, String text) {
        text = text.replaceAll("[^a-zA-Z]", ""); // Clean the text of non-alphabetic characters
        String[] patterns = new String[patternCount];
        Random random = new Random();

        // Generate random patterns of specified length from the text
        for (int i = 0; i < patternCount; i++) {
            int start = random.nextInt(text.length() - patternLength + 1);
            patterns[i] = text.substring(start, start + patternLength);
        }
        return patterns;
    }

    // Method to store generated patterns in a file
    public static void storePatterns(String[] patterns) {
        try (PrintWriter pw = new PrintWriter(new File("patterns.txt"))) {
            for (String pattern : patterns) {
                pw.println(pattern);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Unable to create patterns.txt file.");
            e.printStackTrace();
        }
    }

    // Brute Force search algorithm implementation
    public static int bruteForceSearch(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();

        // Check each substring of text to see if it matches the pattern
        for (int i = 0; i <= n - m; i++) {
            int j;
            for (j = 0; j < m; j++) {
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    break; // Break if a mismatch is found
                }
            }
            if (j == m) {
                return i; // Return the starting index if pattern is found
            }
        }
        return -1; // Return -1 if the pattern is not found
    }

    // Horspool's search algorithm implementation
    public static int horspoolSearch(String text, String pattern, Map<Character, Integer> shiftTable) {
        int n = text.length();
        int m = pattern.length();

        int i = m - 1; // Start at the end of the pattern
        while (i < n) {
            int k = 0;
            // Compare the pattern to the text starting from the end
            while (k < m && pattern.charAt(m - 1 - k) == text.charAt(i - k)) {
                k++;
            }
            if (k == m) {
                return i - m + 1; // Return starting index if pattern is found
            } else {
                // Shift the index based on the shift table
                char nextChar = text.charAt(i);
                i += shiftTable.getOrDefault(nextChar, m);
            }
        }
        return -1; // Return -1 if the pattern is not found
    }

    // Method to build the shift table for Horspool's algorithm
    public static Map<Character, Integer> buildShiftTable(String pattern, Set<Character> distinctCharsInText) {
        int m = pattern.length();
        Map<Character, Integer> table = new HashMap<>();

        // Set default shift values for all distinct characters in the text
        for (char c : distinctCharsInText) {
            table.put(c, m);
        }

        // Update shift values for characters in the pattern
        for (int i = 0; i < m - 1; i++) {
            table.put(pattern.charAt(i), m - 1 - i);
        }
        return table;
    }

    // Method to print the shift table in a neat tabular format
    public static void printShiftTable(Map<Character, Integer> shiftTable, String pattern, int tableNumber) {
        System.out.println("\nShift Table " + tableNumber + ":");
        System.out.println("+----------------------+----------------+");
        System.out.println("| Character            | Shift Value    |");
        System.out.println("+----------------------+----------------+");
        for (Map.Entry<Character, Integer> entry : shiftTable.entrySet()) {
            System.out.printf("| %-20c | %-14d |\n", entry.getKey(), entry.getValue());
        }
        System.out.println("+----------------------+----------------+");
    }
}
