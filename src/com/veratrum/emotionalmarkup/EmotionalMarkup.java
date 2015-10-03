package com.veratrum.emotionalmarkup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EmotionalMarkup {
	
	public EmotionalMarkup() {
		
	}
	
	public MarkedText evaluateText(String text) {
		MarkedText markedText = new MarkedText(text);
		analyseText(markedText);
		return markedText;
	}
	
	private void analyseText(MarkedText markedText) {
		int i;
		
		HashMap<String, Integer> dictionary = new HashMap<String, Integer>();

		//URL url = getClass().getResource("./xml/words.xml");
		//File xmlFile = new File(url.getPath());
		File xmlFile = new File("xml/words.xml");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		Document document = null;
		
		try {
			document = documentBuilder.parse(xmlFile);
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		
		document.getDocumentElement().normalize();
		NodeList nodeList = document.getElementsByTagName("w");
		
		for (i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			
			dictionary.put(node.getTextContent(), Integer.parseInt(((Element) node).getAttribute("c")));
		}
		
		String text = markedText.getText();
		String words[] = text.split("\\s+");
		
		int totalWordLength = 0;
		ArrayList<ConnotatedWord> connotatedWords = new ArrayList<ConnotatedWord>();
		for (i = 0; i < words.length; i++) {
			String word = words[i];
			int wordLength = word.length();
			totalWordLength += wordLength;
			if (dictionary.containsKey(word)) {
				connotatedWords.add(new ConnotatedWord(word, dictionary.get(word)));
			} else {
				connotatedWords.add(new ConnotatedWord(word, 0));
			}
		}
		
		markedText.setNumberOfWords(words.length);
		markedText.setTotalWordLength(totalWordLength);
		markedText.setWords(connotatedWords);
	}
}
