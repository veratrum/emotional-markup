package com.veratrum.emotionalmarkup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javaFlacEncoder.FLAC_FileEncoder;

public class ConvertWAVToText {
	public static String convertWAVToText(String inputFile) {
		convertWAVToFLAC(inputFile, "temp.flac");
		String text;
		text = convertFLACToText("temp.flac");
		return text;
	}
	
	private static void convertWAVToFLAC(String inputFile, String outputFile) {
		FLAC_FileEncoder flacEncoder = new FLAC_FileEncoder();
		File input = new File(inputFile);
		File output = new File(outputFile);
		flacEncoder.encode(input, output);
		
		System.out.println(inputFile + " converted to " + outputFile);
	}
	
	private static String convertFLACToText(String inputFile) {
		URL url = null;

		try {
			url = new URL("https://www.google.com/speech-api/v2/recognize?xjerr=1&client=chromium&lang=en-US&key=AIzaSyDBEq7IaMwB_AF8TuCIv6DZjxFzwqiwXGc");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		HttpURLConnection connection = null;
		
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			connection.setRequestMethod("POST");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}

		//connection.setRequestProperty("Content-Type", "audio/x-flac; rate=16000");
		connection.setRequestProperty("Content-Type", "audio/x-flac; rate=44100");
		connection.setUseCaches (false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		//Path path = Paths.get("cryonics.flac");
		Path path = Paths.get(inputFile);
		byte[] audioFile = null;
		try {
			audioFile = Files.readAllBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		DataOutputStream dataOutputStream = null;
		String output = "";
		try {
			dataOutputStream = new DataOutputStream(connection.getOutputStream());
			dataOutputStream.write(audioFile);
			dataOutputStream.flush();
			dataOutputStream.close();
			
			InputStream inputStream = connection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line;

			line = bufferedReader.readLine();
			line = bufferedReader.readLine();
			
			bufferedReader.close();
			System.out.println("response: " + line);
			output = line;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String text = null;
		if (output != null) {
			JSONObject jsonArray = (JSONObject) JSONValue.parse(output);
			JSONArray result = (JSONArray) jsonArray.get("result");
			JSONObject alternatives = (JSONObject) result.get(0);
			JSONObject transcript = (JSONObject) ((JSONArray) alternatives.get("alternative")).get(0);
			text = (String) transcript.get("transcript");
		}
		
		return text;
		//analyseSentence("This is a good bad terrible great test.");
	}
}
