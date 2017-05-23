package com.ainq.chpl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PassingUnitTests {
	private static final String PROPERTIES_FILE_NAME = "environment.properties";
	private static final String CHPL_API_URL_BEGIN_PROPERTY = "chplApiUrlBegin";
	private static Properties properties;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//load environment.properties file
		properties = new Properties();
		try {
			final InputStream in = PassingUnitTests.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
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

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Calls https://chpl.ahrqstg.org/rest/status to make sure that the CHPL API is available.
	 * No API Key is required.
	 * Expected response is {"status":"OK"}
	 */
	@Test
	public void testChplStatusIsOk() {
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
		assertNotNull(response);
		System.out.println("Response from " + url + ": \n\t" + response.toString());
		assertTrue(response.has("status"));
		JsonElement statusElement = response.get("status");
		assertNotNull(statusElement);
		assertEquals("OK", statusElement.getAsString());
	}
	
	/**
	 * Calls https://chpl.ahrqstg.org/rest/data/certification_editions 
	 * to get the available certification editions in the system. A certification edition
	 * is a year for which a listing on the CHPL is certified.
	 * An API Key is required.
	 * Expected response is [{"id":3,"name":"2015","description":null},{"id":2,"name":"2014","description":null},{"id":1,"name":"2011","description":null}]
	 */
	@Test
	public void testCertificationEditionsApi() {
		assertNotNull(properties.getProperty("apiKey"));
		String url = properties.getProperty(CHPL_API_URL_BEGIN_PROPERTY) +
				properties.getProperty("certificationEditionsApi");
		System.out.println("Making HTTP GET call to " + url + 
				" with API Key " + properties.getProperty("apiKey"));
		
		JsonArray response = null;
		try{
			String jsonResponse = Request.Get(url)
					.version(HttpVersion.HTTP_1_1)
					.addHeader("API-Key", properties.getProperty("apiKey"))
					.execute().returnContent().asString();
			response = new Gson().fromJson(jsonResponse, JsonArray.class);
		} catch (IOException e){
			System.err.println("Failed to make call to " + url);
			System.err.println("Please check that the " + CHPL_API_URL_BEGIN_PROPERTY + 
					" and certificationEditionsApi properties are configured correctly in " +
					PROPERTIES_FILE_NAME);
		}
		assertNotNull(response);
		System.out.println("Response from " + url + ": \n\t" + response.toString());
		assertEquals(3, response.size());
		for(int i = 0; i < response.size(); i++) {
			JsonObject certificationEditionObject = response.get(i).getAsJsonObject();
			assertNotNull(certificationEditionObject);
			assertTrue(certificationEditionObject.has("id"));
			int id = certificationEditionObject.get("id").getAsInt();
			String name = certificationEditionObject.get("name").getAsString();
			switch(id) {
			case 1:
				assertEquals("2011", name);
				break;
			case 2:
				assertEquals("2014", name);
				break;
			case 3:
				assertEquals("2015", name);
				break;
			default:
				fail("Unknown certification edition id found: " + id);
			}
		}
	}

}
