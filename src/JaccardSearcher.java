//Name: 
//Section: 
//ID: 

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class JaccardSearcher extends Searcher{

	public JaccardSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/
		// Remove repeat words
		for(Document d : super.documents){
//			System.out.println(d.getId()+"  "+d.getTokens());
			List<String> tempS = new ArrayList<>();
			for(String s: d.getTokens()){
				if(!tempS.contains(s)){
					tempS.add(s);
				}
			}
			d.setTokens(tempS);
//			System.out.println(d.getId()+"  "+d.getTokens());
		}

		/***********************************************/
	}

	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/

		//Tokenize query
		List<String> query = super.tokenize(queryString);
		List<String> queryList = new ArrayList<>();
		for(String str: query){
			if(!queryList.contains(str)){
				queryList.add(str);
			}
		}

		List<SearchResult> result = new ArrayList<SearchResult>();


		//Each Document
		int count;
		for(Document d: super.documents){
			//Each query
			count = 0;
			//Check intersection
			for(String q : queryList){
				if(d.getTokens().contains(q)){
					count++;
				}
			}
//			System.out.println(d.getId()+" Intersection: "+count);

			//Union
			HashSet<String> union = new HashSet<>();
			union.addAll(d.getTokens());
			union.addAll(queryList);
//			System.out.println(d.getId()+" Union: "+union.size());
			double score = count*1.0/union.size();
//			System.out.println(score);
			SearchResult docResult = new SearchResult(d, score);
			result.add(docResult);
		}
		//Ranking

		Collections.sort(result);

		return result.subList(0, k);
		/***********************************************/
	}

}