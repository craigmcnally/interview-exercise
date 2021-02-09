package sandbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KeywordFinder {

  public static final Logger logger = LogManager.getLogger(KeywordFinder.class);
  public final List<String> keywords;

  public KeywordFinder(List<String> keywords) {
    this.keywords = keywords;
  }

  public List<String> hasKeyword(String text) {
    List<String> ret = new ArrayList<String>(keywords.size());

    if (keywords != null && text != null) {
      for (String kw : keywords) {
        if (text.contains(kw)) {
          ret.add(kw);
        }
      }
    }

    return ret;
  }

  public Future<List<String>> hasKeywordAsync(String text) {
    if (keywords == null || text == null) {
      return CompletableFuture.completedFuture(null);
    }

    return CompletableFuture.supplyAsync(() -> {
      List<String> ret = new ArrayList<String>(keywords.size());

      keywords.stream().forEach(kw -> {
        if (text.contains(kw)) {
          ret.add(kw);
        }
      });
      return ret;
    });
  }

  public CompletableFuture<List<String>> hasKeywordAsyncV2(String text) {
    return CompletableFuture.supplyAsync(() -> keywords.stream()
        .filter(text::contains)
        .collect(Collectors.toList()));
  }

  public CompletableFuture<List<String>> hasKeywordAsyncV3(String text) {
    if (keywords == null || text == null) {
      return CompletableFuture.completedFuture(null);
    }

    return CompletableFuture
        .supplyAsync(() -> keywords.stream().filter(text::contains).collect(Collectors.toList()));
  }

  public static void main(String[] args) throws Exception {
    KeywordFinder finder = new KeywordFinder(Arrays.asList(new String[] {"dog", "cat", "Fox"}));

    String text = "The quick fox jumps over the lazy brown dog.";
    finder.hasKeywordAsyncV2(text).thenAccept(found -> {
      if (found != null) {
        logger.info("found " + found.size() + " keywords in the text: '" + text + "'");
      }
    });

    String text2 = "Fox. Socks. Box. Knox. Knox in box. Fox in socks. Knox on fox in socks in box.";
    finder.hasKeywordAsyncV2(text2).thenAccept(found -> {
      if (found != null) {
        logger.info("found " + found.size() + " keywords in the text: '" + text2 + "'");
      }
    });

    new KeywordFinder(null).hasKeywordAsyncV3(text).get();
  }
}
