package cz.zcu.kiv.nlp.ir;

import cz.zcu.kiv.nlp.ir.data.*;
import cz.zcu.kiv.nlp.ir.factory.DefaultFactory;
import cz.zcu.kiv.nlp.ir.factory.Factory;
import cz.zcu.kiv.nlp.ir.factory.PreprocessorType;
import cz.zcu.kiv.nlp.ir.index.Index;
import cz.zcu.kiv.nlp.ir.index.Indexable;
import cz.zcu.kiv.nlp.ir.trec.data.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.*;

/**
 * @author tigi
 *
 *         Třída slouží pro vyhodnocení vámi vytvořeného vyhledávače
 *
 */
public class TestTrecEval {

    static Logger log = LoggerFactory.getLogger(TestTrecEval.class);
    static final String OUTPUT_DIR = "./TREC";

    /**
     * Metoda vytvoří objekt indexu, načte data, zaindexuje je provede
     * předdefinované dotazy a výsledky vyhledávání
     * zapíše souboru a pokusí se spustit evaluační skript.
     *
     * Na windows evaluační skript pravděpodbně nebude možné spustit. Pokud chcete
     * můžete si skript přeložit a pak
     * by mělo být možné ho spustit.
     *
     * Pokud se váme skript nechce překládat/nebo se vám to nepodaří. Můžete
     * vygenerovaný soubor s výsledky zkopírovat a
     * spolu s přiloženým skriptem spustit (přeložit) na
     * Linuxu např. pomocí vašeho účtu na serveru ares.fav.zcu.cz
     *
     * Metodu není třeba měnit kromě řádků označených T O D O - tj. vytvoření
     * objektu třídy {@link Index} a
     */
    public static void main(String args[]) throws IOException {

        final Factory factory = new DefaultFactory();
        final var preprocessor = factory.createPreprocessor(PreprocessorType.LUCENE);
        final var index = factory.createIndex(preprocessor);

        Collection<Topic> topics = SerializedDataHelper.loadData(new File(OUTPUT_DIR
                + "/topicData.bin"), Topic.class);

        File serializedData = new File(OUTPUT_DIR + "/czechData.bin");

        Collection<Indexable> documents = new ArrayList<>();
        log.info("load");
        try {
            if (serializedData.exists()) {
                documents = SerializedDataHelper.loadData(serializedData, Indexable.class);
            } else {
                log.error("Cannot find " + serializedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Documents: " + documents.size());
        index.index(documents.stream().toList());
        List<String> lines = new ArrayList<String>();

        for (Topic t : topics) {
            // TODO vytvoření dotazu, třída Topic představuje dotaz pro vyhledávání v
            // zaindexovaných dokumentech
            // a obsahuje tři textová pole title, description a narrative. To jak
            // sestavíte
            // dotaz je na Vás a pravděpodobně
            // to ovlivní výsledné vyhledávání - zkuste změnit a uvidíte jaký MAP (Mean
            // Average Precision) dostanete pro jednotlivé
            // kombinace např. pokud budete vyhledávat jen pomocí title (t.getTitle())
            // nebo

            // jen pomocí description (t.getDescription())
            // nebo jejich kombinací (t.getTitle() + " " + t.getDescription())
            List<QueryResult> resultHits = index.search(t.getTitle() + " " + t.getDescription());

            Comparator<QueryResult> cmp = new Comparator<QueryResult>() {
                public int compare(QueryResult o1, QueryResult o2) {
                    if (o1.getScore() > o2.getScore())
                        return -1;
                    if (o1.getScore() == o2.getScore())
                        return 0;
                    return 1;
                }
            };

            Collections.sort(resultHits, cmp);
            for (QueryResult r : resultHits) {
                final String line = r.toString(t.getId());
                lines.add(line);
            }
            if (resultHits.size() == 0) {
                lines.add(t.getId() + " Q0 " + "abc" + " " + "99" + " " + 0.0 + " runindex1");
            }
        }
        final File outputFile = new File(
                OUTPUT_DIR + "/results-" +
                        SerializedDataHelper.SDF.format(System.currentTimeMillis()) + ".txt");
        FileUtils.saveFile(outputFile, lines);
        // final File outputFile = new File(OUTPUT_DIR +
        // "/results-2023-05-08_10_36_193.txt");
        // try to run evaluation
        try {
            runTrecEval(outputFile.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String runTrecEval(String predictedFile) throws IOException {

        String commandLine = "./trec_eval.8.1/./trec_eval" +
                " ./trec_eval.8.1/czech" +
                " " + predictedFile;

        System.out.println(commandLine);
        Process process = Runtime.getRuntime().exec(commandLine);

        BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String trecEvalOutput;
        StringBuilder output = new StringBuilder("TREC EVAL output:\n");
        for (String line; (line = stdout.readLine()) != null;)
            output.append(line).append("\n");
        trecEvalOutput = output.toString();
        System.out.println(trecEvalOutput);

        int exitStatus = 0;
        try {
            exitStatus = process.waitFor();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        System.out.println(exitStatus);

        stdout.close();
        stderr.close();

        return trecEvalOutput;
    }
}
