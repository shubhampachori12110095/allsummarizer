package aak.as.confs.multiling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import aak.as.preProcess.PreProcessor;
import aak.as.process.extraction.Summarizer;
import aak.as.process.extraction.bayes.Feature;
import aak.as.process.extraction.cluster.Cluster;
import aak.as.process.extraction.cluster.NaiveCluster;
import aak.as.tools.Data;
import aak.as.tools.Tools;


public class MSS {
	
	private List<Feature> features = new ArrayList<Feature>();
	private String lang = "en";
	
	private Data data;
	private boolean clustered = false;
	private PreProcessor preprocessor;
	
	public MSS (String lang){
		this.lang = (lang.length()==2)?lang:"en";
		clustered = false;
		data = new Data();
		preprocessor = new PreProcessor(this.lang, this.data);
	}

	public void preprocess(File file){
		String text = readFile(file);
		preprocessor.preProcess(text);
		clustered = false;
	}
	
	public List<Double> getSimilarity(){
		return data.getSimList();
	}
	
	public int sentNum (){
		return data.getSentNumber();
	}
	
	public double getTermDistribution(){
		List<List<String>> sentWords = data.getSentWords();
		return Tools.termsDistribution(sentWords);
	}
	
	public void cluster(double threshold){
		Cluster cluster = new NaiveCluster(threshold, data);
		cluster.createClasses();
		clustered = true;
	}
	
	
	/**
	 * Use a feature for scoring
	 * @param feature
	 */
	public void addFeature(Feature feature){
		if (!features.contains(feature))
			features.add(feature);
	}
	
	public String summarize(int summarySize, double simTH) throws Exception{
		
		if(features.size() <1 ) throw new Exception("add at least one feature");
		if (! clustered ) throw new Exception("Use cluster before summarize");
		
		Summarizer summarizer = new Summarizer();
		
		for (Feature feature: features)
			summarizer.addFeature(feature);
		
		summarizer.summarize(data);
		
		return getSummary(data, summarizer.getOrdered(), summarySize, simTH);
	}
	
	/**
	 * Clear the used features if there is any
	 */
	public void clearFeatures(){
		features.clear();
	}


	private String readFile(File file){
		StringBuffer content = new StringBuffer();

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			while ( (line = in.readLine()) != null) {
				if (line.startsWith("["))
					continue;
				content.append(line + " ");
			}
			in.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return "";
		}

		return content.toString();
	}
	
	
	private static String getSummary(Data data, List<Integer> order, int summarySize, double simTH){

		List<String> sentences = data.getSentences();
		List<List<String>> sentWords = data.getSentWords();

		/*
		String summary = sentences.get(order.get(0)) + "\n";
		int index = 1;

		while (index < order.size() && summary.length() < summarySize) {
			
			int sentPos = order.get(index);
			
			List<String> prevWords = sentWords.get(order.get(index-1));
			List<String> actWords = sentWords.get(index);

			if (Tools.similar(prevWords, actWords, simTH))
				summary += sentences.get(sentPos) + "\n";
			index++;
		};*/
		
		String summary = "";
		int numChars = 0;
		int numOrder = 0;

		while(true){

			if (numOrder >=  order.size())
				break;

			int index = order.get(numOrder);

			if (numOrder > 0){
				List<String> prevWords = sentWords.get(order.get(numOrder-1));
				List<String> actWords = sentWords.get(index);
				if (Tools.similar(prevWords, actWords, simTH)){
					numOrder ++;
					if (numOrder < order.size())
						index = order.get(numOrder);
				}
			}

			numChars += sentences.get(index).length();


			if (numChars > summarySize)
				break;
			
			summary += sentences.get(index) + "\n";

			numOrder ++;
		}

		return summary;
	}


}