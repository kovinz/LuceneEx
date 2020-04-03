import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

public class Main {
    public static void main(String[] args) throws Exception{
/*        Directory directory = new RAMDirectory();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(directory, indexWriterConfig);

        Document doc = new Document();
        // Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.YES
        FieldType type = new FieldType();
        type.setStoreTermVectors(true);
        type.setStoreTermVectorPositions(true);
        type.setStoreTermVectorOffsets(true);
        type.setIndexOptions(IndexOptions.DOCS);

        Field fieldStore = new Field("tags", "foo bar and then some", type);
        doc.add(fieldStore);
        writer.addDocument(doc);

        doc = new Document();
        fieldStore = new Field("tags", "foo bar and then some changing staff staff or maybe", type);
        doc.add(fieldStore);
        writer.addDocument(doc);
        writer.close();

        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        Term t = new Term("tags", "bar");
        Query q = new TermQuery(t);
        TopDocs results = searcher.search(q, 2);

        for ( ScoreDoc scoreDoc: results.scoreDocs ) {
            Fields termVs = reader.getTermVectors(scoreDoc.doc);
            Terms f = termVs.terms("tags");
            TermsEnum te = f.iterator();
            PostingsEnum docsAndPosEnum = null;
            BytesRef bytesRef;
            while ( (bytesRef = te.next()) != null ) {
                docsAndPosEnum = te.postings(docsAndPosEnum, PostingsEnum.ALL);
                // for each term (iterator next) in this field (field)
                // iterate over the docs (should only be one)
                int nextDoc = docsAndPosEnum.nextDoc();
                assert nextDoc != DocIdSetIterator.NO_MORE_DOCS;
                final int fr = docsAndPosEnum.freq();
                final int p = docsAndPosEnum.nextPosition();
                final int o = docsAndPosEnum.startOffset();
                System.out.println("p="+ p + ", o=" + o + ", l=" + bytesRef.length + ", f=" + fr + ", s=" + bytesRef.utf8ToString());
            }
        }*/
    }
}
