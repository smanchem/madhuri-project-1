package cs601.project1;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;

public class AmazonSearch {

	public AmazonSearch() {
		reviewList = new HashMap<String, String[]>();
		qaList = new HashMap<String, String[]>();
		asinList = new HashMap<>();
	}

	private HashMap<String, String[]> reviewList;
	private HashMap<String, String[]> qaList;
	private HashMap<String, AsinList> asinList;

	public HashMap<String, String[]> getReviewList(){
		return this.reviewList;
	}
	public HashMap<String, String[]> getQaList(){
		return this.qaList;
	}
	public HashMap<String, AsinList> getAsinList(){
		return this.asinList;
	}
	
	public static void main(String[] args) throws Exception, JsonParseException {

		/*
		 * command line args validation
		 * 
		 */

		String reviews = null, qa = null;
		if(args.length != 4) {
			System.out.println("Incorrect arguments. Please try again.");
			return;
		}
		if ("-reviews".compareTo(args[0]) == 0) {
			reviews = args[1];
		} else if ("-qa".compareTo(args[0]) == 0) {
			qa = args[1];
		}
		if ("-qa".compareTo(args[2]) == 0) {
			qa = args[3];
		} else if ("-reviews".compareTo(args[2]) == 0) {
			reviews = args[3];
		}
		if (reviews == null || qa == null) {
			System.out.println("Incorrect input. Please specifiy review and qa parameters");
		}

		/*
		 * parsing both reviews file
		 * 
		 * ReviewId is to identify each record in the review file
		 * 
		 */
		
		parseFiles(reviews, qa);
		return;

	}
	
	public static void parseFiles(String reviews, String qa) {
		String reviewId = null;
		String qaId = null;
		int count = 0;

		AmazonSearch jsonP = new AmazonSearch();

		JsonParser rpar = new JsonParser();
		JsonParser qapar = new JsonParser();

		InvertedIndexing indexObj = new InvertedIndexing();

		try (BufferedReader reviewreader = Files.newBufferedReader(Paths.get(reviews), Charset.forName("ISO-8859-1"))) {

			String reviewLine;
			while ((reviewLine = reviewreader.readLine()) != null) {
				try {

					JsonElement element = rpar.parse(reviewLine);
					JsonObject object = element.getAsJsonObject();
					JsonElement asinElement = object.get("asin");
					JsonElement reviewTextElement = object.get("reviewText");
					JsonElement reviewIDElement = object.get("reviewerID");
					JsonElement timeElement = object.get("unixReviewTime");
					JsonElement scoreElement = object.get("overall");
					reviewId = (((reviewIDElement.getAsString()).concat(asinElement.getAsString()))
							.concat(timeElement.getAsString())).toLowerCase();

					jsonP.reviewList.put(reviewId, new String[] { (asinElement.getAsString()).toLowerCase(),
							(reviewIDElement.getAsString()).toLowerCase(), (scoreElement.getAsString()).toLowerCase(),
							(reviewTextElement.getAsString()).toLowerCase() });
					String asinNum = asinElement.getAsString().toLowerCase();
					if (jsonP.asinList.containsKey(asinNum)) {
						jsonP.asinList.get(asinNum).updateReviewList(reviewId);

					} else {
						AsinList asinobj = new AsinList();
						asinobj.updateReviewList(reviewId);
						jsonP.asinList.put(asinNum, asinobj);
					}

					count++;
					indexObj.reviewIndex(reviewTextElement.getAsString(), reviewId);
				} catch (JsonParseException e) {
					System.out.println("there is an error in the record");
				}
			}

		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Skipping this Record and continue to Read the file");

		}

		/*
		 * Parsing Question and Answer File qaId is used to identify each record of the
		 * file
		 */

		// count = 0;
		try (BufferedReader qareader = Files.newBufferedReader(Paths.get(qa), Charset.forName("ISO-8859-1"))) {

			String qaline;
			count = 0;

			while ((qaline = qareader.readLine()) != null) {
				try {
					JsonElement element = qapar.parse(qaline);
					JsonObject object = element.getAsJsonObject();
					JsonElement asinElement = object.get("asin");
					JsonElement questionTextElement = object.get("question");
					JsonElement answerTextElement = object.get("answer");
					JsonElement timeElement = object.get("unixTime");
					String asinNum = asinElement.getAsString().toLowerCase();

					if (timeElement == null || timeElement.getAsString().equals(null)) {
						qaId = asinNum.concat("timeisnull");
					} else {
						qaId = asinNum.concat(timeElement.getAsString());
					}

					jsonP.qaList.put(qaId,
							new String[] { (asinElement.getAsString()).toLowerCase(),
									(questionTextElement.getAsString()).toLowerCase(),
									(answerTextElement.getAsString()).toLowerCase() });
					count++;

					if (jsonP.asinList.containsKey(asinNum)) {
						jsonP.asinList.get(asinNum).updateQaIdList(qaId);

					} else {
						AsinList asinobj = new AsinList();
						asinobj.updateQaIdList(qaId);
						jsonP.asinList.put(asinNum, asinobj);
					}
					String tmp = questionTextElement.getAsString().concat(answerTextElement.getAsString());
					indexObj.qaIndex(tmp, qaId);

				} catch (JsonParseException e) {
					System.out.println(e);
				}

			}

		} catch (IOException e) {

			System.out.println("Skipping this Record and continue to Read the file");

		}
		runQueries(indexObj, jsonP);
	}
	
	public static void runQueries(InvertedIndexing indexObj, AmazonSearch jsonP) {
		// Ready to run queries
				UserOptions up = new UserOptions();
				up.template();

				// Accept command line input
				Scanner sc = new Scanner(System.in);
				String command = "";
				while (true) {
					command = sc.nextLine();
					if (command.equals("exit"))
						break;
					String[] queryArgs = command.split(" ");
					if (queryArgs.length != 2) {
						System.out.println("The input is not valid. please try again");
						up.template();
					} else {
						switch (queryArgs[0]) {
						case "find":
							asinSearch(queryArgs[1].toLowerCase(), jsonP);
							up.template();
							break;
						case "reviewsearch":
							indexObj.searchingReviewTerm(queryArgs[1].toLowerCase(), jsonP);
							up.template();
							break;
						case "qasearch":
							indexObj.searchingQATerm(queryArgs[1].toLowerCase(), jsonP);
							up.template();
							break;
						case "reviewpartialsearch":
							indexObj.reviewPatialSearch(queryArgs[1].toLowerCase(), jsonP);
							up.template();
							break;
						case "qapartialsearch":
							indexObj.qaPatialSearch(queryArgs[1].toLowerCase(), jsonP);
							up.template();
							break;
						default:
							System.out.println("Only the following options are available:");
							up.template();
						}
					}

				}
				sc.close();
	}

	public static void asinSearch(String asin, AmazonSearch jsonP) {
		if (jsonP.asinList.containsKey(asin)) {
			jsonP.asinList.get(asin).printList(jsonP);
		} else {
			System.out.println("No reviews or Q&A's with the given asin Id");
		}
	}

}
