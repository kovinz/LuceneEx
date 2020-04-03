import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomIndex {
    private Directory memoryIndex;
    private Analyzer analyzer;
    private String fieldName;

    public CustomIndex(Directory memoryIndex, Analyzer analyzer, String fieldName) {
        this.memoryIndex = memoryIndex;
        this.analyzer = analyzer;
        this.fieldName = fieldName;
    }

    public void indexDocument(String dataStr) {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        try {
            IndexWriter writer = new IndexWriter(memoryIndex, indexWriterConfig);
            Document document = new Document();
            FieldType type = new FieldType();
            type.setStoreTermVectors(true);
            type.setStoreTermVectorPositions(true);
            type.setStoreTermVectorOffsets(true);
            type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
            Field fieldStore = new Field(fieldName, dataStr, type);

            document.add(fieldStore);

            writer.addDocument(document);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Document> searchTermQuery(String queryStr) {
        try {
            Term term = new Term(fieldName, queryStr);
            Query query = new TermQuery(term);
            IndexReader indexReader = DirectoryReader.open(memoryIndex);
            IndexSearcher searcher = new IndexSearcher(indexReader);

            TopDocs topDocs = searcher.search(query, 10);

            List<Document> documents = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                printTerms(indexReader, scoreDoc);
                documents.add(searcher.doc(scoreDoc.doc));
            }

            return documents;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Document> searchPhraseQuery(String queryStr, int slop) {
        try {
            Query query = new PhraseQuery(slop, fieldName, queryStr.split(" "));
            IndexReader indexReader = DirectoryReader.open(memoryIndex);
            IndexSearcher searcher = new IndexSearcher(indexReader);

            TopDocs topDocs = searcher.search(query, 10);

            List<Document> documents = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                documents.add(searcher.doc(scoreDoc.doc));
            }

            return documents;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * For phrase search with the ability to use only prefix of the last word and without reordering
     * @param queryStr
     * @param slop
     * @return
     */
    public List<Document> searchPhrasePrefQuery(String queryStr, int slop) {
        try {
            IndexReader indexReader = DirectoryReader.open(memoryIndex);
            IndexSearcher searcher = new IndexSearcher(indexReader);

            String[] phraseWords = queryStr.split(" ");
            SpanQuery[] queryParts = new SpanQuery[phraseWords.length];
            for (int i = 0; i < phraseWords.length - 1; i++) {
                WildcardQuery wildQuery = new WildcardQuery(new Term(fieldName, phraseWords[i]));
                queryParts[i] = new SpanMultiTermQueryWrapper<>(wildQuery);
            }

            WildcardQuery wildQuery = new WildcardQuery(new Term(fieldName, phraseWords[phraseWords.length - 1] + "*"));
            queryParts[phraseWords.length - 1] = new SpanMultiTermQueryWrapper<>(wildQuery);

            Query query = new SpanNearQuery(queryParts, slop, true);

            TopDocs topDocs = searcher.search(query, 10);

            List<Document> documents = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                documents.add(searcher.doc(scoreDoc.doc));
            }

            return documents;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public List<Document> searchPathWithRegexQuery(String queryStr) {
        try {
            StringBuilder builder = new StringBuilder();
            // (d|.*\/d|.*\.d)(.*\/o|.*\.o|o)(.*\/c|.*\.c|c)(.*\/s|.*\.s|s).*
            for (char aChar : queryStr.toCharArray()) {
                if (aChar != '/') {
                    builder.append(String.format("(%c|.*/%c|.*\\.%c)", aChar, aChar, aChar));
                } else {
                    builder.append(".*/");
                }
            }
            builder.append(".*");

            Term term = new Term(fieldName, builder.toString());
            Query query = new RegexpQuery(term);

            IndexReader indexReader = DirectoryReader.open(memoryIndex);
            IndexSearcher searcher = new IndexSearcher(indexReader);

            TopDocs topDocs = searcher.search(query, 10);

            List<Document> documents = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                printTerms(indexReader, scoreDoc);
                documents.add(searcher.doc(scoreDoc.doc));
            }

            return documents;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Document> getAllDocs() {
        try {
            Query query = new MatchAllDocsQuery();
            IndexReader indexReader = DirectoryReader.open(memoryIndex);
            IndexSearcher searcher = new IndexSearcher(indexReader);

            TopDocs topDocs = searcher.search(query, 10);

            List<Document> documents = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                printTerms(indexReader, scoreDoc);
                documents.add(searcher.doc(scoreDoc.doc));
            }

            return documents;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void printTerms(IndexReader indexReader, ScoreDoc scoreDoc) throws IOException{
        Fields termVs = indexReader.getTermVectors(scoreDoc.doc);
        Terms f = termVs.terms(fieldName);
        TermsEnum te = f.iterator();
        PostingsEnum docsAndPosEnum = null;
        BytesRef bytesRef;
        while ( (bytesRef = te.next()) != null ) {
            docsAndPosEnum = te.postings(docsAndPosEnum, PostingsEnum.ALL);

            int nextDoc = docsAndPosEnum.nextDoc();
            assert nextDoc != DocIdSetIterator.NO_MORE_DOCS;
            final int fr = docsAndPosEnum.freq();
            final int p = docsAndPosEnum.nextPosition();
            final int o = docsAndPosEnum.startOffset();
            System.out.println("p=" + p + ", o=" + o + ", l=" + bytesRef.length + ", f=" + fr + ", s=" + bytesRef.utf8ToString());
        }
    }
}
