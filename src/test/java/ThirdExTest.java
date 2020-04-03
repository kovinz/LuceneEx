import org.apache.lucene.document.Document;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class ThirdExTest {
    private static CustomIndex index;

    @BeforeClass
    public static void config() {
        index = new CustomIndex(new RAMDirectory(), new CustomAnalyzer(), FieldName.SOME_FIELD);

        index.indexDocument("to Be or not to be that is the question");
        index.indexDocument("make a long story short");
        index.indexDocument("see eye to eye");
        index.indexDocument("bla");
        index.indexDocument("bla to");
    }

    @Test
    public void test1() {
        List<Document> documentList = index.searchPhraseQuery("to be not", 1);
        Assert.assertEquals(0, documentList.size());
    }

    @Test
    public void test2() {
        List<Document> documentList = index.searchTermQuery("see eye to eye");
        Assert.assertEquals(0, documentList.size());
    }

    @Test
    public void test3() {
        List<Document> documentList = index.searchTermQuery("make");
        Assert.assertEquals(0, documentList.size());
    }

    @Test
    public void test4() {
        List<Document> documentList = index.searchTermQuery("bla");
        Assert.assertEquals(2, documentList.size());
    }

    @Test
    public void test5() {
        List<Document> documentList = index.getAllDocs();
        Assert.assertEquals(5, documentList.size());
    }

    @Test
    public void test6() {
        List<Document> documentList = index.searchTermQuery("be or not be that is the question");
        Assert.assertEquals(1, documentList.size());
    }

    @Test
    public void test7() {
        List<Document> documentList = index.searchTermQuery("make long story short");
        Assert.assertEquals(1, documentList.size());
    }
}
