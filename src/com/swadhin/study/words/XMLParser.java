package com.swadhin.study.words;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.swadhin.study.words.model.Word;

public class XMLParser extends DefaultHandler {

	private Word word;
	private ArrayList<Word> words = new ArrayList<Word>();

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		// super.startElement(uri, localName, qName, attributes);

		if (localName.equals("word")) {
			word = new Word(attributes.getValue("term"), attributes.getValue("meaning"));
//			word.setWord(attributes.getValue("term"));
//			word.setDefinition(attributes.getValue("term"));
			words.add(word);			
		}
	}

	@Override
	public String toString() {
		return word.toString();
	}

	public ArrayList<Word> getWordsList() {
		return words;
	}
	
}
