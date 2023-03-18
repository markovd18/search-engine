package cz.zcu.kiv.nlp.ir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializedDataHelper {

    private final static Logger logger = LoggerFactory.getLogger(SerializedDataHelper.class);
    static final java.text.DateFormat SDF = new SimpleDateFormat("yyyy-MM-dd_HH_mm_SS");

    public static <T> Collection<T> loadData(final File serializedFile, final Class<T> type) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(serializedFile))) {
            final Object object = objectInputStream.readObject();
            if (object instanceof Collection<?> collection) {
                return collection.stream()
                        .map((item) -> type.cast(item))
                        .collect(Collectors.toList());
            }

            logger.warn("Attempting to load data of invalid type", object);
            return Collections.emptyList();
        } catch (Exception ex) {
            logger.error("Error while loading serialized documents", ex);
            throw new RuntimeException(ex);
        }
    }

    public static <T> void saveData(final File outputFile, final Collection<T> data) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(outputFile))) {
            objectOutputStream.writeObject(data);
            logger.info("Data saved to " + outputFile.getPath());
        } catch (final IOException e) {
            logger.error("Error while saving serialized data", e);
            throw new RuntimeException(e);
        }
    }
}
