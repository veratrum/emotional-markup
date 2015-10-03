package com.veratrum.emotionalmarkup;

import java.util.ArrayList;

public class MarkedText {
	private String text;
	private int numberOfWords;
	private int totalWordLength;
	private ArrayList<ConnotatedWord> words;
	
	public MarkedText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public int getNumberOfWords() {
		return numberOfWords;
	}
	
	public int getTotalWordLength() {
		return totalWordLength;
	}
	
	public ArrayList<ConnotatedWord> getWords() {
		return words;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setNumberOfWords(int numberOfWords) {
		this.numberOfWords = numberOfWords;
	}
	
	public void setTotalWordLength(int totalWordLength) {
		this.totalWordLength = totalWordLength;
	}
	
	public void setWords(ArrayList<ConnotatedWord> words) {
		this.words = words;
	}
	
}
