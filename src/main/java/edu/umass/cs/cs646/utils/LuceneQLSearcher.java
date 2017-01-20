package edu.umass.cs.cs646.utils;

import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.IOException;
import java.util.*;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.document.Document;
import java.io.*;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.math3.stat.inference.TTest;
public class LuceneQLSearcher extends AbstractQLSearcher {
	public static void main(String[] args){
		try{
			//Robust04
			/*String pathIndex = "index_robust04"; // index path - Robust04
			String pathQueries = "queries_robust04.txt"; // query file path
			String pathQrels = "qrels_robust04.txt"; // qrels file path
			LuceneQLSearcher searcher = new LuceneQLSearcher( pathIndex );
			Map<String, Set<String>> qrels = EvalUtils.loadQrels( pathQrels );
			
			*/
			//TREC123
			String pathIndex = "index_trec123"; // index path - TREC123
			String pathQueries = "queries_trec1-3.txt"; // query file path
			String pathQrels = "qrels_trec1-3.txt"; // qrels file path
			LuceneQLSearcher searcher = new LuceneQLSearcher( pathIndex );
			Map<String, Set<String>> qrels = EvalUtils.loadQrels( pathQrels );
			
			
			Analyzer analyzer = LuceneUtils.getAnalyzer( LuceneUtils.Stemming.Krovetz ); // change the stemming setting accordingly
			String pathStopwords = "stopwords_inquery.txt"; // change to your stop words path
			searcher.setStopwords( pathStopwords );
			String field_docno = "docno";
			String field_search = "content";
			Map<String, Map<String, Integer>> rele = new HashMap<String, Map<String, Integer>>();
			
			BufferedReader br = new BufferedReader(new FileReader(pathQrels));
        	try {
        	    StringBuilder sb = new StringBuilder();
        	    String line = br.readLine();

        	    while (line != null) {
        	    	List<String> s = Arrays.asList(line.split("\\s+"));
        	        
        	    	for(int i = 0; i<s.size();i+=4){
        	    		String qid = s.get(i);
 	        	       
	        	        String docno = s.get(i+2);
	        	        int relevance = Integer.parseInt(s.get(i+3));
	        	        Map<String, Integer> tmp = new HashMap<String, Integer>();
	        	        
	        	        if(!rele.containsKey(qid)){
	        	        	tmp.put(docno, relevance);
	        	        	rele.put(qid, tmp);
	        	        }else{
	        	        	if(!rele.get(qid).containsKey(docno)){
	        	        		tmp = rele.get(qid);
	        	        		tmp.put(docno, relevance);
	        	        		rele.put(qid, tmp);
	        	        	}
	        	        }
        	    	}
        	    	
        	    	line = br.readLine();
        	    }
        	} finally {
        	    br.close();
        	}
			
        	NumberFormat formatter = new DecimalFormat("#0.000"); 
			Map<Double, Double> apvals = new HashMap<Double, Double>();
			Map<Double, Double> p10vals = new HashMap<Double, Double>();
			Map<Double, Double> ndcgvals = new HashMap<Double, Double>();
			Map<Double, Double> errvals = new HashMap<Double, Double>();
			int top = 1000;
			double mu = 1500.0;
			double Lorg = 0.9;
			double mu1 = 1000.0;
			double mu2 = 0.0;
			double alpha = 0.0;
			int numfbterms = 100;
			int numfbdocs = 10;
			double total = 0;
			Map<String, String> queries = EvalUtils.loadQueries( pathQueries );
			
			
			List<Double> rm4rob = new ArrayList<Double>();
			double rm4robloss = 0.11228221;
			rm4rob.add(0.13423582);
			rm4rob.add(-0.5044497);
			rm4rob.add(0.58156316);
			rm4rob.add(-0.35776858);
			rm4rob.add(-0.10706904);
			List<Double> rm4trec = new ArrayList<Double>();
			double rm4trecloss = 0.81732925;
			rm4trec.add(-0.18447744);
			 rm4trec.add(-0.42194312);
			 rm4trec.add( -0.06137184);
			 rm4trec.add(-0.18982568);
			 rm4trec.add(0.0);
			List<Double> rm3trec = new ArrayList<Double>();
			double rm3trecloss = -1.60396653;
			rm3trec.add(0.18586684);
			 rm3trec.add(-0.42544561);
			 rm3trec.add(0.2592747);
			 rm3trec.add(0.2146465 );
			 rm3trec.add(0.0);
			List<Double> rm3rob= new ArrayList<Double>();
			double rm3robloss = -1.23207628;
			rm3rob.add(0.13638937);
			 rm3rob.add(-0.02545945 );
			 rm3rob.add(0.56740556);
			 rm3rob.add(-0.33148869);
			 rm3rob.add(   -0.09287719);
			 
			//Map<String, Double> ndcgmap = new HashMap<String, Double>(searcher.getNDCG(queries, qrels, rele));
			//Map<String, Double> errmap = new HashMap<String, Double>(searcher.getERR(queries, qrels, rele));
			
			
			double[] p10 = new double[queries.size()];
			double[] ap = new double[queries.size()];
			double[] ndcg = new double[queries.size()];
			double[] err = new double[queries.size()];
			double[] opalpha = new double[queries.size()];
			
			double[] p102 = new double[queries.size()];
			double[] ap2 = new double[queries.size()];
			double[] ndcg2 = new double[queries.size()];
			double[] err2 = new double[queries.size()];
			double[] opalpha2 = new double[queries.size()];
			
			for(String qid:queries.keySet()){
			
				//Map<String, Double> ndcgmap = new HashMap<String, Double>(this.getNDCG(queries, qrels, rele));
				double ndcgsum = 0.0;
				double errsum = 0.0;
 				int ix = 0;
 				double maxap = -1.0;
 				double optalpha=0.0;
 				
 				double ndcgsum2 = 0.0;
				double errsum2 = 0.0;
 				int ix2 = 0;
 				double maxap2 = -1.0;
 				double optalpha2=0.0;
 				double maxndcg1=0.0;
 				double maxndcg2=0.0;
 				double maxerr1=0.0;
 				double maxerr2=0.0;
 				String query = queries.get( qid );
				List<String> terms = LuceneUtils.tokenize( query, analyzer );
				//System.out.println("This is: "+qid);
				double alpharm3 = 0.0;
				double alpharm4 = 0.0;
				double sumrm3=0.0;
				double sumrm4=0.0;
				
				
				/*List<Double> features = new ArrayList<Double>();
				features.add(searcher.QEnt_R1(field_search, terms));
				features.add(searcher.QEnt_R3(field_search, terms));
				features.add(searcher.FBEnt_R1(field_search, terms, mu, mu2, numfbdocs, numfbterms, 0.7));
				features.add(searcher.FBEnt_R2(field_search, terms, mu, mu2, numfbdocs, numfbterms, 0.7));
				features.add(searcher.Feedback_Length(field_search, terms, mu, mu2, numfbdocs, numfbterms, 0.7));
				
				for(int i = 0; i < features.size();i++){
					sumrm3+=(double)(rm3rob.get(i)*features.get(i));
					sumrm4+=(double)(rm4rob.get(i)*features.get(i));
				}
				sumrm3+=rm3robloss;
				sumrm4+=rm4robloss;
				alpharm3=(double)(1/(1+Math.exp(-sumrm3)));
				alpharm4=(double)(1/(1+Math.exp(-sumrm4)));
				sumrm3=0.0;
				sumrm4=0.0;*/
				//System.out.println(alpharm3+" "+alpharm4);
 				//for(alpha = 0.1;alpha<=0.9; alpha+=0.1){
					//System.out.println("Alpha is:"+alpha);
					Map<String, Double> rm1 = searcher.estimateQueryModelRM1( field_search, terms, mu, mu2, numfbdocs, numfbterms );
					Map<String, Double> rm3 = searcher.estimateQueryModelRM3( terms, rm1, 0.01 );
					List<SearchResult> RM3results = searcher.search( field_search, rm3, mu, top );
					
					Map<String, Double> rm2 = searcher.estimateQueryModelRM2( field_search, terms, mu, mu2, numfbdocs, numfbterms );
					Map<String, Double> rm4 = searcher.estimateQueryModelRM4( terms, rm2, 0.01 );
					
					//for(String w:rm4.keySet()){
						//System.out.println(w+" "+rm4.get(w));
					//}
					List<SearchResult> RM4results = searcher.search( field_search, rm4, mu, top );
					
					SearchResult.dumpDocno( searcher.index, field_docno, RM3results );
					SearchResult.dumpDocno(searcher.index, field_docno, RM4results);
					//if(qrels.containsKey(qid)){
						//if(EvalUtils.avgPrec(RM3results, qrels.get(qid), top)>maxap){
							//maxap=EvalUtils.avgPrec(RM3results, qrels.get(qid), top);
							//optalpha=alpha;
							//maxndcg1=searcher.getNDCG(queries, qid, qrels, rele, RM3results);
							//maxerr1=searcher.getERR(queries, qid, qrels, rele, RM3results);
						//}
						
						
						//System.out.println(alpha+"\t"+EvalUtils.avgPrec(RM3results, qrels.get(qid), top));
						//p10[ix] = EvalUtils.precision( RM3results, qrels.get( qid ), 10 );
						
					if(qrels.containsKey(qid)){
					ap[ix] = EvalUtils.avgPrec( RM3results, qrels.get( qid ), top );
						ndcg[ix] = searcher.getNDCG(queries, qid, qrels, rele, RM3results);
						err[ix] = searcher.getERR(queries, qid, qrels, rele, RM3results);
						ap2[ix2] = EvalUtils.avgPrec( RM4results, qrels.get( qid ), top );
						ndcg2[ix2] = searcher.getNDCG(queries, qid, qrels, rele, RM4results);
						err2[ix2] = searcher.getERR(queries, qid, qrels, rele, RM4results);
						/*System.out.printf(
								"%-10s%8.3f%8.3f\n",
								qid,
								p10[ix],
								ap[ix]
						);*/
					}
					
					//SearchResult.dumpDocno( searcher.index, field_docno, RM4results );
					//if(qrels.containsKey(qid)){
						//System.out.println("Result"+EvalUtils.avgPrec(RM4results, qrels.get(qid), top));
						//if(EvalUtils.avgPrec(RM4results, qrels.get(qid), top)>maxap2){
							//maxap2=EvalUtils.avgPrec(RM4results, qrels.get(qid), top);
							//optalpha2=alpha;
							//maxndcg2=searcher.getNDCG(queries, qid, qrels, rele, RM4results);
							//maxerr2=searcher.getERR(queries, qid, qrels, rele, RM4results);
						//}
						//System.out.println(alpha+"\t"+EvalUtils.avgPrec(RM3results, qrels.get(qid), top));
						//p10[ix] = EvalUtils.precision( RM3results, qrels.get( qid ), 10 );
						//ap[ix] = EvalUtils.avgPrec( RM3results, qrels.get( qid ), top );
						//ndcg[ix] = ndcgmap.get(qid);
						//err[ix] = errmap.get(qid);
						/*System.out.printf(
								"%-10s%8.3f%8.3f\n",
								qid,
								p10[ix],
								ap[ix]
						);*/
					//}
					
					
					//ndcgsum+=ndcg[ix];
					//errsum+=err[ix];
					//ix++;
					//System.out.println("System exiting");
				//}
 				//if(maxap!=-1){
 					//ap[ix]=maxap;
 					//System.out.println(qid+"\t"+formatter.format(ap[ix]));
 					//opalpha[ix]=optalpha;
 					//ndcg[ix]=maxndcg1;
 					//err[ix]=maxerr1;
 					//ndcg[ix]=searcher.getNDCG(queries, qid, qrels, rele, results)
 					/*System.out.printf(
 							"%-10s%-25s%10.3f%10.3f\n",
 							"QL",
 							"QL",
 							StatUtils.mean( p10 ),
 							StatUtils.mean( ap )
 					);
 					*/
 					//p10vals.put(alpha, StatUtils.mean(p10));
 					//apvals.put(alpha, StatUtils.mean(ap));
 					//ndcgvals.put(alpha, (double)ndcgsum/ndcg.length);
 					//errvals.put(alpha, (double)errsum/err.length);
 					//System.out.println("The value of alpha is: " + alpha + " and the value of MAP is: " + p10vals.get(alpha));
 					//System.out.printf("%-10s%10s%10s%10s\n", "alpha", "MAP", "nDCG", "ERR");
 					//System.out.printf( "%-8.3f%8.3f%8.3f%8.3f\n", alpha,apvals.get(alpha),ndcgvals.get(alpha),errvals.get(alpha) );
 					//System.out.println("For qid: "+qid+" maxap is: "+ap[ix]+" and optimal alpha is: "+opalpha[ix]);
 	 					
 	 				//System.out.println(formatter.format(0.0-Math.log((1/opalpha[ix])-1))+"\t"+formatter.format()+"\t"+formatter.format()+"\t"+formatter.format()+"\t"+formatter.format()+"\t"+formatter.format());	
 	 					
 	 				//System.out.println(formatter.format(opalpha[ix])+"\t"+formatter.format(searcher.QEnt_R1(field_search, terms))+"\t"+formatter.format(searcher.QEnt_R3(field_search, terms))+"\t"+formatter.format(searcher.FBEnt_R1(field_search, terms, mu, mu2, numfbdocs, numfbterms, 0.7))+"\t"+formatter.format(searcher.FBEnt_R2(field_search, terms, mu, mu2, numfbdocs, numfbterms, 0.7))+"\t"+formatter.format(searcher.Feedback_Length(field_search, terms, mu, mu2, numfbdocs, numfbterms, 0.7)));	
 	 					
 				//}
 				
 				//if(maxap2!=-1){
 					//ap2[ix2]=maxap2;
 					//System.out.println(qid+"\t"+formatter.format(ap2[ix2]));
 					//opalpha2[ix2]=optalpha2;
 					//ndcg2[ix2]=maxndcg2;
 					//err2[ix2]=maxerr2;
 					/*System.out.printf(
 							"%-10s%-25s%10.3f%10.3f\n",
 							"QL",
 							"QL",
 							StatUtils.mean( p10 ),
 							StatUtils.mean( ap )
 					);
 					*/
 					//p10vals.put(alpha, StatUtils.mean(p10));
 					//apvals.put(alpha, StatUtils.mean(ap));
 					//ndcgvals.put(alpha, (double)ndcgsum/ndcg.length);
 					//errvals.put(alpha, (double)errsum/err.length);
 					//System.out.println("The value of alpha is: " + alpha + " and the value of MAP is: " + p10vals.get(alpha));
 					//System.out.printf("%-10s%10s%10s%10s\n", "alpha", "MAP", "nDCG", "ERR");
 					//System.out.printf( "%-8.3f%8.3f%8.3f%8.3f\n", alpha,apvals.get(alpha),ndcgvals.get(alpha),errvals.get(alpha) );
 					//System.out.println("For qid: "+qid+" maxap is: "+ap[ix]+" and optimal alpha is: "+opalpha[ix]);
 	 					
 	 				//System.out.println(formatter.format(0.0-Math.log((1/opalpha[ix])-1))+"\t"+formatter.format(searcher.QEnt_R1(field_search, terms))+"\t"+formatter.format(searcher.QEnt_R3(field_search, terms))+"\t"+formatter.format(searcher.FBEnt_R1(field_search, terms, mu, mu2, numfbdocs, numfbterms, 0.7))+"\t"+formatter.format(searcher.FBEnt_R2(field_search, terms, mu, mu2, numfbdocs, numfbterms, 0.7))+"\t"+formatter.format(searcher.Feedback_Length(field_search, terms, mu, mu2, numfbdocs, numfbterms, 0.7)));	
 	 					
 	 				//System.out.println(formatter.format(opalpha[ix])+"\t"+formatter.format()+"\t"+formatter.format()+"\t"+formatter.format(searcher.FBEnt_R1(field_search, terms, mu, mu2, numfbdocs, numfbterms, 0.7))+"\t"+formatter.format(searcher.FBEnt_R2(field_search, terms, mu, mu2, numfbdocs, numfbterms, 0.7))+"\t"+formatter.format(searcher.Feedback_Length(field_search, terms, mu, mu2, numfbdocs, numfbterms, 0.7)));	
 	 					
 				//}
 				System.out.println(qid+"\t"+formatter.format(ap[ix])+"\t"+formatter.format(ap2[ix2])+"\t"+formatter.format(ndcg[ix])+"\t"+formatter.format(ndcg2[ix2])+"\t"+formatter.format(err[ix])+"\t"+formatter.format(err2[ix2]));
				ix++;
				ix2++;
			}
			//System.out.println(formatter.format(StatUtils.mean(ap))+"\t"+formatter.format(StatUtils.mean(ap2))+"\t"+formatter.format(StatUtils.mean(ndcg))+"\t"+formatter.format(StatUtils.mean(ndcg2))+"\t"+formatter.format(StatUtils.mean(err))+"\t"+formatter.format(StatUtils.mean(err2)));
			searcher.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	protected File dirBase;
	protected Directory dirLucene;
	protected IndexReader index;
	protected Map<String, DocLengthReader> doclens;
	
	public LuceneQLSearcher( String dirPath ) throws IOException {
		this( new File( dirPath ) );
	}
	
	public LuceneQLSearcher( File dirBase ) throws IOException {
		this.dirBase = dirBase;
		this.dirLucene = FSDirectory.open( this.dirBase.toPath() );
		this.index = DirectoryReader.open( dirLucene );
		this.doclens = new HashMap<>();
	}
	
	public IndexReader getIndex() {
		return this.index;
	}
	
	public PostingList getPosting( String field, String term ) throws IOException {
		return new LuceneTermPostingList( index, field, term );
	}
	
	public DocLengthReader getDocLengthReader( String field ) throws IOException {
		DocLengthReader doclen = doclens.get( field );
		if ( doclen == null ) {
			doclen = new FileDocLengthReader( this.dirBase, field );
			doclens.put( field, doclen );
		}
		return doclen;
	}
	
	public void close() throws IOException {
		index.close();
		dirLucene.close();
		for ( DocLengthReader doclen : doclens.values() ) {
			doclen.close();
		}
	}
	
	public Map<String, Double> estimateQueryModelRM1( String field, List<String> terms, double mu, double mufb, int numfbdocs, int numfbterms ) throws IOException {
		
		List<SearchResult> results = search( field, terms, mu, numfbdocs );
		Set<String> voc = new HashSet<>();
		for ( SearchResult result : results ) {
			TermsEnum iterator = index.getTermVector( result.getDocid(), field ).iterator();
			BytesRef br;
			while ( ( br = iterator.next() ) != null ) {
				if ( !isStopwords( br.utf8ToString() ) ) {
					voc.add( br.utf8ToString() );
				}
			}
		}
		
		Map<String, Double> collector = new HashMap<>();
		for ( SearchResult result : results ) {
			double ql = result.getScore();
			double dw = Math.exp( ql );
			TermsEnum iterator = index.getTermVector( result.getDocid(), field ).iterator();
			Map<String, Integer> tfs = new HashMap<>();
			int len = 0;
			BytesRef br;
			while ( ( br = iterator.next() ) != null ) {
				tfs.put( br.utf8ToString(), (int) iterator.totalTermFreq() );
				len += iterator.totalTermFreq();
			}
			for ( String w : voc ) {
				int tf = tfs.getOrDefault( w, 0 );
				double pw = ( tf + mufb * index.totalTermFreq( new Term( field, w ) ) / index.getSumTotalTermFreq( field ) ) / ( len + mufb );
				collector.put( w, collector.getOrDefault( w, 0.0 ) + pw * dw );
			}
		}
		return Utils.getTop( Utils.norm( collector ), numfbterms );
	}
	
public Map<String, Double> estimateQueryModelRM2( String field, List<String> terms, double mu, double mufb, int numfbdocs, int numfbterms ) throws IOException {
		Map<String, Double>res = new HashMap<String, Double>();
		List<SearchResult> results = search( field, terms, mu, numfbdocs );
		Set<String> voc = new HashSet<>();
		for ( SearchResult result : results ) {
			TermsEnum iterator = index.getTermVector( result.getDocid(), field ).iterator();
			BytesRef br;
			while ( ( br = iterator.next() ) != null ) {
				if ( !isStopwords( br.utf8ToString() ) ) {
					voc.add( br.utf8ToString() );
				}
			}
		}
		
		Map<String, Double> term_doc_score = new HashMap<String, Double>();
		Map<String, Map<SearchResult, Double>>q_score = new HashMap<String, Map<SearchResult, Double>>(); 
		Map<String, Map<SearchResult, Double>>qtfs = new HashMap<String, Map<SearchResult, Double>>();
		Map<String, Double> collector = new HashMap<>();
		Map<SearchResult, Double> docsize = new HashMap<SearchResult, Double>();
		for ( SearchResult result : results ) {
			double ql = result.getScore();
			double dw = Math.exp( ql );
			TermsEnum iterator = index.getTermVector( result.getDocid(), field ).iterator();
			Map<String, Integer> tfs = new HashMap<>();
			int len = 0;
			BytesRef br;
			while ( ( br = iterator.next() ) != null ) {
				tfs.put( br.utf8ToString(), (int) iterator.totalTermFreq() );
				len += iterator.totalTermFreq();
				//System.out.println("TFS"+tfs.get(br.utf8ToString()));
			}
			
			docsize.put(result, (double)len);
			for ( String w : voc ) {
				int tf = tfs.getOrDefault( w, 0 );
				double pw = ( (double)tf + mufb * index.totalTermFreq( new Term( field, w ) ) / index.getSumTotalTermFreq( field ) ) / ( (double)len + mufb );
				collector.put( w, (double)(collector.getOrDefault( w, 0.0 ) + pw) );
			}
		}
		
		/*for(String w:voc){
			System.out.println("Collector");
			System.out.println(collector.get(w));
		}*/
		double mult=1.0;
		double sum=0.0;
		for(String w:voc){
			for(String term:terms){
				for(SearchResult result:results){
					double ql = result.getScore();
					double dw = Math.exp( ql );
					TermsEnum iterator = index.getTermVector( result.getDocid(), field ).iterator();
					Map<String, Double> tfs = new HashMap<>();
					int len = 0;
					BytesRef br;
					while ( ( br = iterator.next() ) != null ) {
						tfs.put( br.utf8ToString(), (double) iterator.totalTermFreq() );
						len += iterator.totalTermFreq();
					}
					iterator = index.getTermVector( result.getDocid(), field ).iterator();
					//Map<String, Integer> tfs = new HashMap<>();
					//int len = 0;
					//BytesRef br;
					//while ( ( br = iterator.next() ) != null ) {
						if(tfs.containsKey(term)){
							double qterm = (double)(((double)tfs.get(term)+mufb* index.totalTermFreq( new Term( field, term ) ) / index.getSumTotalTermFreq( field ))/(docsize.get(result)+mufb));
							double tterm = (double)( (double)tfs.getOrDefault(w,0.0) + mufb * index.totalTermFreq( new Term( field, w ) ) / index.getSumTotalTermFreq( field ) ) / ( docsize.get(result) + mufb )/collector.get(w);
							sum+=(double)qterm*tterm;
							//System.out.println("qterm"+qterm);
							break;
						}
						
					//}
					
				}
				mult*=sum;
				sum=0.0;
			}
			res.put(w,(double)mult*collector.get(w));
			mult=1.0;
		}
		
		
		
		return Utils.getTop( Utils.norm( res ), numfbterms );
	}
	
	public Map<String, Double> estimateQueryModelRM4( List<String> terms, Map<String, Double> rm2, double weight_org ) throws IOException {
		
		Map<String, Double> mle = new HashMap<>();
		for ( String term : terms ) {
			mle.put( term, mle.getOrDefault( term, 0.0 ) + 1.0 );
		}
		for ( String w : mle.keySet() ) {
			mle.put( w, mle.get( w ) / terms.size() );
		}
		
		Set<String> v = new TreeSet<>();
		v.addAll( terms );
		v.addAll( rm2.keySet() );
		
		Map<String, Double> rm4 = new HashMap<>();
		for ( String w : v ) {
			rm4.put( w, weight_org * mle.getOrDefault( w, 0.0 ) + ( 1 - weight_org ) * rm2.getOrDefault( w, 0.0 ) );
		}
		
		return rm4;
	}

	public Map<String, Double> estimateQueryModelRM3( List<String> terms, Map<String, Double> rm1, double weight_org ) throws IOException {
		
		Map<String, Double> mle = new HashMap<>();
		for ( String term : terms ) {
			mle.put( term, mle.getOrDefault( term, 0.0 ) + 1.0 );
		}
		for ( String w : mle.keySet() ) {
			mle.put( w, mle.get( w ) / terms.size() );
		}
		
		Set<String> v = new TreeSet<>();
		v.addAll( terms );
		v.addAll( rm1.keySet() );
		
		Map<String, Double> rm3 = new HashMap<>();
		for ( String w : v ) {
			rm3.put( w, weight_org * mle.getOrDefault( w, 0.0 ) + ( 1 - weight_org ) * rm1.getOrDefault( w, 0.0 ) );
		}
		
		return rm3;
	}
	
	public Map<String, Double> getIDCG(Map<String, String> queries, Map<String, Set<String>> qrels , Map<String, Map<String, Integer>> rele) throws IOException{
		int ik = 10;
    	Map<String, Double> idcgq = new HashMap<String, Double>();
    	Map<String, Double> idcgsys = new HashMap<String, Double>();
    	double idcgsum = 0.0;
    	double idcgtotal = 0.0;
    	for(String qid:queries.keySet()){
    			if(qrels.containsKey(qid)){
    				Set<String> s = new HashSet<String>();
        			s=qrels.get(qid);
        			//System.out.println("The qid is:"+qid);
        			//System.out.println("The size of the set s is:"+s.size());
        			List<String> listdocs = new ArrayList<String>();
        			for(String str:s){
        				listdocs.add(str);
        			}
        			Collections.sort(listdocs, (w1,w2)->rele.get(qid).get(w2).compareTo(rele.get(qid).get(w1)));
        			for(int i = 0; i < ik&&i<listdocs.size(); i++){
        				if(rele.get(qid).containsKey(listdocs.get(i))){
        					idcgsum+=(double)((Math.pow(2.0, (double)rele.get(qid).get(listdocs.get(i)))-1)/(double)(Math.log(i+2)/Math.log(2)));
        				}
        			}
        			
        			idcgq.put(qid, idcgsum);
        			idcgsum=0.0;
    			}else{
    				idcgq.put(qid, 0.0);
    			}
    			
    			
    	}
    	return idcgq;
	}
	
	public double getNDCG(Map<String, String> queries, String qid, Map<String, Set<String>> qrels , Map<String, Map<String, Integer>> rele, List<SearchResult> results) throws IOException{
		int k = 10;
		double res = 0.0;
    	Map<String, Double> dcgq = new HashMap<String, Double>();
    	Map<String, Double> dcgsys = new HashMap<String, Double>();
    	Map<String, Double> ndcg = new HashMap<String, Double>();
    	Map<String, Double> ndcgq = new HashMap<String, Double>();
    	Map<String, List<Double>> dcgvalslist = new HashMap<String, List<Double>>();
    	Map<String, List<Double>> ndcgvalslist = new HashMap<String, List<Double>>();
    	List<Double> dcgtmp = new ArrayList<Double>();
    	List<Double> ndcgtmp = new ArrayList<Double>();
    	double ndcgtotal=0.0;
    	double dcgsum = 0.0;
    	double dcgtotal = 0.0;
    	Map<String, Double> docscore = new HashMap<String, Double>();
    	for(SearchResult result:results){
    		docscore.put(result.getDocno(), result.getScore());
    	}
    	//for(String qid:queries.keySet()){
    			//Map<String, Integer> tmp = new HashMap<String, Integer>(Utils.getTop(q_rank.get(name).get(qid), k));
    			//Map<String, Map<String, Integer>> tmp2 = new HashMap<String, Map<String, Integer>>(q_rank.get(name));
    			//tmp2.put(qid, tmp);
    			//TreeMap<String, Integer> tmp3 = new TreeMap<String, Integer>();
    		if(qrels.containsKey(qid)){
    			Set<String> s = new HashSet<String>(docscore.keySet());
    			List<String> listdocs = new ArrayList<String>();
    			for(String str:s){
    				listdocs.add(str);
    			}
    			Collections.sort(listdocs, (w1,w2)->docscore.get(w2).compareTo(docscore.get(w1)));
        			
        			for(int i = 0; i < listdocs.size()&&i<k; i++){
        				if(rele.get(qid).containsKey(listdocs.get(i))){
        					dcgsum+=(double)((Math.pow(2.0, (double)rele.get(qid).get(listdocs.get(i)))-1)/(double)(Math.log(i+2)/Math.log(2)));
        				}
        			}
        			
        			
        			//dcgtmp.add(dcgsum);
        			//dcgtotal+=dcgsum;
        			//ndcgtotal+=(dcgsum/(double)idcgq.get(qid));
        			//ndcgq.put(qid, (dcgsum/(double)this.getIDCG(queries, qrels, rele).get(qid)));
        			//ndcgtmp.add(dcgsum/(double)idcgq.get(qid));
        			res = (double)dcgsum/this.getIDCG(queries, qrels, rele).get(qid);
    		}else{
    			res=0.0;
    		}
    		
    			
    		
    		
    	return res;
	}
	public static double R(int g, int gmax) throws IOException{
		double a;
		a = (double)(((double)Math.pow(2, g)-1)/(double)Math.pow(2, gmax));
		return a;
	}
	public double getERR(Map<String, String> queries, String qid, Map<String, Set<String>> qrels , Map<String, Map<String, Integer>> rele, List<SearchResult> results) throws IOException{
		int mx = 2;
		int k = 10;
		double res=0.0;
    	Map<String, Double> errq = new HashMap<String, Double>();
    	Map<String, Double> errname = new HashMap<String, Double>();
    	Map<String, List<Double>> errvalslist = new HashMap<String, List<Double>>();
    	List<Double> errtmp=new ArrayList<Double>();
    	double errtotal=0.0;
    	Map<String, Double> docscore = new HashMap<String, Double>();
    	for(SearchResult result:results){
    		docscore.put(result.getDocno(), result.getScore());
    	}
    	//for(String name:q_rank.keySet()){
    		//for(String qid:queries.keySet()){
    			//Map<String, Integer> tmp = new HashMap<String, Integer>(Utils.getTop(q_rank.get(name).get(qid), k));
    			if(qrels.containsKey(qid)){
    				Set<String> s = new HashSet<String>(docscore.keySet());
        			List<String> listdocs = new ArrayList<String>();
        			for(String str:s){
        				listdocs.add(str);
        			}
        			List<String> listrels = new ArrayList<String>(rele.get(qid).keySet());
        			Collections.sort(listrels, (w1,w2)->rele.get(qid).get(w2).compareTo(rele.get(qid).get(w1)));
        			int maxgrade=rele.get(qid).get(listrels.get(0));
        			Collections.sort(listdocs, (w1,w2)->docscore.get(w2).compareTo(docscore.get(w1)));
        			double innerproduct=1.0;
        			double outersum=0.0;
        			double checkersum = 0.0;
        			double checker = 0.0;
        			for(int i = 0; i < k&&i<listdocs.size();i++){
        				for(int j=0;j<i;j++){
        					if(listrels.contains(listdocs.get(j))){
        						innerproduct*=(double)(1-R(rele.get(qid).get(listdocs.get(j)),mx));
        					}else{
        						innerproduct*=(double)(1-R(0, mx));
        					}
        				}
        				if(listrels.contains(listdocs.get(i))){
        					checker=(double)R(rele.get(qid).get(listdocs.get(i)),mx)*innerproduct;
        					checkersum+=checker;
        					outersum+=(double)(checker/(double)(i+1));
        				}else{
        					checker=(double)R(0,mx)*innerproduct;
        					checkersum+=checker;
        					outersum+=(double)(checker/(double)(i+1));
        				}
        				innerproduct=1.0;
        			}
        			res=outersum;
        			errq.put(qid, outersum);
        			//errtmp.add(outersum);
        			//errtotal+=outersum;
        			
    			}else{
    				res=0.0;
    			}
    			
    		
    		//errname.put(name, (double)(errtotal/(double)q_rank.get(name).size()));
    		//errvalslist.put(name, errtmp);
    		//errtmp=new ArrayList<Double>();
    		//errtotal=0.0;

        	return res;
	
	}
	
	
	/*The 6 most significant features
	QEnt_R1
	QEnt_R3
	FBEnt_R1
	FBEnt_R2
	|F|
	QFBDiv_A
	*/
	
	public double QEnt_R1(String field, List<String> terms) throws IOException{
		double sum=0.0;
		for(String t:terms){
			double temp_q = (double)Collections.frequency(terms, t)/terms.size();
			double temp_c = (double)index.totalTermFreq(new Term(field, t))/index.getSumTotalTermFreq(field);
			sum+=(temp_q*Math.log((double)temp_q/temp_c));
		}
		
		return sum;
	}
	
	public double QEnt_R3(String field, List<String> terms) throws IOException{
		return (double)Math.log(QEnt_R1(field, terms));
	}
	
	public double FBEnt_R1(String field, List<String> terms, double mu, double mufb, int numfbdocs, int numfbterms, double lambda) throws IOException{
		double res = 0.0;
		List<SearchResult> results = search( field, terms, mu, numfbdocs );
		Set<String> voc = new HashSet<>();
		for ( SearchResult result : results ) {
			TermsEnum iterator = index.getTermVector( result.getDocid(), field ).iterator();
			BytesRef br;
			while ( ( br = iterator.next() ) != null ) {
				if ( !isStopwords( br.utf8ToString() ) ) {
					voc.add( br.utf8ToString() );
				}
			}
		}
		
		Map<String, Double> collector = new HashMap<>();
		for ( SearchResult result : results ) {
			double ql = result.getScore();
			double dw = Math.exp( ql );
			TermsEnum iterator = index.getTermVector( result.getDocid(), field ).iterator();
			Map<String, Integer> tfs = new HashMap<>();
			int len = 0;
			BytesRef br;
			while ( ( br = iterator.next() ) != null ) {
				tfs.put( br.utf8ToString(), tfs.getOrDefault(br.utf8ToString(), 0)+(int) iterator.totalTermFreq() );
				len += iterator.totalTermFreq();
			}
			
			for ( String w : voc ) {
				int tf = tfs.getOrDefault( w, 0 );
				res+= (((1-lambda)*tf/len) + lambda *( index.totalTermFreq( new Term( field, w ) ) / index.getSumTotalTermFreq( field ) ));
				
			}
		}
		return res;
	}
	
	public double FBEnt_R2(String field, List<String> terms, double mu, double mufb, int numfbdocs, int numfbterms, double lambda) throws IOException{
		return Math.exp(FBEnt_R1(field, terms, mu, mufb, numfbdocs, numfbterms, lambda));
	}

	public double Feedback_Length(String field, List<String> terms, double mu, double mufb, int numfbdocs, int numfbterms, double lambda) throws IOException{
		List<SearchResult> results = search(field, terms, mu, numfbterms);
		return (double)results.size();
	}
	
	public double QFBDiv_A(String field, List<String> terms, double mu, double mufb, int numfbdocs, int numfbterms, double lambda) throws IOException{
		double res=0.0;
		List<SearchResult> results = search( field, terms, mu, numfbdocs );
		Set<String> voc = new HashSet<>();
		for ( SearchResult result : results ) {
			TermsEnum iterator = index.getTermVector( result.getDocid(), field ).iterator();
			BytesRef br;
			while ( ( br = iterator.next() ) != null ) {
				if ( !isStopwords( br.utf8ToString() ) ) {
					voc.add( br.utf8ToString() );
				}
			}
		}
		
		for(String w:voc){
			System.out.println(w);
			if(estimateQueryModelRM1(field, terms, mu, mufb, numfbdocs, numfbterms).containsKey(w)&&estimateQueryModelRM1(field, terms, mu, 1500, numfbdocs, numfbterms).get(w)>0.0){
				System.out.println("In");
				res+=(estimateQueryModelRM1(field, terms, mu, mufb, numfbdocs, numfbterms).get(w)*Math.log(estimateQueryModelRM1(field, terms, mu, mufb, numfbdocs, numfbterms).get(w)/estimateQueryModelRM1(field, terms, mu, 1500, numfbdocs, numfbterms).get(w)));
			}
			
		}
		
		return res;
	}
	
}
