import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;

public class CustomTokenFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);

    private char separator = ' ';
    private boolean done;

    private StringBuilder buf = new StringBuilder();

    protected CustomTokenFilter(TokenStream input) {
        super(input);
    }

    public void setTokenSeparator(char separator) {
        this.separator = separator;
    }

    @Override
    public void reset() throws IOException {
        input.reset();
        done = false;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (done) {
            return false;
        }
        done = true;

        buf.setLength(0);
        boolean firstTerm = true;
        while (input.incrementToken()) {
            if (!firstTerm) {
                buf.append(separator);
            }

            buf.append(termAtt);
            firstTerm = false;
        }

        termAtt.setEmpty().append(buf);
        posIncrAtt.setPositionIncrement(1);

        return true;
    }
}