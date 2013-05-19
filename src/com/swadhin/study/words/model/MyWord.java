package com.swadhin.study.words.model;

public class MyWord {

	private String word;
	private int myRating;

	public MyWord() {
	}

	public MyWord(String word, int myRating) {
		this.word = word;
		this.myRating = myRating;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getMyRating() {
		return myRating;
	}

	public void setMyRating(int myRating) {
		this.myRating = myRating;
	}

	@Override
	public String toString() {
		return "MyWord [word=" + word + ", myRating=" + myRating + "]";
	}

}
