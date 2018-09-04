package lucene_demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import com.hankcs.lucene.HanLPAnalyzer;

public class Demo1 {

	@Test
	public void test01() throws IOException {
		//指定索引库的位置
		String indexdirectory = "E:\\temp\\index";
		//打开目录
		Directory directory = FSDirectory.open(Paths.get(indexdirectory));
		
		//创建分词器对象
		Analyzer analyzer = new HanLPAnalyzer();
		//StandardAnalyzer analyzer = new StandardAnalyzer();
		//创建索引配置信息对象
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		//创建索引写对象
		IndexWriter indexWriter = new IndexWriter(directory,config);
		
		//执行数据源位置
		File dir = new File("E:\\temp\\source");
		//获得数据源数据
		for (File f : dir.listFiles()) {
		//文件名
		String fileName = f.getName();
		//文件内容
		String fileContent = FileUtils.readFileToString(f);
		//文件路径
		String filePath = f.getPath();
		//文件的大小
		long fileSize = FileUtils.sizeOf(f);
		//第一个参数：域的名称
		//第二个参数：域的内容
		//第三个参数：是否存储
		Field fileNameField = new TextField("filename", fileName, Field.Store.YES);
		//文件内容域
		Field fileContentField = new TextField("content", fileContent,
		Field.Store.YES);
		
		Field fileSizeField = new TextField("size", String.valueOf(fileSize),
				Field.Store.YES);
		/*Field fileSizeField =new NumericDocValuesField("size",fileSize);*/
		//文件路径域（不分析、不索引、只存储）
		Field filePathField = new StoredField("path", filePath);
		//文件大小域
		/*Field fileSizeField = new StoredField("size", fileSize);*/
	
		//创建document对象
		Document document = new Document();
		document.add(fileNameField);
		document.add(fileContentField);
		document.add(filePathField);
		document.add(fileSizeField);
		//创建索引，并写入索引库
		indexWriter.addDocument(document);
		}
		//关闭indexwriter
		indexWriter.close();
		
	}
	
	
	
	
	@Test
	public void testQueryIndex() throws Exception {
	//指定索引库存放的路径
	String indexdirectory = "E:\\temp\\index";
	Directory directory = FSDirectory.open(Paths.get(indexdirectory));
	//创建indexReader对象
	IndexReader indexReader = DirectoryReader.open(directory);
	//创建indexsearcher对象
	IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	//创建查询
	Query query = new TermQuery(new Term("filename", "Apache"));
	//执行查询
	//第一个参数是查询对象，第二个参数是查询结果返回的最大值
	TopDocs topDocs = indexSearcher.search(query, 10);
	//查询结果的总条数
	System.out.println("查询结果的总条数："+ topDocs.totalHits);
	//遍历查询结果
	//topDocs.scoreDocs存储了document对象的id
	for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
	//scoreDoc.doc属性就是document对象的id
	//根据document的id找到document对象
	Document document = indexSearcher.doc(scoreDoc.doc);
	System.out.println(document.get("filename"));
	System.out.println(document.get("content"));
	System.out.println(document.get("path"));
	System.out.println(document.get("size"));
	}
	//关闭indexreader对象
	indexReader.close();
	}
	
	
	@Test
	public void testDeleteIndex() throws Exception {
	//指定索引库存放的路径
	String indexdirectory = "E:\\temp\\index";
	//打开目录
	Directory directory = FSDirectory.open(Paths.get(indexdirectory));
	//索引
	Analyzer analyzer = new HanLPAnalyzer();
	IndexWriterConfig config = new IndexWriterConfig(analyzer);
	IndexWriter indexWriter = new IndexWriter(directory,config);
	//删除全部索引
	indexWriter.deleteAll();
	//关闭indexwriter
	indexWriter.close();
	}
	
	
	
	@Test
	public void testMatchAllDocsQuery() throws Exception {
	//指定索引库存放的路径
	String indexdirectory = "E:\\temp\\index";
	Directory directory = FSDirectory.open(Paths.get(indexdirectory));
	//创建indexReader对象
	IndexReader indexReader = DirectoryReader.open(directory);
	//创建indexsearcher对象
	IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	//创建查询条件
	Query query = new MatchAllDocsQuery();
	//执行查询
	//查询索引库
	TopDocs topDocs = indexSearcher.search(query, 100);
	ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	System.out.println("查询结果总记录数：" + topDocs.totalHits);
	//遍历查询结果
	for (ScoreDoc scoreDoc : scoreDocs) {
	int docId = scoreDoc.doc;
	//通过id查询文档对象
	Document document = indexSearcher.doc(docId);
	//取属性
	System.out.println(document.get("filename"));
	System.out.println(document.get("size"));
	System.out.println(document.get("content"));
	System.out.println(document.get("path"));
	}
	//关闭索引库
	indexSearcher.getIndexReader().close();
	}
	
	@Test
	public void testTermQuery() throws Exception {
	//指定索引库存放的路径
	String indexdirectory = "E:\\temp\\index";
	Directory directory = FSDirectory.open(Paths.get(indexdirectory));
	//创建indexReader对象
	IndexReader indexReader = DirectoryReader.open(directory);
	//创建indexsearcher对象
	IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	//创建查询对象
	Query query = new TermQuery(new Term("content", "lucene"));
	//执行查询
	TopDocs topDocs = indexSearcher.search(query, 10);
	//共查询到的document个数
	System.out.println("查询结果总数量：" + topDocs.totalHits);
	//遍历查询结果
	for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
	Document document = indexSearcher.doc(scoreDoc.doc);
	System.out.println(document.get("filename"));
	System.out.println(document.get("path"));
	System.out.println(document.get("size"));
	}
	//关闭indexreader
	indexSearcher.getIndexReader().close();
	
}
	
	
	//数值范围查询
	@Test
	public void testNumericRangeQuery() throws Exception {
	//指定索引库存放的路径
	String indexdirectory = "E:\\temp\\index";
	Directory directory = FSDirectory.open(Paths.get(indexdirectory));
	//创建indexReader对象
	IndexReader indexReader = DirectoryReader.open(directory);
	//创建indexsearcher对象
	IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	//创建查询
	Query query =LongPoint.newRangeQuery("size", 1l,500l);
	//执行查询
	TopDocs topDocs = indexSearcher.search(query, 100);
	ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	System.out.println("查询结果总记录数：" + topDocs.totalHits);
	//遍历查询结果
	for (ScoreDoc scoreDoc : scoreDocs) {
	int docId = scoreDoc.doc;
	//通过id查询文档对象
	Document document = indexSearcher.doc(docId);
	//取属性
	System.out.println(document.get("filename"));
	System.out.println(document.get("path"));
	System.out.println(document.get("size"));
	}
	//关闭索引库
	indexSearcher.getIndexReader().close();
	}
	
	
	@Test
	public void testBooleanQuery() throws Exception {
	//指定索引库存放的路径
	String indexdirectory = "E:\\temp\\index";
	Directory directory = FSDirectory.open(Paths.get(indexdirectory));
	//创建indexReader对象
	IndexReader indexReader = DirectoryReader.open(directory);
	//创建indexsearcher对象
	IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	//创建boolean查询
	Query query1 = new TermQuery(new Term("filename", "mybatis"));
	Query query2 = new TermQuery(new Term("content", "xxx"));
	BooleanClause bc1 = new BooleanClause(query1, BooleanClause.Occur.MUST);
	BooleanClause bc2 = new BooleanClause(query2, BooleanClause.Occur.MUST_NOT);
	BooleanQuery boolQuery = new BooleanQuery.Builder().add(bc1).add(bc2).build();
	// 返回前10条
	TopDocs topDocs = indexSearcher.search(boolQuery, 10);
	//执行查询
	ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	System.out.println("查询结果总记录数：" + topDocs.totalHits);
	//遍历查询结果
	for (ScoreDoc scoreDoc : scoreDocs) {
	int docId = scoreDoc.doc;
	//通过id查询文档对象
	Document document = indexSearcher.doc(docId);
	//取属性
	System.out.println(document.get("filename"));
	System.out.println(document.get("path"));
	System.out.println(document.get("size"));
	}
	//关闭索引库
	indexSearcher.getIndexReader().close();
	}
	
	@Test
	public void testQueryParser() throws Exception {
	//指定索引库存放的路径
	String indexdirectory = "E:\\temp\\index";
	Directory directory = FSDirectory.open(Paths.get(indexdirectory));
	//创建indexReader对象
	IndexReader indexReader = DirectoryReader.open(directory);
	//创建indexsearcher对象
	IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	//创建queryparser对象
	//第一个参数默认搜索的域
	//第二个参数就是分析器对象
	QueryParser queryParser = new QueryParser("content", new HanLPAnalyzer());
	Query query = queryParser.parse("Lucene是java开发的");
	//执行查询
	//查询索引库
	TopDocs topDocs = indexSearcher.search(query, 100);
	ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	System.out.println("查询结果总记录数：" + topDocs.totalHits);
	//遍历查询结果
	for (ScoreDoc scoreDoc : scoreDocs) {
	int docId = scoreDoc.doc;
	//通过id查询文档对象
	Document document = indexSearcher.doc(docId);
	//取属性
	System.out.println(document.get("filename"));
	System.out.println(document.get("path"));
	System.out.println(document.get("size"));
	}
	//关闭索引库
	indexSearcher.getIndexReader().close();
	}
	
	@Test
	public void testMultiFiledQueryParser() throws Exception {
	//指定索引库存放的路径
	String indexdirectory = "E:\\temp\\index";
	Directory directory = FSDirectory.open(Paths.get(indexdirectory));
	//创建indexReader对象
	IndexReader indexReader = DirectoryReader.open(directory);
	//创建indexsearcher对象
	IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	//可以指定默认搜索的域是多个
	String[] fields = {"filename", "content"};
	//创建一个MulitFiledQueryParser对象
	MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, new
	HanLPAnalyzer());
	Query query = queryParser.parse("java and apache");
	System.out.println(query);
	//执行查询
	//查询索引库
	TopDocs topDocs = indexSearcher.search(query, 100);
	ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	System.out.println("查询结果总记录数：" + topDocs.totalHits);
	//遍历查询结果
	for (ScoreDoc scoreDoc : scoreDocs) {
		int docId = scoreDoc.doc;
		//通过id查询文档对象
		Document document = indexSearcher.doc(docId);
		//取属性
		System.out.println(document.get("filename"));
		System.out.println(document.get("path"));
		System.out.println(document.get("size"));
		}
		//关闭索引库
		indexSearcher.getIndexReader().close();
	}
}
