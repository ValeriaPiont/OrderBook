package com.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;

public class Test {
    private static final String INPUT_FILE_NAME = "input.txt";
    private static final String OUTPUT_FILE_NAME = "output.txt";

    public static void main(String[] args) {
        var processor = new Processor();
        try (var reader = newBufferedReader(Path.of(INPUT_FILE_NAME));
             var writer = newBufferedWriter(Path.of(OUTPUT_FILE_NAME))) {
            processor.process(supply(reader), consume(writer));
        } catch (IOException ex) {
            System.err.println("Error with opening file " + ex.getMessage());
        }
    }

    static Consumer<String> consume(BufferedWriter writer) {
        return line -> {
            try {
                writer.write(line);
                writer.newLine();
            } catch (IOException ex) {
                System.err.println("Writing error " + ex.getMessage());
            }
        };
    }

    static Supplier<String> supply(BufferedReader reader) {
        return () -> {
            try {
                return reader.readLine();
            } catch (IOException ex) {
                System.err.println("Reading error " + ex.getMessage());
            }
            return null;
        };
    }

}
