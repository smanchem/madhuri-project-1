package cs601.project1;

import java.util.ArrayList;
import java.util.HashMap;

public class AsinList {
//	private String anyId;
	private ArrayList<String> reviewIdList = new ArrayList<String>();

	private ArrayList<String> qaIdList = new ArrayList<String>();

//	public AsinList(String id) {
//		
//		this.anyId = id;
//	}

	public void updateReviewList(String id) {

		reviewIdList.add(id);

	}

	public void updateQaIdList(String id) {
		qaIdList.add(id);
	}

	public void printList(AmazonSearch jsonP) {

		HashMap<String, String[]> reviewList = jsonP.getReviewList();
		HashMap<String, String[]> qaList = jsonP.getQaList();
		// System.out.println(reviewIdList + "\n");
		if (!(reviewIdList.isEmpty())) {

			for (String reviewId : reviewIdList) {
				if (reviewList.containsKey(reviewId)) {
					String[] review = reviewList.get(reviewId);
					for (int i = 1; i < review.length; i++) {
						System.out.println(review[i]);
					}

				}
			}
		}
		
		if (!(qaIdList.isEmpty())) {

			for (String qaId : qaIdList) {
				if (qaList.containsKey(qaId)) {
					String[] queAndAns = qaList.get(qaId);
					for (int i = 1; i < queAndAns.length; i++) {
						System.out.println(queAndAns[i]);
					}

				}
			}
		}

		System.out.println(qaIdList + "\n");
	}

}
