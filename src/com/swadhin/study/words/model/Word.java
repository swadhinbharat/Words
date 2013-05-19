package com.swadhin.study.words.model;

public class Word {
	
	public Word(String word, String definition) {
		super();
		this.word = word;
		this.definition = definition;
	}

	private String word;
	private String definition;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	@Override
	public String toString() {
		return "Word [word=" + word + ", definition=" + definition + "]";
	}	
}
