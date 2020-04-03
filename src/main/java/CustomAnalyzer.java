import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;

public class CustomAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        WhitespaceTokenizer src = new WhitespaceTokenizer();
        TokenStream result = new LowerCaseFilter(src);
        result = new StopFilter(result,  StopFilter.makeStopSet("a", "an", "for", "to"));
        result = new CustomTokenFilter(result);

        return new TokenStreamComponents(src, result);
    }
}
