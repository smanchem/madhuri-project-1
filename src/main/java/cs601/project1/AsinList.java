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
//		// TODO Auto-generated constructor stub
//	}

	public void updateReviewList(String id) {

		reviewIdList.add(id);

	}

	public void updateQaIdList(String id) {
		qaIdList.add(id);
	}

	public void printList(AmazonSearch jsonP) {

		// System.out.println(reviewIdList + "\n");
		if (!(reviewIdList.isEmpty())) {

			for (String reviewId : reviewIdList) {
				if (jsonP.reviewList.containsKey(reviewId)) {
					String[] review = jsonP.reviewList.get(reviewId);
					for (int i = 1; i < review.length; i++) {
						System.out.println(review[i]);
					}

				}
			}
		}
		
		if (!(qaIdList.isEmpty())) {

			for (String qaId : qaIdList) {
				if (jsonP.qaList.containsKey(qaId)) {
					String[] queAndAns = jsonP.qaList.get(qaId);
					for (int i = 1; i < queAndAns.length; i++) {
						System.out.println(queAndAns[i]);
					}

				}
			}
		}

		System.out.println(qaIdList + "\n");
	}

}
