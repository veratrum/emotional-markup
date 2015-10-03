package com.veratrum.emotionalmarkup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javaFlacEncoder.FLAC_FileEncoder;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.xml.ws.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Main {
	
	private static AudioFormat audioFormat;
	private static TargetDataLine targetDataLine;
	
	public static void main(String[] args) {
		System.out.println("beginning recording");
		recordSound();

		long timerDelay = 5 * 1000;
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				stopRecordingSound();
			}
		}, timerDelay);
	}
	
	private static void analyseSentence(String sentence) {
		/*
		 * Create the POST request
		 */
		
		HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://sentiment.vivekn.com/api/text/");
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("txt", sentence, ContentType.TEXT_PLAIN);
		//builder.addTextBody("txt", "great", ContentType.TEXT_PLAIN);
		HttpEntity multipart = builder.build();
		httpPost.setEntity(multipart);
		
		HttpResponse httpResponse = null;
		String responseString = null;
		try {
			httpResponse = httpClient.execute(httpPost);
			responseString = new BasicResponseHandler().handleResponse(httpResponse);
			System.out.println(responseString);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JSONObject jsonArray = (JSONObject) JSONValue.parse(responseString);
		JSONObject result = (JSONObject) jsonArray.get("result");
		String sentiment = (String) result.get("sentiment");
		String confidence = (String) result.get("confidence");
		
		System.out.println("result: " + sentiment + " with " + confidence + "% confidence");
		
		/*EmotionalMarkup emotionalMarkup = new EmotionalMarkup();
		MarkedText markedText = emotionalMarkup.evaluateText(sentence);

		int numberOfWords = markedText.getNumberOfWords();
		int totalWordLength = markedText.getTotalWordLength();
		int i;
		
		System.out.println(numberOfWords + " words in this sentence of average length " + (totalWordLength / numberOfWords) + ".");
		
		for (i = 0; i < numberOfWords; i++) {
			ConnotatedWord word = markedText.getWords().get(i);
			System.out.println(word.getWord() + " : " + word.getConnotation());
		}*/
	}
	
	private static void recordSound() {
		audioFormat = new AudioFormat(44100.0F, 16, 1, true, false);
		
		DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
		
		try {
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		
		Thread captureThread = new Thread() {
			AudioFileFormat.Type fileType = null;
			File audioFile = null;
			@Override
			public void run() {
				fileType = AudioFileFormat.Type.WAVE; // change to FLAC
				audioFile = new File("temp.wav");
				
				try {
					targetDataLine.open(audioFormat);
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
				
				targetDataLine.start();
				
				try {
					AudioSystem.write(new AudioInputStream(targetDataLine), fileType, audioFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		captureThread.start();
	}
	
	private static void stopRecordingSound() {
		targetDataLine.stop();
		targetDataLine.close();
		System.out.println("recording complete");
		
		String text = ConvertWAVToText.convertWAVToText("temp.wav");
		analyseSentence(text);
	}

}