package com.cjbarker.wb.ws;

import com.cjbarker.wb.ws.Weather.Forecast;
import com.cjbarker.wb.ws.Weather.Location;

import junit.framework.TestCase;

public class WeatherTest extends TestCase {
	
	private static final Location LOC_AUSTIN = new Location("Austin", "TX");
	
	private Weather weather;
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        this.weather = new OpenWeather();
    }
    
    public void testGetToday() {
    	Forecast fc = weather.getToday(LOC_AUSTIN);
    	assertNotNull(fc);
    	assertNotNull(fc.cloudDescp);
    	assertNotNull(fc.location);
    	assertNotNull(fc.sun);
    	assertNotNull(fc.temperature);
    	assertNotNull(fc.wind);
    	assertTrue(fc.toString() != null && !fc.toString().trim().equals(""));
    }
}
