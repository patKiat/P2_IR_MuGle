//Name: 
//Section: 
//ID: 

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.plaf.synth.SynthSplitPaneUI;

public class TFIDFSearcher extends Searcher
{	public Map<String, Double> idfscore = new TreeMap();
	public Map<String, Double> dfscore = new TreeMap();
	public Set<String> vocab = new HashSet<String>();

	public Map<String, Integer> termID = new HashMap<>();
	public Map<Document, Integer> docID = new HashMap<>();
	public double[][] tf;
	public int idDoc;
	public int idTerm;

	public TFIDFSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/
		for(Document d: super.documents){
			vocab.addAll(d.getTokens());
		}

		idDoc = 0;
		idTerm = 0;
		for(Document d: super.documents){
			docID.put(d,idDoc++);
			for(String str : d.getTokens()){
				if(!termID.keySet().contains(str)){
					termID.put(str, idTerm++);
				}
			}
		}

		tf = new double[idDoc+1][idTerm+1];

		for(Document d : super.documents){
			HashSet<String> temp = new HashSet<>();
			temp.addAll(d.getTokens());
			for(String s:temp){
				if(dfscore.keySet().contains(s)){
					dfscore.replace(s, dfscore.get(s)+1);
				}
				else{
					dfscore.put(s, 1.0);
				}
			}
		}
		for(String q : vocab){
			double temp = 0;
			if(dfscore.get(q) != 0){
				temp = Math.log10(1+(super.documents.size()/dfscore.get(q)));
			}
			idfscore.put(q,temp);
		}
		//TF
		for(Document d:super.documents){
			int countTf;
			double tfCal;
			for(String s:d.getTokens()){
				countTf = Collections.frequency(d.getTokens(), s);
				if(countTf == 0){
					tfCal = 0;
				}else{
					tfCal =  1 + Math.log10(countTf);
				}
				tf[docID.get(d)][termID.get(s)] = tfCal;
			}
		}
		/***********************************************/
	}

	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/

		List<SearchResult> result = new ArrayList<SearchResult>();
		Map<String, Double> query = new TreeMap<>();
		Map<String, Double> doc;
		List<String> queryList = super.tokenize(queryString);
		//find weight for q
		for(String s:queryList){
			double weight;
			if(idfscore.get(s)!=null){
				double tempTF = 0;
				int countTF = Collections.frequency(queryList, s);
				if(countTF == 0){
					tempTF = 0.0;
				}else{
					tempTF = 1 + Math.log10(countTF);
				}
				weight = tempTF * idfscore.get(s);
			}
			else{
				weight = 0.0;
			}
			query.put(s, weight);
		}


		for(Document d:super.documents){
			doc = new TreeMap<>();
			//Calculate weight for document vector
			for(String s:d.getTokens()){
				if(termID.get(s) == null){
					doc.put(s, 0.0);
				}
				else{
					doc.put(s, (idfscore.get(s) * tf[docID.get(d)][termID.get(s)]));
				}
			}

			Set<String> unionSet = new HashSet<>();

			unionSet.addAll(d.getTokens());
			unionSet.addAll(queryList);
			double w = 0;
			double score = 0;
			double sized = 0;
			double sizeq = 0;
			for(String i:unionSet){
				if(doc.get(i)!=null&&query.get(i)!=null){
					w += doc.get(i)*query.get(i);
					sizeq += Math.pow(query.get(i),2);
					sized += Math.pow(doc.get(i),2);
				}
				else if (doc.get(i)==null&&query.get(i)!=null){
					sizeq += Math.pow(query.get(i),2);
				}
				else if(doc.get(i)!=null&&query.get(i)==null){
					sized += Math.pow(doc.get(i),2);
				}
			}
			score = w/(Math.sqrt(sized)*Math.sqrt(sizeq));
			SearchResult docResult = new SearchResult(d, score);
			result.add(docResult);
		}

		Collections.sort(result);
		return result.subList(0, k);
		/***********************************************/
	}
}