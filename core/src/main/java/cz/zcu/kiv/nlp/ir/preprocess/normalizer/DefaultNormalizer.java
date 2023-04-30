package cz.zcu.kiv.nlp.ir.preprocess.normalizer;

public class DefaultNormalizer implements Normalizer {

  @Override
  public String removeAccents(final String text) {
    return text == null ? null
        : java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
  }

}
