//Name: 
//Section: 
//ID: 

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.stylesheets.LinkStyle;

public class SearcherEvaluator {
	private List<Document> queries = null;				//List of test queries. Each query can be treated as a Document object.
	private  Map<Integer, Set<Integer>> answers = null;	//Mapping between query ID and a set of relevant document IDs

	public List<Document> getQueries() {
		return queries;
	}

	public Map<Integer, Set<Integer>> getAnswers() {
		return answers;
	}

	/**
	 * Load queries into "queries"
	 * Load corresponding documents into "answers"
	 * Other initialization, depending on your design.
	 * @param corpus
	 */
	public SearcherEvaluator(String corpus)
	{
		String queryFilename = corpus+"/queries.txt";
		String answerFilename = corpus+"/relevance.txt";

		//load queries. Treat each query as a document.
		this.queries = Searcher.parseDocumentFromFile(queryFilename);
		this.answers = new HashMap<Integer, Set<Integer>>();
		//load answers
		try {
			List<String> lines = FileUtils.readLines(new File(answerFilename), "UTF-8");
			for(String line: lines)
			{
				line = line.trim();
				if(line.isEmpty()) continue;
				String[] parts = line.split("\\t");
				Integer qid = Integer.parseInt(parts[0]);
				String[] docIDs = parts[1].trim().split("\\s+");
				Set<Integer> relDocIDs = new HashSet<Integer>();
				for(String docID: docIDs)
				{
					relDocIDs.add(Integer.parseInt(docID));
				}
				this.answers.put(qid, relDocIDs);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns an array of 3 numbers: precision, recall, F1, computed from the top *k* search results
	 * returned from *searcher* for *query*
	 * @param query
	 * @param searcher
	 * @param k
	 * @return
	 */
	public double[] getQueryPRF(Document query, Searcher searcher, int k)
	{
		/*********************** YOUR CODE HERE *************************/
		double[] result = new double[3];
		double precision, recall, f1;

//		System.out.println(query.getTokens());
		List<SearchResult> R = searcher.search(query.getRawText(),k); //Retrieve doc
		// get id from R then store in set

		Set<Integer> ans = answers.get(query.getId()); // G
		Set<Integer> intersect = new HashSet<>();
		for(SearchResult res : R){
			if(ans.contains(res.getDocument().getId())){
				intersect.add(res.getDocument().getId());
			}
		}
		if(intersect.size() == 0){
			result[0] = 0;
			result[1] = 0;
			result[2] = 0;
		}
		else{
			// find size of R intersect G
			precision = (double)intersect.size()/(double)R.size();
			recall = (double)intersect.size()/(double)ans.size();
			f1 = (2*precision*recall)/(precision+recall);
			result[0] = precision;
			result[1] = recall;
			result[2] = f1;

		}

		return result;
		/****************************************************************/
	}

	/**
	 * Test all the queries in *queries*, from the top *k* search results returned by *searcher*
	 * and take the average of the precision, recall, and F1.
	 * @param searcher
	 * @param k
	 * @return
	 */
	public double[] getAveragePRF(Searcher searcher, int k)
	{
		/*********************** YOUR CODE HERE *************************/
		double[] result = new double[3];
		double precision = 0;
		double recall = 0;
		double f1 = 0;
		for(Document q:queries){
			double[] eval = this.getQueryPRF(q, searcher, k);
			precision += eval[0];
			recall += eval[1];
			f1 += eval[2];
		}
		result[0] = precision/queries.size();
		result[1] = recall/queries.size();
		result[2] = f1/queries.size();

		return result;
		/****************************************************************/
	}
}