import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class SecondExTest {
    private static CustomIndex index;

    @BeforeClass
    public static void config() {
        index = new CustomIndex(new RAMDirectory(), new StandardAnalyzer(), FieldName.SOME_FIELD);

        index.indexDocument("to be or not to be that is the question");
        index.indexDocument("make a long story short");
        index.indexDocument("see eye to eye");
    }

    @Test
    public void test1() {
        List<Document> documentList = index.searchPhraseQuery("to be not", 1);
        Assert.assertEquals(1, documentList.size());
    }

    @Test
    public void test2() {
        List<Document> documentList = index.searchPhraseQuery("to or to", 1);
        Assert.assertEquals(0, documentList.size());
    }

    @Test
    public void test3() {
        List<Document> documentList = index.searchPhraseQuery("to", 1);
        Assert.assertEquals(2, documentList.size());
    }

    @Test
    public void test4() {
        List<Document> documentList = index.searchPhraseQuery("long story short", 0);
        Assert.assertEquals(1, documentList.size());
    }

    @Test
    public void test5() {
        List<Document> documentList = index.searchPhraseQuery("long short", 0);
        Assert.assertEquals(0, documentList.size());
    }

    @Test
    public void test6() {
        List<Document> documentList = index.searchPhraseQuery("long short", 1);
        Assert.assertEquals(1, documentList.size());
    }

    @Test
    public void test7() {
        List<Document> documentList = index.searchPhrasePrefQuery("story long", 1);
        Assert.assertEquals(0, documentList.size());
    }

    @Test
    public void test8() {
        List<Document> documentList = index.searchPhrasePrefQuery("story sho", 2);
        Assert.assertEquals(1, documentList.size());
    }

    @Test
    public void test9() {
        List<Document> documentList = index.searchPhrasePrefQuery("short sto", 2);
        Assert.assertEquals(0, documentList.size());
    }
}
