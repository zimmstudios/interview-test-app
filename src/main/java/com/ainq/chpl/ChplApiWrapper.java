package com.ainq.chpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.Iterator;

public class ChplApiWrapper {
	private static final String PROPERTIES_FILE_NAME = "environment.properties";
	private static final String CHPL_API_URL_BEGIN_PROPERTY = "chplApiUrlBegin";
	private static ChplApiWrapper instance = null;
	private Properties properties;

	private ChplApiWrapper() {
		//load environment.properties file
		properties = new Properties();
		try {
			final InputStream in = ChplApiWrapper.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
			properties.load(in);
			in.close();
			System.out.println("Loaded " + properties.size() + " properties from file " + PROPERTIES_FILE_NAME);
			for(Object property : properties.keySet()) {
				String key = (String) property;
				properties.setProperty(key, properties.getProperty(key).trim());
				System.out.println("\t" + key + "=" + properties.getProperty(key));
			}
		}
		catch (IOException ex) {
			System.err.println("Could not read properties from file " + PROPERTIES_FILE_NAME);
		}
	}
	
	public static ChplApiWrapper getInstance() {
		if(instance == null) {
			instance = new ChplApiWrapper();
		} 
		return instance;
	}
	
	/**
	 * Get the status of the CHPL API. Most of the time this should return "OK".
	 * If the CHPL API is not available, an HTTP error code may be returned
	 * from the API or simply no response if the server itself is down.
	 * @return
	 */
	public String getChplStatus() {
		String url = properties.getProperty(CHPL_API_URL_BEGIN_PROPERTY) +
				properties.getProperty("statusApi");
		System.out.println("Making HTTP GET call to " + url);
		
		JsonObject response = null;
		try{
			String jsonResponse = Request.Get(url)
					.version(HttpVersion.HTTP_1_1)
					.execute().returnContent().asString();
			response = new Gson().fromJson(jsonResponse, JsonObject.class);
		} catch (IOException e){
			System.err.println("Failed to make call to " + url);
			System.err.println("Please check that the " + CHPL_API_URL_BEGIN_PROPERTY + 
					" and statusApi properties are configured correctly in " +
					PROPERTIES_FILE_NAME);
		}
		if (response == null) {
                    System.out.println("Response from " + url + "was null.");
                    return null;
                } else {
                    System.out.println("Response from " + url + ": \n\t" + response.toString());
                    return response.getAsJsonPrimitive("status").getAsString();
                }
	}
	
	
	/**
	 * Query the CHPL API to get a list of the education levels.
	 * @return A list of certification body names.
	 */
	public List<String> getEducationLevelNames() {
		String url = properties.getProperty(CHPL_API_URL_BEGIN_PROPERTY) +
				properties.getProperty("educationTypesApi");
		System.out.println("Making HTTP GET call to " + url + 
				" with API Key " + properties.getProperty("apiKey"));
		
		JsonObject response = null;
		try{
			String jsonResponse = Request.Get(url)
					.version(HttpVersion.HTTP_1_1)
					.addHeader("API-Key", properties.getProperty("apiKey"))
					.execute().returnContent().asString();
			response = new Gson().fromJson(jsonResponse, JsonObject.class);
		} catch (IOException e){
			System.err.println("Failed to make call to " + url);
			System.err.println("Please check that the " + CHPL_API_URL_BEGIN_PROPERTY + 
					" and educationTypesApi properties are configured correctly in " +
					PROPERTIES_FILE_NAME);
		}
		System.out.println("Response from " + url + ": \n\t" + response.toString());
		JsonArray dataArray = response.getAsJsonArray("data");
		List<String> result = new ArrayList<String>(dataArray.size());
		for(int i = 0; i < dataArray.size(); i++) {
			JsonObject educationObj = dataArray.get(i).getAsJsonObject();
			String educationName = educationObj.get("name").getAsString();
			result.add(educationName);
		}
		
		return result;
	}
	
	/**
	 * This method should return a list of the education level names
	 * sorted alphabetically (A -> Z).
	 * @return A sorted list of education types.
	 */
	public List<String> getSortedEducationLevelNames() {
		List<String> educationLevelNames = getEducationLevelNames();
                
                Collections.sort(educationLevelNames);
                
                return educationLevelNames;
	}
	
	/**
	 * This method should call https://chpl.ahrqstg.org/rest/data/practice_types
	 * and parse the "name" field from each element to get a list of
	 * practice type names.
	 * @return A list of practice type names or an empty list if there was
         *         no response from the CHPL API.
	 */
	public List<String> getPracticeTypeNames() {
		String url = properties.getProperty(CHPL_API_URL_BEGIN_PROPERTY) +
				properties.getProperty("practiceTypesApi");
		System.out.println("Making HTTP GET call to " + url +
				" with API Key " + properties.getProperty("apiKey"));
                
                JsonArray response = null;
		try {
			String jsonResponse = Request.Get(url)
					.version(HttpVersion.HTTP_1_1)
					.addHeader("API-Key", properties.getProperty("apiKey"))
					.execute().returnContent().asString();
                        
			response = new Gson().fromJson(jsonResponse, JsonArray.class);
		} catch (IOException e){
			System.err.println("Failed to make call to " + url);
			System.err.println("Please check that the " + CHPL_API_URL_BEGIN_PROPERTY +
					" and practiceTypesApi properties are configured correctly in " +
					PROPERTIES_FILE_NAME);
                        System.err.println(e);
		}
                
                List<String> practiceTypeNameList = new ArrayList<>();
                
                if (response == null) {
                    System.out.println("Response from " + url + "was null.");
                } else {
                    System.out.println("Response from " + url + ": \n\t" + response.toString());
                    
                    Iterator<JsonElement> iterator = response.iterator();
                    while (iterator.hasNext()) {
                        JsonObject jsonPracticeType = iterator.next().getAsJsonObject();
                        practiceTypeNameList.add(jsonPracticeType.getAsJsonPrimitive("name").getAsString());
                    }
                }
                
		return practiceTypeNameList;
	}
}
