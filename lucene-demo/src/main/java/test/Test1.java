package test;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class Test1 {
    String[] a = {
            "3, 华为 - 华为电脑, 爆款",
            "4, 华为手机, 旗舰",
            "5, 联想 - Thinkpad, 商务本",
            "6, 联想手机, 自拍神器"
    };

    @Test
    public void test1() throws IOException {
        //用 lucene 对四篇文档生成索引

        //准备文件夹 d:/test/lucene/abc
        FSDirectory fsDirectory = FSDirectory.open(new File("d:/test/lucene/abc").toPath());
        //配置中文分词工具
        IndexWriterConfig config = new IndexWriterConfig(new SmartChineseAnalyzer());
        //创建索引的输出工具
        IndexWriter writer = new IndexWriter(fsDirectory, config);
        //遍历文档输出索引
        for (String s :
                a) {
            //     id    title      sellPoint
            //s - "3, 华为 - 华为电脑, 爆款"
            String[] arr=s.split(",");
            //文档的三个数据，封装到Document对象
            Document doc = new Document();
            doc.add(new LongPoint("id", Long.parseLong(arr[0])));
            //作为摘要存储
            doc.add(new StoredField("id", Long.parseLong(arr[0])));
            doc.add(new TextField("title", arr[1], Field.Store.YES));
            doc.add(new TextField("sellPoint", arr[2], Field.Store.YES));

            writer.addDocument(doc);
        }
        writer.flush();
        writer.close();
    }

    @Test
    public void test2() throws IOException {
        //文件夹
        FSDirectory fsDirectory = FSDirectory.open(new File("d:/test/lucene/abc").toPath());
        //读取器工具
        DirectoryReader reader = DirectoryReader.open(fsDirectory);
        //创建搜索器工具
        IndexSearcher searcher = new IndexSearcher(reader);
        //封装搜索的关键词
        TermQuery query = new TermQuery(new Term("title", "华为"));
        //用搜索器执行搜索，并得到结果：
        TopDocs topDocs = searcher.search(query, 20);
        // 排序的文档列表[{index:0,score:0.6},{index:2,score:0.4},{index:1,score:0.1}]
        for (ScoreDoc sd :
                topDocs.scoreDocs) {
            int i = sd.doc;
            float score = sd.score;
            Document doc = searcher.doc(i);
            System.out.println(i);
            System.out.println(score);
            System.out.println(doc.get("id"));
            System.out.println(doc.get("title"));
            System.out.println(doc.get("sellPoint"));
            System.out.println("-----------------------------");
        }
    }

}
