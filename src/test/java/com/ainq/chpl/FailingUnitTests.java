package com.ainq.chpl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class FailingUnitTests {
	private static ChplApiWrapper api;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		api = ChplApiWrapper.getInstance();
	}

	/**
	 * Get the CHPL application status from the API Wrapper.
	 * The value expected to be returned from the API Wrapper is "OK".
	 */
	@Test
	public void testChplStatusIsOk() {
		String apiStatus = api.getChplStatus();
		assertEquals("OK", apiStatus);
	}
	
	/**
	 * Get the list of sorted education levels from the API Wrapper.
	 */
	@Test
	public void testGetSortedEducationLevels() {
		List<String> educationLevels = api.getSortedEducationLevelNames();
		assertEquals(8, educationLevels.size());
		
		//are they sorted?
		for (int i = 0; i < educationLevels.size() - 1; i++) {
			String curr = educationLevels.get(i);
			String next = educationLevels.get(i+1);
	        if (curr.compareToIgnoreCase(next) > 0) {
	            fail("The education levels are not sorted because " + curr + " and " + next + " are out of order.");
	        }
	    }
	}
	
	/**
	 * Get the practice type names from the API wrapper.
	 * Expected Ambulatory, Inpatient
	 */
	@Test
	public void testGetPraticeTypeNames() {
		List<String> practiceTypes = api.getPracticeTypeNames();
		assertEquals(2, practiceTypes.size());
		
		boolean foundAmbulatory = false;
		boolean foundInpatient = false;
		for(String practiceType : practiceTypes) {
			switch(practiceType.toUpperCase()) {
			case "AMBULATORY":
				foundAmbulatory = true;
				break;
			case "INPATIENT":
				foundInpatient = true;
				break;
			default:
				fail("Unexpected practice type found: " + practiceType);
			}
		}
		assertTrue(foundAmbulatory);
		assertTrue(foundInpatient);
	}
}
