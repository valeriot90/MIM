package it.unipi.ing.mim.main;

import java.io.File;

import com.github.cliftonlabs.json_simple.JsonObject;

import it.unipi.ing.mim.deep.tools.Output;
import it.unipi.ing.mim.img.elasticsearch.ElasticImgIndexing;
import it.unipi.ing.mim.img.elasticsearch.ElasticImgSearching;
import it.unipi.ing.mim.utils.MetadataRetriever;
import it.unipi.ing.mim.utils.Statistics;

public class Main {
	public static boolean showMatchWindow = false;
	private static boolean bestMatchFound = false;
	
	public static void main(String[] args) {
		if (args.length < 2) printHelp();
		else {
		    String indexName = "-i".equals(args[args.length - 2]) ? args[args.length - 1] :
		                                                            Parameters.INDEX_NAME;
		    
		    
			try {
				switch (args[0]) {
				    case "search":
				        ElasticImgSearching eis = 
				                        new ElasticImgSearching(Parameters.TOP_K_QUERY, indexName);
				        String bestGoodMatch = eis.search(args[1]);
				        eis.close();
				        
				        if (bestGoodMatch != null) {
				        	bestMatchFound=true;
				            JsonObject metadata = MetadataRetriever.readJsonFile(bestGoodMatch);
			                String qryImagePath = new File(args[1]).toURI().toString();
			                String bestMatchPath = new File(bestGoodMatch).toURI().toString();
			                Output.toHTML(metadata, qryImagePath, bestMatchPath, Parameters.RESULTS_HTML);
				        }
				        else {
				        	bestMatchFound=false;
				        }
				        break;


				    case "index":
				        ElasticImgIndexing eii = 
			                            new ElasticImgIndexing(Parameters.TOP_K_IDX, indexName);
				        eii.indexAll(args[1]);
				        eii.close();
				        break;
				        
				    case "statistics":
				        if (args.length < 5 && !("-tp".equals(args[1]) && "-tn".equals(args[3]))) 
				            printHelp();
				        else {
				            String[] fileNames = {"statistic.txt", 
				                                  "ransac_parameters.csv", 
				                                  "test_set.csv",
				                                  args[2],
				                                  args[4]
		                                          };
				            Statistics.run(fileNames, indexName);
				        }
				        break;

				    default:
				        printHelp();
				        break;
				}
				System.out.println("Program ended");
			} 
			catch (Exception e) {
				System.err.println("Program generated an exception: " + e.getClass().getName() 
						+ ":\n" + e.getMessage() + "\nExiting");
				System.exit(1);
			}
		}
	}
	
	private static void printHelp() {
		System.out.println("Available commands:");
		System.out.println("search path/to/image [-i index_name]");
		System.out.println("\t\tAsk the program to retrieve");
		System.out.println("\t\tthe most similar image present");
		System.out.println("\t\tinto the index called index_name");
		System.out.println("\t\tinstead of default one (" + Parameters.INDEX_NAME +")");
		System.out.println();
		System.out.println("index dir [-i index_name]");
		System.out.println("\t\tStart indexing each image into dir");
		System.out.println("\t\tOptionally you can tell the program");
		System.out.println("\t\tto index objects using index_name");
		System.out.println("\t\tinstead of default one (" + Parameters.INDEX_NAME +")");
		System.out.println("\t\tdir must contain subdirectories that");
		System.out.println("\t\tcontains images to index");
		System.out.println();
		System.out.println("statistics -tp TPdir -tn TNdir [-i index_name]");
		System.out.println("\t\tStart gathering statistics using");
		System.out.println("\t\timages in TPdir to compute true");
		System.out.println("\t\tpositives and images in TNdir for");
		System.out.println("\t\tcomputing true negative. Optionally");
		System.out.println("\t\tthe user can use index_name index");
		System.out.println("\t\tinstead of default one (" + Parameters.INDEX_NAME +")");
	}

	public static void test() {
		System.out.println("INDEXING FROM GUI");
	}

	public static boolean bestMatchFound() {
		return bestMatchFound;
	}
	
}
