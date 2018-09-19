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
		// TODO Auto-generated constructor stub
		reviewList = new HashMap<String, String[]>();
		qaList = new HashMap<String, String[]>();
		asinList = new HashMap<>();
	}

	public Map<String, String[]> reviewList;
	public Map<String, String[]> qaList;
	public Map<String, AsinList> asinList;

	public static void main(String[] args) throws Exception, JsonParseException {
//		System.out.println(System.nanoTime());
		String reviewId = null;
		String qaId = null;
		int count = 0;

		AmazonSearch jsonP = new AmazonSearch();

		JsonParser rpar = new JsonParser();
		JsonParser qapar = new JsonParser();

		InvertedIndexing indexObj = new InvertedIndexing();

		/*
		 * command line args validation
		 * 
		 */

		String reviews = null, qa = null;

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
//		System.out.println(reviews + " : " + qa);

		/*
		 * parsing both reviews file
		 * 
		 * ReviewId is to identify each record in the review file
		 * 
		 */

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

//			System.out.println("the total no of records = " + count);

//			for (String i : jsonP.reviewList.keySet()) {
//				System.out.println("ReviewId is : " + i + "\n" + "Review Details are : ");
//				for (String s : jsonP.reviewList.get(i)) {
//					System.out.println(s);
//				}
//
//				System.out.println("\n \n");
//			}

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

					// System.out.println(qaId);
					// System.out.println(reviewId);
					// System.out.println(reviewTextElement.getAsString());
					// System.out.println("\n");
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
//					System.out.println(e);
				}

			}
//			System.out.println("the total no of records = " + count);
//			jsonP.asinList.get("120401325x").printinglist();	
//			for (String i : jsonP.qaList.keySet()) {
//				System.out.println("qaId is : " + i + "\n" + "Q&A Details are : ");
//				for (String s : jsonP.qaList.get(i)) {
//					System.out.println(s);
//				}
//
//				System.out.println("\n \n");
//			}

		} catch (IOException e) {

			System.out.println("Skipping this Record and continue to Read the file");

		}
//		System.out.println("the total no of records = " + count);
//		System.out.println(System.nanoTime());

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
			if (queryArgs.length > 2 || queryArgs[1] == null) {
				System.out.println("The i/p is null. please try again");
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
		return;

	}

	public static void asinSearch(String asin, AmazonSearch jsonP) {
		if (jsonP.asinList.containsKey(asin)) {
			jsonP.asinList.get(asin).printList(jsonP);
		} else {
			System.out.println("No reviews or Q&A's with the given asin Id");
		}
	}

}
