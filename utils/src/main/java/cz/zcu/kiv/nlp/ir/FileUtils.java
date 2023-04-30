package cz.zcu.kiv.nlp.ir;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Collection of utility functions for working with files.
 */
public class FileUtils {

    public static final java.text.DateFormat SDF = new SimpleDateFormat("yyyy-MM-dd_HH_mm_SS");

    /**
     * Read lines from the stream; lines are trimmed and empty lines are ignored.
     *
     * @param inputStream
     *            stream
     * @return list of lines
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public static List<String> readLines(final InputStream inputStream)
            throws UnsupportedEncodingException, IOException {
        if (inputStream == null) {
            return Collections.emptyList();
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            List<String> result = new ArrayList<String>();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    result.add(line.trim());
                }
            }

            return result;
        }
    }

    /**
     * Read lines from the stream; lines are trimmed and empty lines are ignored.
     *
     * @param inputStream
     *            stream
     * @return text
     */
    public static Optional<String> readFile(final InputStream inputStream) {
        if (inputStream == null) {
            return Optional.empty();
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            return Optional.of(sb.toString().trim());
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Saves lines from the list into given file; each entry is saved as a new line.
     *
     * @param file
     *            file to save
     * @param list
     *            lines of text to save
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static void saveFile(final File file, final Collection<String> list)
            throws UnsupportedEncodingException, FileNotFoundException {
        try (PrintStream printStream = new PrintStream(new FileOutputStream(file), true, "UTF-8")) {
            for (String text : list) {
                printStream.println(text);
            }
        }
    }

    /**
     * Saves lines from the list into given file; each entry is saved as a new line.
     *
     * @param file
     *            file to save
     * @param text
     *            text to save
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static void saveFile(final File file, final String text)
            throws UnsupportedEncodingException, FileNotFoundException {
        try (PrintStream printStream = new PrintStream(new FileOutputStream(file), true, "UTF-8")) {
            printStream.println(text);
        }
    }
}
