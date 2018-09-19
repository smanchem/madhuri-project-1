package cs601.project1;

import java.util.*;

public class InvertedIndexing {
	private String lineText;
	private String idNum;
	private String reviewTerm;
	private String qaTerm;
	private Map<String, HashMap<String, Integer>> reviewIndexList = new HashMap<String, HashMap<String, Integer>>();
	private Map<String, HashMap<String, Integer>> qaIndexList = new HashMap<String, HashMap<String, Integer>>();
	// protected int idCount = 0;

	public InvertedIndexing() {
		// TODO Auto-generated constructor stub
	}

	public void reviewIndex(String reviewText, String reviewId) {

		this.lineText = reviewText;
		this.idNum = reviewId;

		String[] textSplit = lineText.split(" ");

		for (int i = 0; i < textSplit.length; i++) {
			String a = textSplit[i];
			a = (a.replaceAll("\\p{Punct}", "")).toLowerCase();
			textSplit[i] = a;

			if (reviewIndexList.containsKey(textSplit[i])) {
				HashMap<String, Integer> listOfReviews = reviewIndexList.get(textSplit[i]);
				if (listOfReviews.containsKey(idNum)) {
					listOfReviews.put(idNum, listOfReviews.get(idNum) + 1);
				} else {
					listOfReviews.put(idNum, 1);
				}
			} else {
				HashMap<String, Integer> tmp = new HashMap<>();
				tmp.put(idNum, 1);
				reviewIndexList.put(textSplit[i], tmp);
			}
		}

	}

	public void qaIndex(String qnOrAns, String qaId) {
		this.lineText = qnOrAns;
		this.idNum = qaId;

		String[] textSplit = lineText.split(" ");

		for (int i = 0; i < textSplit.length; i++) {
			String a = textSplit[i];
			a = (a.replaceAll("\\p{Punct}", "")).toLowerCase();
			textSplit[i] = a;

			if (qaIndexList.containsKey(textSplit[i])) {
				HashMap<String, Integer> listOfQnA = qaIndexList.get(textSplit[i]);
				if (listOfQnA.containsKey(idNum)) {
					listOfQnA.put(idNum, listOfQnA.get(idNum) + 1);
				} else {
					listOfQnA.put(idNum, 1);
				}
			} else {
				HashMap<String, Integer> tmp = new HashMap<>();
				tmp.put(idNum, 1);
				qaIndexList.put(textSplit[i], tmp);
			}
		}
	}

	public void searchingReviewTerm(String term, AmazonSearch jsop) {
		this.reviewTerm = term.toLowerCase();
		if (reviewTerm == null) {
			System.out.println("The term is null. There are no such terms");
		} else {
			if (reviewIndexList.containsKey(reviewTerm)) {
				HashMap<String, Integer> searchList = reviewIndexList.get(reviewTerm);
				List<Map.Entry<String, Integer>> listOfReviewsAndCount = new LinkedList<Map.Entry<String, Integer>>(
						searchList.entrySet());
				Collections.sort(listOfReviewsAndCount, new CompareSearchListEntry());
				// listOfReviewsAndCount is sorted in descending order by count
				for (Map.Entry<String, Integer> en : listOfReviewsAndCount) {

					String[] reviewdetails = jsop.reviewList.get(en.getKey());
					for (String s : reviewdetails) {
						System.out.println(s);
					}

				}
			} else {
				System.out.println("The given Term doesnot exist in Reviews");
			}
		}

	}

	public void searchingQATerm(String qterm, AmazonSearch jsonp) {
		this.qaTerm = qterm.toLowerCase();
		if (qaTerm == null) {
			System.out.println("The term is null. There are no such terms");
		} else {
			if (qaIndexList.containsKey(qaTerm)) {
				HashMap<String, Integer> searchList = qaIndexList.get(qaTerm);
				List<Map.Entry<String, Integer>> listOfQaAndCount = new LinkedList<Map.Entry<String, Integer>>(
						searchList.entrySet());
				Collections.sort(listOfQaAndCount, new CompareSearchListEntry());
				// listOfReviewsAndCount is sorted in descending order by count
				for (Map.Entry<String, Integer> en : listOfQaAndCount) {

					String[] qaDetails = jsonp.qaList.get(en.getKey());
					for (String s : qaDetails) {
						System.out.println(s);
					}

				}
			} else {
				System.out.println("The given Term doesnot exist in Questions and Answers");
			}
		}

	}

	public void reviewPatialSearch(String partialTerm, AmazonSearch jp) {
		if (partialTerm == null) {
			System.out.println("The term is null. There are no such terms");
		} else {
			for (Map.Entry<String, HashMap<String, Integer>> en : reviewIndexList.entrySet()) {
				if (en.getKey().indexOf(partialTerm) == -1) {
					continue;
				} else {
					HashMap<String, Integer> searchList = reviewIndexList.get(en.getKey());
					List<Map.Entry<String, Integer>> listOfReviewsAndCount = new LinkedList<Map.Entry<String, Integer>>(
							searchList.entrySet());
					Collections.sort(listOfReviewsAndCount, new CompareSearchListEntry());
					// listOfReviewsAndCount is sorted in descending order by count
					for (Map.Entry<String, Integer> pen : listOfReviewsAndCount) {

						String[] reviewdetails = jp.reviewList.get(pen.getKey());
						for (String s : reviewdetails) {
							System.out.println(s);
						}

					}

				}
			}
		}
	}

	public void qaPatialSearch(String partTerm, AmazonSearch jpar) {
		if (partTerm == null) {
			System.out.println("The term is null. There are no such terms");
		} else {
			for (Map.Entry<String, HashMap<String, Integer>> en : qaIndexList.entrySet()) {
				if (en.getKey().indexOf(partTerm) == -1) {
					continue;
				} else {
					HashMap<String, Integer> searchList = qaIndexList.get(en.getKey());
					List<Map.Entry<String, Integer>> listOfQAAndCount = new LinkedList<Map.Entry<String, Integer>>(
							searchList.entrySet());
					Collections.sort(listOfQAAndCount, new CompareSearchListEntry());
					// listOfReviewsAndCount is sorted in descending order by count
					for (Map.Entry<String, Integer> pen : listOfQAAndCount) {

						String[] qadetails = jpar.qaList.get(pen.getKey());
						for (String s : qadetails) {
							System.out.println(s);
						}

					}

				}
			}
		}
	}

}

/*
 * https://www.geeksforgeeks.org/sorting-a-hashmap-according-to-values/ Learnt
 * to implement comparator from above website
 */
class CompareSearchListEntry implements Comparator<Map.Entry<String, Integer>> {
	public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
		return (-1 * e1.getValue().compareTo(e2.getValue()));
	}

}
