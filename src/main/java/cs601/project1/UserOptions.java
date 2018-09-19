package cs601.project1;

public class UserOptions {

	public UserOptions() {
		// TODO Auto-generated constructor stub
	}

	public void template() {
		System.out.println("User Option are below : ");
		System.out.println("1. find <asin>" + "\t" + ": Displays a list of reviews and Questions & Answers for the Product "
						+ "\n" + "\t" + "I/P format is find asinId");
		System.out.println("2. reviewsearch <term>" + "\t" + ": Displays a list of reviews that contain the exact word "
				+ "\n" + "\t" + "I/P format is reviewsearch word");
		System.out.println("3. qasearch <term>" + "\t" + ": Displays a list of quesions/Answers that contain the exact word "
				+ "\n" + "\t" + "I/P format is qasearch word");
		System.out.println("4. reviewpartialsearch <term>" + "\t" + ": Displays a list of reviews where any word in the review contains a partial match to the term "
				+ "\n" + "\t" + "I/P format is reviewpartialsearch word");
		System.out.println("5. qapartialsearch <term>" + "\t" + ": Displays a list of reviews where any word in the qn/Ans contains a partial match to the term "
				+ "\n" + "\t" + "I/P format is qapartialsearch word");
		System.out.println("6. exit : to exit the program");
	}
	
	

}
