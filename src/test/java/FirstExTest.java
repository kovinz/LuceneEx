import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class FirstExTest {
    private static CustomIndex index;

    @BeforeClass
    public static void config() {
        index = new CustomIndex(new RAMDirectory(), new WhitespaceAnalyzer(), FieldName.PATH);

        index.indexDocument("lucene/queryparser/docs/xml/img/plus.gif");
        index.indexDocument("lucene/queryparser/docs/xml/img/join.gif");
        index.indexDocument("lucene/queryparser/docs/xml/img/minusbottom.gif");
    }

    @Test
    public void test1() {
        List<Document> documentList = index.searchPathWithRegexQuery("lqdocspg");
        Assert.assertEquals(1, documentList.size());
    }

    @Test
    public void test2() {
        List<Document> documentList = index.searchPathWithRegexQuery("minusbot.gif");
        Assert.assertEquals(1, documentList.size());
    }

    @Test
    public void test3() {
        List<Document> documentList = index.searchPathWithRegexQuery("lqd///gif");
        Assert.assertEquals(3, documentList.size());
    }
}
