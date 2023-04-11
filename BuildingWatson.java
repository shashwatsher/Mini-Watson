import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import org.apache.lucene.search.similarities.BM25Similarity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildingWatson {
	
	public static class quesColl {
		String answer;
		String category;
		String text;
	}
	
  public static void main(String[] args) throws IOException, ParseException {
	  
	 String collName = args[0];
	 String questionFile = args[1];
	 String similarity = args[2];
	 appr = args[3];
	 String perf = args[4];
	 String packageName = args[5];
	 
	 List<String> fileColl = new ArrayList<String>();
	 BufferedReader bR = null;
	 
	 // Analyzer for tokenizing text. The same analyzer should be used for indexing and searching
	 Analyzer analyzer = null;
	 
	 // WhiteSpace for only tokenization
	 if (appr.equals(naiveAppr) || appr.equals(lemmaAppr))
		 analyzer = new WhitespaceAnalyzer(Version.LUCENE_40);
	 
	 // Standard Analyzer for stemming with tokenization
	 if (appr.equals(stemAppr))
	     analyzer = new StandardAnalyzer(Version.LUCENE_40);
	 
	 // Using fs directory for disk storage
	 FSDirectory index = FSDirectory.open(new File(packageName));
	 if (!DirectoryReader.indexExists(index)) {
             System.out.println("Parsing the documents...Please Wait for 4 to 6 minutes....");
             System.out.println(" ");
	     IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
	     config.setOpenMode(OpenMode.CREATE);
	     IndexWriter w = new IndexWriter(index, config);
	     Files.walk(Paths.get(collName)).forEach(filePath -> {
		    if (Files.isRegularFile(filePath)) {
		    	fileColl.add(filePath.getFileName().toString());
		    }
	     });
	     int c = 0;
	     while(c < fileColl.size()){
		       bR = new BufferedReader(new FileReader(collName+"/" + fileColl.get(c)));
		       if (appr.equals(stemAppr) || appr.equals(lemmaAppr))
	               prepareIndex(w,bR);
		       else
		    	   prepareIndexNaive(w,bR);
	           c++;
	     }
         w.close();
         System.out.println("Parsing Finished !!!");
         System.out.println(" ");
	}

    // 3. search
    System.out.println("Commencing Search");
    System.out.println(" ");
    int hitsPerPage = 10;
    IndexReader reader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(reader);
    // For changing similarity (Using BM25 in place of tf-idf)
    if (similarity.equals(OkapiBM25))
    	searcher.setSimilarity(simnew);
    
    // For parsing question file
    File readQ = new File(questionFile);
    Scanner scannedQ = new Scanner(readQ, "UTF-8");
    List<quesColl> qC = parseQuestions(scannedQ);
    ScoreDoc[] hits = null;// = new ScoreDoc();
    String queryStr = "";
    Query q;
    int success = 0;
    float position = 1.0f;
    float total_pos = 0;
    
    ListIterator<quesColl> liq = qC.listIterator();
    
    
    /*************** Search based on text, category and year P@10**************/
    if (appr.equals(stemAppr) || appr.equals(lemmaAppr)){
    if (perf.equals(performanceatten)){
    while(liq.hasNext()){
    	int found = 0;
    	quesColl temp = liq.next();
    	
    	// For text
	    queryStr = temp.text + " " + temp.category;
    	q = new QueryParser(Version.LUCENE_40, "text", analyzer).parse(queryStr);
		if (similarity.equals(OkapiBM25))
		    searcher.setSimilarity(simnew);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		hits = collector.topDocs().scoreDocs;
		if (hits.length != 0){
		   	for(int i=0;i<hits.length;++i) {
	            int docId = hits[i].doc;
		   	    float score = hits[i].score;
		    	Document d = searcher.doc(docId);
		    	if (d.get("title").matches(temp.answer)){
		    		success++;
		    	    System.out.println(temp.text); 
		    	    found = 1;
		    	    position = i + 1;
		    	    System.out.println(temp.answer + " " + score + " "+position);
		    	    total_pos = total_pos + (1/position);
		    	    System.out.println(" ");
		    	    break;
		    	}
		   	}
		}
		if (found == 1)
			continue;
			
		// For text
	    queryStr = temp.text;
    	q = new QueryParser(Version.LUCENE_40, "text", analyzer).parse(queryStr);
		if (similarity.equals(OkapiBM25))
		    searcher.setSimilarity(simnew);
		collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		hits = collector.topDocs().scoreDocs;
		if (hits.length != 0){
		   	for(int i=0;i<hits.length;++i) {
	            int docId = hits[i].doc;
		   	    float score = hits[i].score;
		    	Document d = searcher.doc(docId);
		    	if (d.get("title").matches(temp.answer)){
		    		success++;
		    	    System.out.println(temp.text); 
		    	    found = 1;
		    	    position = i + 1;
		    	    System.out.println(temp.answer + " " + score + " "+position);
		    	    total_pos = total_pos + (1/position);
		    	    System.out.println(" ");
		    	    break;
		    	}
		   	}
		}
		if (found == 1)
			continue;
		
		// For Section-Titles
		queryStr = temp.text + " " + temp.category;
    	q = new QueryParser(Version.LUCENE_40, "subcat", analyzer).parse(queryStr);
		if (similarity.equals(OkapiBM25))
		    searcher.setSimilarity(simnew);
		collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		hits = collector.topDocs().scoreDocs;
		if (hits.length != 0){
		   	for(int i=0;i<hits.length;++i) {
	            int docId = hits[i].doc;
		   	    float score = hits[i].score;
		    	Document d = searcher.doc(docId);
		    	if (d.get("title").matches(temp.answer)){
		    		success++;
		    	    System.out.println(temp.text); 
		    	    found = 1;
		    	    position = i + 1;
		    	    System.out.println(temp.answer + " " + score + " "+position);
		    	    total_pos = total_pos + (1/position);
                    System.out.println(" ");
		    	    break;
		    	}
		   	}
		}
		if (found == 1)
			continue;
		
		// For Category
		queryStr = temp.text + " " + temp.category;
    	q = new QueryParser(Version.LUCENE_40, "cat", analyzer).parse(queryStr);
		if (similarity.equals(OkapiBM25))
		    searcher.setSimilarity(simnew);
		collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		hits = collector.topDocs().scoreDocs;
		if (hits.length != 0){
		   	for(int i=0;i<hits.length;++i) {
	            int docId = hits[i].doc;
		   	    float score = hits[i].score;
		    	Document d = searcher.doc(docId);
		    	if (d.get("title").matches(temp.answer)){
		    		success++;
		    	    System.out.println(temp.text); 
		    	    found = 1;
		    	    position = i + 1;
		    	    System.out.println(temp.answer + " " + score + " "+position);
		    	    total_pos = total_pos + (1/position);
		    	    System.out.println(" ");
		    	    break;
		    	}
		   	}
		}
		if (found == 1)
			continue;
		
		// For Year Category
		queryStr = temp.text + " " + temp.category;
		q = new QueryParser(Version.LUCENE_40, "year", analyzer).parse(queryStr);
		if (similarity.equals(OkapiBM25))
		    searcher.setSimilarity(simnew);
		collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		hits = collector.topDocs().scoreDocs;
		if (hits.length != 0){
		   	for(int i=0;i<hits.length;++i) {
		        int docId = hits[i].doc;
		    	float score = hits[i].score;
				Document d = searcher.doc(docId);
		    	if (d.get("title").matches(temp.answer)){
		    		success++;
				    System.out.println(temp.text); 
				    found = 1;
				    position = i + 1;
				    System.out.println(temp.answer + " " + score + " "+position);
				    total_pos = total_pos + (1/position);
				    System.out.println(" ");
				    break;
				 }
		 	}
		}
		if (found == 1)
			continue;
		
	    if (found == 0){
	    	System.out.println(temp.text); 
    	    System.out.println("No answer Found");
    	    System.out.println(" ");
    	    position = 0;
    	    continue;
	    }
    }    
    }
    }
    
   
    /***************Search based on text, category and year P@1 **************/
    if (appr.equals(stemAppr) || appr.equals(lemmaAppr)){
    if (perf.equals(performanceatone)){
    while(liq.hasNext()){
    	int found = 0;
    	quesColl temp = liq.next();
    	
    	// For text
    	queryStr = temp.text + " " + temp.category;
    	q = new QueryParser(Version.LUCENE_40, "text", analyzer).parse(queryStr);
		if (similarity.equals(OkapiBM25))
		    searcher.setSimilarity(simnew);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		hits = collector.topDocs().scoreDocs;
		if (hits.length != 0){
	        int docId = hits[0].doc;
		   	float score = hits[0].score;
		    Document d = searcher.doc(docId);
		    if (d.get("title").matches(temp.answer)){
		    	success++;
		        System.out.println(temp.text); 
		        found = 1;
		   	    System.out.println(temp.answer + " " + score + " "+position);
		   	    System.out.println(" ");
		    }
		}
		if (found == 1)
			continue;
		
		queryStr = temp.text;
    	q = new QueryParser(Version.LUCENE_40, "text", analyzer).parse(queryStr);
		if (similarity.equals(OkapiBM25))
		    searcher.setSimilarity(simnew);
		collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		hits = collector.topDocs().scoreDocs;
		if (hits.length != 0){
	        int docId = hits[0].doc;
		   	float score = hits[0].score;
		    Document d = searcher.doc(docId);
		    if (d.get("title").matches(temp.answer)){
		    	success++;
		        System.out.println(temp.text); 
		        found = 1;
		        System.out.println(temp.answer + " " + score + " "+position);
		        System.out.println(" ");
		    } 	
		}
	
		if (found == 1)
			continue;
		
		// For Section-Titles
		queryStr = temp.text;// + " " + temp.category;;
    	q = new QueryParser(Version.LUCENE_40, "subcat", analyzer).parse(queryStr);
		if (similarity.equals(OkapiBM25))
		    searcher.setSimilarity(simnew);
		collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		hits = collector.topDocs().scoreDocs;
		if (hits.length != 0){
	        int docId = hits[0].doc;
		   	float score = hits[0].score;
		    Document d = searcher.doc(docId);
		    if (d.get("title").matches(temp.answer)){
		    	success++;
		        System.out.println(temp.text);  
		        found = 1;
		        System.out.println(temp.answer + " " + score + " "+position);
		        System.out.println(" ");
		    } 	
		}
		if (found == 1)
			continue;
		
		// For Category
		queryStr = temp.text;// + " " + temp.category;;
    	q = new QueryParser(Version.LUCENE_40, "cat", analyzer).parse(queryStr);
		if (similarity.equals(OkapiBM25))
		    searcher.setSimilarity(simnew);
		collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		hits = collector.topDocs().scoreDocs;
		if (hits.length != 0){
	        int docId = hits[0].doc;
		   	float score = hits[0].score;
		    Document d = searcher.doc(docId);
		    if (d.get("title").matches(temp.answer)){
		    	success++;
		        System.out.println(temp.text); 
		        found = 1;
		        System.out.println(temp.answer + " " + score + " "+position);
		        System.out.println(" ");
		    } 	
		}
		if (found == 1)
			continue;
		
		// For Year Category
		queryStr = temp.text;// + " " + temp.category;;
    	q = new QueryParser(Version.LUCENE_40, "year", analyzer).parse(queryStr);
		if (similarity.equals(OkapiBM25))
		    searcher.setSimilarity(simnew);
		collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		hits = collector.topDocs().scoreDocs;
		if (hits.length != 0){
	        int docId = hits[0].doc;
		   	float score = hits[0].score;
		    Document d = searcher.doc(docId);
		    if (d.get("title").matches(temp.answer)){
		    	success++;
		        System.out.println(temp.text); 
		        found = 1;
		        System.out.println(temp.answer + " " + score + " "+position);
		        System.out.println(" ");
		    } 	
		}
		if (found == 1)
			continue;
		
	    if (found == 0){
	    	System.out.println(temp.text); 
    	    System.out.println("No answer Found");
    	    System.out.println(" ");
    	    continue;
	    }
    } 
    }
    }
    
    
    /***************** Search for Naive Approach P@10 **************/
    
    if (appr.equals(naiveAppr)){
    if (perf.equals(performanceatten)){
    while(liq.hasNext()){
    	int found = 0;
    	quesColl temp = liq.next();
	    queryStr = temp.text+ " " + temp.category;
    	q = new QueryParser(Version.LUCENE_40, "text", analyzer).parse(queryStr);
		if (similarity.equals(OkapiBM25))
		    searcher.setSimilarity(simnew);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		hits = collector.topDocs().scoreDocs;
		if (hits.length != 0){
		  	//int count = Math.min(10,hits.length);
		   	for(int i=0;i<hits.length;++i) {
	            int docId = hits[i].doc;
		   	    float score = hits[i].score;
		    	Document d = searcher.doc(docId);
		    	if (d.get("title").matches(temp.answer)){
		    		success++;
		    	    System.out.println(temp.text); 
		    	    found = 1;
		    	    position = i + 1;
		    	    total_pos = total_pos + (1/position);
		    	    System.out.println(temp.answer + " " + score + " "+position);
		    	    System.out.println(" ");
		    	    break;
		    	}
		   	}
		}
	    if (found == 0){
	    	System.out.println(temp.text); 
    	    System.out.println("No answer Found");
    	    System.out.println(" ");
    	    position = 0;
    	    continue;
	    }
    }    
    }
    }
    
    /*************** Search for Naive Approach P@1 ***************/
    
    if (appr.equals(naiveAppr)){
    if (perf.equals(performanceatone)){
    while(liq.hasNext()){
    	int found = 0;
    	quesColl temp = liq.next();
	    queryStr = temp.text+ " " + temp.category;
    	q = new QueryParser(Version.LUCENE_40, "text", analyzer).parse(queryStr);
		if (similarity.equals(OkapiBM25))
		    searcher.setSimilarity(simnew);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		hits = collector.topDocs().scoreDocs;
		if (hits.length != 0){
	        int docId = hits[0].doc;
		   	float score = hits[0].score;
		    Document d = searcher.doc(docId);
		    if (d.get("title").matches(temp.answer)){
		    	success++;
		        System.out.println(temp.text); 
		        found = 1;
		   	    System.out.println(temp.answer + " " + score + " "+position);
		   	    System.out.println(" ");
		    }
		}
	    if (found == 0){
	    	System.out.println(temp.text); 
    	    System.out.println("No answer Found");
    	    System.out.println(" ");
	    }
    }    
    }
    }
    
    float MRR = 0.0f;
    if(total_pos != 0){
        MRR = total_pos / 100 ;
        System.out.println("MRR : " + MRR);
    }
    System.out.println("Total number of answers found : " + success);
    
    // reader can only be closed when there
    // is no need to access the documents any more.
    reader.close();
  }

  private static List<quesColl> parseQuestions(Scanner scannedQ) {
      int lineCnt = 0;
      List<quesColl> qC = new ArrayList<quesColl>();
      quesColl temp = new quesColl();
      StanfordLemmatizerIf sl = null;
      if (appr.equals(lemmaAppr))
		try {
			sl = (StanfordLemmatizerIf) Class.forName("StanfordLemmatizer").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
	  while(scannedQ.hasNextLine()){
		  lineCnt++;
		  String scaQ = scannedQ.nextLine();
		  if (lineCnt % 4 == 0){
			  qC.add(temp);
			  temp = new quesColl();
		  }
		  // Obtaining the Query category
		  if (lineCnt % 4 == 1){
			  //http://stackoverflow.com/questions/25060835/ignore-special-characters-with-lucene
			  String normalized = Normalizer.normalize(scaQ, Form.NFD)
				        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
				        .replaceAll("[^A-Za-z0-9 ]+", "");
			  if (appr.equals(lemmaAppr)){
			      String result = sl.lemmatize(scaQ);
			      temp.category = result;
			  }
			  else
			      temp.category = normalized;
			  
		  }
		  // Obtaining the Query Clue
		  if (lineCnt % 4 == 2){
			  String normalized = Normalizer.normalize(scaQ, Form.NFD)
				        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
				        .replaceAll("[^A-Za-z0-9 ]+", ""); 
			  
			  if (appr.equals(lemmaAppr)){
			      String result = sl.lemmatize(scaQ);
			      temp.text = result;
			  }
			  else
			      temp.text = normalized;
		  }
		// Obtaining the Query Answer
		  if (lineCnt % 4 == 3){
			  temp.answer = scaQ;
		  }
	  }	    
	  return qC;
}
	  	  
  /**********************Parsing and Indexing for Improved Approach**************************/
  private static void prepareIndex(IndexWriter w, BufferedReader bR) throws IOException{
	  
	  String currentLine = null;
	  Pattern pTitle = Pattern.compile("^(\\[\\[)((?!Image|File))(.*?)(\\]\\])$");
	  String pMRT = "^(.*?)(may refer to:)$"; // For MRT:
	  Pattern pCat1 = Pattern.compile("^(CATEGORIES: )(.*?)$"); //For Categories
	  String pRed = "^(#REDIRECT|#redirect)(.*?)$"; // for redirect
	  Pattern ptpl = Pattern.compile("(\\[tpl\\])(.*?)(\\[/tpl\\])"); // For tpl
	  Pattern pIY = Pattern.compile("^(.*?)(\\d{4})(.*?)$");
	  Pattern pCat2 = Pattern.compile("^(======|=====|====|===|==)(.*?)(==|===|====|=====|======)$");
	  String pOr = "^(\\|| \\|)(.*?)";
	  int dontInsert = 1;
	  Matcher m;
	  String tempTitle = null;
	  String category1 = "";
	  String category2 = "";
	  String year = "";
	  String text = "";
	  
	  StanfordLemmatizerIf sl = null;

      
      if (appr.equals(lemmaAppr))
		try {
			sl = (StanfordLemmatizerIf) Class.forName("StanfordLemmatizer").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		while ((currentLine = bR.readLine()) != null) {

			    if (currentLine.isEmpty())
			    	continue;
			    m = pTitle.matcher(currentLine);
				if(m.find()){
					// Adding a new document
					if(dontInsert != 1){
						Document doc = new Document();
						doc.add(new StringField("title", tempTitle, Field.Store.YES));
			            // If lemmatization is used
						if (appr.equals(lemmaAppr)){
							String result = sl.lemmatize(category1);
							doc.add(new TextField("cat", result, Field.Store.YES));
							result = sl.lemmatize(category2);
							doc.add(new TextField("subcat", result, Field.Store.YES));
							result = sl.lemmatize(text);
							doc.add(new TextField("text", result, Field.Store.YES));
							result = sl.lemmatize(year);
							doc.add(new TextField("year", result, Field.Store.YES));
						}
						else{
							doc.add(new TextField("cat", category1, Field.Store.YES));
							doc.add(new TextField("subcat", category2, Field.Store.YES));
							doc.add(new TextField("year", year, Field.Store.YES));
							doc.add(new TextField("text", text, Field.Store.YES));
						}
						 w.addDocument(doc);
					}
					else
						dontInsert = 0;
					tempTitle = m.group(3);
					category1 = "";
					category2 = "";
					year = "";
					text= "";
					continue;
				}
				// Ignore documents with may refer to:
				boolean matchedMRT = Pattern.matches(pMRT, currentLine);
				if(matchedMRT){
					dontInsert = 1;
					continue;
				}
				// Ignore documents with #REDIRECT
				boolean matchedRed = Pattern.matches(pRed, currentLine);
				if(matchedRed){
					dontInsert = 1;
					continue;
				}
				// Ignore lines starting with '|'
				boolean matchedOr = Pattern.matches(pOr, currentLine);
				if(matchedOr){
					continue;
				}
				// Parsing CATEGORIES:
				m = pCat1.matcher(currentLine);
				if(m.find()){
				   String ans = m.group(2);
    			   category1 = ans;
    				Matcher mtemp = pIY.matcher(ans);
    			    if(mtemp.find()){
    				   year = ans;
    				}
				}
				//Parsing Section-Titles
				m = pCat2.matcher(currentLine);
				if(m.find()){
					// Removing Section titles that are very common in all wiki pages
					Pattern prem = Pattern.compile("(References|External links|Technology|See also|Notes|Bibliography)");
					Matcher mtemp = prem.matcher(m.group(2));
					if(!mtemp.find()){
					   if(!category2.equals("")){
						   category2 = category2 + " " + m.group(2);
					         mtemp = pIY.matcher(m.group(2));
		    					if(m.find())
		    					   year = m.group(2);
					    }
					    else{
					    	 category2 = m.group(2);
					         mtemp = pIY.matcher(m.group(2));
		    					if(mtemp.find())
		    					   year = m.group(2);
					    }
					}
				}
				// removing links starting with [tpl]
				m = ptpl.matcher(currentLine);
				if(m.find()){
				   String[] coll = ptpl.split(currentLine);
				   int i = 0;
				   while (i < coll.length){
					      text = text + " " + coll[i];
	    				  i++;
				   }
				   continue;
				}
				//Indexing contents of document
				if(!currentLine.isEmpty()){
					if(!text.equals("")){
    					text = text + " " + currentLine;
    					m = pIY.matcher(currentLine);
    					if(m.find())
    						year = currentLine;
					}
    				else{
    					text = currentLine;
    					m = pIY.matcher(currentLine);
    					if(m.find())
    						year = currentLine;
    				}
				}
		  }
		Document doc = new Document();
		doc.add(new StringField("title", tempTitle, Field.Store.YES));
        // If lemmatization is used
		if (appr.equals(lemmaAppr)){
			String result = sl.lemmatize(category1);
			doc.add(new TextField("cat", result, Field.Store.YES));
			result = sl.lemmatize(category2);
			doc.add(new TextField("subcat", result, Field.Store.YES));
			result = sl.lemmatize(text);
			doc.add(new TextField("text", result, Field.Store.YES));
			result = sl.lemmatize(year);
			doc.add(new TextField("year", result, Field.Store.YES));
		}
		else{
			doc.add(new TextField("cat", category1, Field.Store.YES));
			doc.add(new TextField("subcat", category2, Field.Store.YES));
			doc.add(new TextField("year", year, Field.Store.YES));
			doc.add(new TextField("text", text, Field.Store.YES));
		}
		w.addDocument(doc);
       
  }
  /**********************Parsing and Indexing for Naive Approach**************************/
  private static void prepareIndexNaive(IndexWriter w, BufferedReader bR) throws IOException{
	  
	  String currentLine = null;
	  Pattern pTitle = Pattern.compile("^(\\[\\[)((?!Image|File))(.*?)(\\]\\])$");
	  String pMRT = "^(.*?)(may refer to:)$"; // For MRT:
	  Pattern pCat1 = Pattern.compile("^(CATEGORIES: )(.*?)$"); //For Categories
	  String pRed = "^(#REDIRECT|#redirect)(.*?)$"; // for redirect
	  Pattern ptpl = Pattern.compile("(\\[tpl\\])(.*?)(\\[/tpl\\])"); // For tpl
	  Pattern.compile("^(.*?)(\\d{4})(.*?)$");
	  Pattern pCat2 = Pattern.compile("^(======|=====|====|===|==)(.*?)(==|===|====|=====|======)$");
	  int dontInsert = 1;
	  Matcher m;
	  String tempTitle = null;
	  String text = "";
	  
	  StanfordLemmatizerIf sl = null;

      if (appr.equals(lemmaAppr))
		try {
			sl = (StanfordLemmatizerIf) Class.forName("StanfordLemmatizer").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		while ((currentLine = bR.readLine()) != null) {

			    if (currentLine.isEmpty())
			    	continue;
			    m = pTitle.matcher(currentLine);
				if(m.find()){
					// Adding a new document
					if(dontInsert != 1){
						Document doc = new Document();
						doc.add(new StringField("title", tempTitle, Field.Store.YES));
			            // If lemmatization is used
						if (appr.equals(lemmaAppr)){
							String result = sl.lemmatize(text);
							doc.add(new TextField("text", result, Field.Store.YES));
						}
						else{
							doc.add(new TextField("text", text, Field.Store.YES));
						}
						 w.addDocument(doc);
					}
					else
						dontInsert = 0;
					tempTitle = m.group(3);
					text= "";
					continue;
				}
				// Ignore documents with may refer to:
				boolean matchedMRT = Pattern.matches(pMRT, currentLine);
				if(matchedMRT){
					dontInsert = 1;
					continue;
				}
				// Ignore documents with #REDIRECT
				boolean matchedRed = Pattern.matches(pRed, currentLine);
				if(matchedRed){
					dontInsert = 1;
					continue;
				}
				
				// Parsing CATEGORIES:
				m = pCat1.matcher(currentLine);
				if(m.find()){
				   text = m.group(2);
				}
				//Parsing Section-Titles
				m = pCat2.matcher(currentLine);
				if(m.find()){
				   if(!text.equals("")){
						text = text + " " + m.group(2);
				   }
				   else{
					    text = m.group(2);
				   }
				}
				// removing links starting with [tpl]
				m = ptpl.matcher(currentLine);
				if(m.find()){
				   String[] coll = ptpl.split(currentLine);
				   int i = 0;
				   while (i < coll.length){
					      text = text + " " + coll[i];
	    				  i++;
				   }
				   continue;
				}
				//Indexing contents of document
				if(!currentLine.isEmpty()){
					if(!text.equals("")){
    					text = text + " " + currentLine;
					}
    				else{
    					text = currentLine;
    				}
				}
		  }
		Document doc = new Document();
		doc.add(new StringField("title", tempTitle, Field.Store.YES));
        // If lemmatization is used
		if (appr.equals(lemmaAppr)){
			String result = sl.lemmatize(text);
			doc.add(new TextField("text", result, Field.Store.YES));
		}
		else{
			doc.add(new TextField("text", text, Field.Store.YES));
		}
		w.addDocument(doc);

  }
  static BM25Similarity simnew = new BM25Similarity();
  public static final String OkapiBM25 = "1";
  public static final String naiveAppr = "naive";
  public static final String stemAppr = "stem";
  public static final String lemmaAppr = "lemma";
  public static final String performanceatten = "P@10";
  public static final String performanceatone = "P@1";
  public static String appr = null;
}
