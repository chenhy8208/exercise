package com.hongru.helloworld;

import com.hongru.domain.Article;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by leochen on 16/9/26.
 */
public class HelloWorld {

    private static final String name;
    static{
        name = "UnKnown";
    }

//    public HelloWorld(String name) {
//        this.name = name;
//    }

    public HelloWorld(){

    }

    @Test
    public void testCreateIndex() {
        //建内容
        Article article = new Article();
        article.setId(6);
        article.setTitle("今天天气不错啊2");
        article.setContent("今天天气不错啊3,要不要出去逛逛呢???");

        Document document = new Document();
        document.add(new Field("id", NumericUtils.intToPrefixCoded(article.getId()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("title", article.getTitle(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("content", article.getContent(), Field.Store.YES, Field.Index.ANALYZED));

        document.setBoost(2.0f);

        Directory directory = null;
        try {
            directory = FSDirectory.open(new File("./indexDir/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Analyzer analyzer = new IKAnalyzer();

        IndexWriter indexWriter = null;
        try {
            indexWriter = new IndexWriter(directory, analyzer, new IndexWriter.MaxFieldLength(10000));
            indexWriter.addDocument(document);
            indexWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testUpdateIndex() {
        IndexWriter indexWriter = null;
        try {
            Directory directory = FSDirectory.open(Paths.get("./indexDir/").toFile());
            Directory directory2 = new RAMDirectory();

            //Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
            Analyzer analyzer = new IKAnalyzer();

            indexWriter = new IndexWriter(directory, analyzer, IndexWriter.MaxFieldLength.UNLIMITED);

            Document doc = new Document();
            doc.add(new Field("id", "1", Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field("title", "今天天气不好啊", Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("content", "是嘛", Field.Store.YES, Field.Index.ANALYZED));

            indexWriter.updateDocument(new Term("id", "1"), doc);

            indexWriter.optimize();
            indexWriter.setMergeFactor(10);  //10个碎片以后就自动合并

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

//            Runtime.getRuntime().addShutdownHook(new Thread(){
//                @Override
//                public void run() {
//                   indexWriter.close();
//                }
//            });

            if (indexWriter != null)
            {
                try {
                    indexWriter.close();
                } catch (IOException e) {
                    new RuntimeException(e);
                }
            }
        }
    }

    @Test
    public void testDeleteIndex() {
        IndexWriter indexWriter = null;
        try {
            Directory directory = new SimpleFSDirectory(Paths.get("./indexDir/").toFile());
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

            indexWriter = new IndexWriter(directory, analyzer, IndexWriter.MaxFieldLength.UNLIMITED);
            indexWriter.deleteDocuments(new Term("id", "1"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (indexWriter != null)
            {
                try {
                    indexWriter.close();
                } catch (IOException e) {
                    new RuntimeException(e);
                }
            }
        }
    }

    @Test
    public void testSearch() {
        String queryString = "天气";
        String queryString2 = "逛逛";

        List<Article> list = new ArrayList<>();

        Directory directory = null;
        try {
            directory = FSDirectory.open(new File("./indexDir/"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DateTools.dateToString(new Date(), DateTools.Resolution.DAY);

        //Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
        Analyzer analyzer = new IKAnalyzer();

        QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_CURRENT, new String[]{"title","content"}, analyzer);
        PhraseQuery query = null;
        BooleanQuery bQuery = null;
//        try {
            //query = queryParser.parse(queryString);
            //query = new TermQuery(new Term("title", queryString));
            //query = new WildcardQuery(new Term("title", queryString));
            //query = new FuzzyQuery(new Term("title", "天真"), 0.8f);

            query = new PhraseQuery();
            query.add(new Term("title", "天气"));
            query.add(new Term("title", "不错"));
            query.setSlop(5);


            Query query2 = new TermQuery(new Term("title", queryString));

        bQuery = new BooleanQuery();
        bQuery.add(query, BooleanClause.Occur.MUST);
        bQuery.add(query2, BooleanClause.Occur.MUST);



//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        TopDocs topDocs = null;
        IndexSearcher indexSearch = null;
        try {
            indexSearch = new IndexSearcher(directory);
            //topDocs = indexSearch.search(query, 100);

            Sort sort = new Sort(new SortField("id", SortField.INT, true));
            Filter filter = null;
            filter = NumericRangeFilter.newIntRange("id", 1, 6, true, false);


            topDocs = indexSearch.search(bQuery, filter, 100, sort);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;

            Formatter formatter = new SimpleHTMLFormatter("<p>", "</p>");
            Scorer scorer = new QueryScorer(query);
            Highlighter highlighter = new Highlighter(formatter, scorer);
            Fragmenter frag = new SimpleFragmenter(15);
            highlighter.setTextFragmenter(frag);


            for (ScoreDoc scoreDoc: scoreDocs
                 ) {
                System.out.println("score = " + scoreDoc.score);

                int docId = scoreDoc.doc;
                Document doc = indexSearch.doc(docId);

                try {
                    String content = highlighter.getBestFragment(analyzer, "content", doc.get("content"));
                    if (content != null)
                    {
                        doc.getField("content").setValue(content);
                    }
                } catch (InvalidTokenOffsetsException e) {
                    e.printStackTrace();
                }

                Article article = new Article();
                article.setId(NumericUtils.prefixCodedToInt(doc.get("id")));
                article.setTitle(doc.get("title"));
                article.setContent(doc.get("content"));
                list.add(article);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (indexSearch != null)
            {
                try {
                    indexSearch.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //显示结果
        System.out.println("符合条件的结果数量:" + topDocs.totalHits);
        for (Article article: list
             ) {
            System.out.println("id = " + article.getId() + ", title = " + article.getTitle() + ", content = " + article.getContent());
        }

        HelloWorld helloWorld = new HelloWorld();
        System.out.println("helloWorld name = " + helloWorld.name);
    }
}
