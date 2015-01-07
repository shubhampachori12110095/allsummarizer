package aak.as.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import aak.as.preProcess.PreProcessor;
import aak.as.process.extraction.bayes.Feature;
import aak.as.process.extraction.bayes.PLeng;
import aak.as.process.extraction.bayes.Pos;
import aak.as.process.extraction.bayes.RLeng;
import aak.as.process.extraction.bayes.TFB;
import aak.as.process.extraction.bayes.TFU;
import aak.as.tools.Calculus;
import aak.as.tools.Data;
import aak.as.tools.FileManager;

public class Cmplg {

	private static String cmplgfolder = "cmplg/src/";
	private static String peerfolder = "cmplg/peer/";
	private static String linefolder = "cmplg/line/";
	private static String statsfolder = "cmplg/stats/";
	private static Feature[] features = { new TFU(), new TFB(), new Pos(),
			new RLeng(), new PLeng(), };

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		File dir = new File(cmplgfolder);
		File[] files = dir.listFiles();
		String statContent = "doc,#sent,mean,median,variance,hmode,skewness\n";
		for (File file : files) {

			System.out.println("file: " + file.getName());

			Data data = new Data();
			{
				PreProcessor preprocess = new PreProcessor("en", data);
				String text = FileManager.readFile(file);
				preprocess.preProcess(text);
			}

			/*
			 * //This is used to generate the files with each sentence in a line
			 * List<String> ls = data.getSentences(); String ff = ""; for(String
			 * s : ls) ff = ff + s + System.lineSeparator(); try {
			 * FileManager.saveFile(linefolder + file.getName(), ff); } catch
			 * (IOException e) { e.printStackTrace(); }
			 */

			// This is used to store statistics
			{
				{
					int dot = file.getName().lastIndexOf('.');
					String filename = file.getName().substring(0, dot);
					statContent += filename + ",";
				}
				statContent += data.getSentNumber() + ",";
				List<Double> sim = Calculus.delMultiple(data.getSimList(), 0.0);
				statContent += Calculus.mean(sim) + ",";// mean
				statContent += Calculus.median(sim) + ",";// median
				statContent += Calculus.variance(sim) + ",";// variance
				statContent += Calculus.modeHigh(sim) + ","; // Hmode
				statContent += Calculus.skewness(sim) + "\n";// skewness

			}

			/*
			 * for(int th = 85; th <= 100; th++){
			 * 
			 * { double Threshold = (double) th / 100;
			 * System.out.println("threshold: " + Threshold); Cluster cluster =
			 * new NaiveCluster(Threshold, data); cluster.createClasses(); }
			 * 
			 * for (int combNbr=1; combNbr <= features.length; combNbr++){
			 * List<List<Integer>> comb =
			 * Calculus.getCombinations(features.length, combNbr);
			 * 
			 * for (List<Integer> oneComb : comb){ Summarizer summarizer = new
			 * Summarizer(); String combStr = ""; for (int index: oneComb){
			 * summarizer.addFeature(features[index]); combStr +=
			 * features[index].getClass().getSimpleName() + "-"; } combStr =
			 * combStr.substring(0, combStr.length()-1);
			 * 
			 * //System.out.println("features: " + combStr);
			 * 
			 * summarizer.summarize(data); List<Integer> order =
			 * summarizer.getSentNumber(5); String summary = ""; for (int i=0;
			 * i< 5; i++) summary += data.getSentence(order.get(i)) + "\n";
			 * 
			 * combStr += "/"; int dot = file.getName().lastIndexOf('.');
			 * combStr += file.getName().substring(0, dot) + "/"; combStr += th
			 * + ".asz"; try { FileManager.saveFile(peerfolder + combStr,
			 * summary); } catch (IOException e) { e.printStackTrace(); }
			 * 
			 * 
			 * } }// combinations }//threshold
			 */
			System.out.println("......................");
		}// files

		// comb: combination number: 1 combination, 2 cimbinations, etc.
		/*
		 * for (int comb=1; comb <= features.length; comb++){
		 * 
		 * }
		 */

		// TFU t = new TFU();

		// save statistics
		try {
			FileManager.saveFile(statsfolder + "stats.csv", statContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
