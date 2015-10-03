package com.veratrum.emotionalmarkup;

public class ConnotatedWord {
	private String word;
	private int connotation;
	
	public ConnotatedWord(String word, int connotation) {
		this.word = word;
		this.connotation = connotation;
	}
	
	public String getWord() {
		return word;
	}
	
	public int getConnotation() {
		return connotation;
	}
}
