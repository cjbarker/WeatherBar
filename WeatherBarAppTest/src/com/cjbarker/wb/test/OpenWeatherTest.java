package com.cjbarker.wb.test;

import java.lang.reflect.Method;

import com.cjbarker.wb.ws.OpenWeather;
import com.cjbarker.wb.ws.Weather.Forecast;

import junit.framework.TestCase;

public class OpenWeatherTest extends TestCase {
 
	private static final String TEST_JSON = "{\"coord\":{\"lon\":-97.74,\"lat\":30.27},\"sys\":{\"message\":0.0088,\"country\":\"United States of America\",\"sunrise\":1388842087,\"sunset\":1388879064},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"Sky is Clear\",\"icon\":\"01n\"}],\"base\":\"cmc stations\",\"main\":{\"temp\":48.94,\"pressure\":1022,\"temp_min\":46,\"temp_max\":52,\"humidity\":44},\"wind\":{\"speed\":1.1,\"gust\":2.57,\"deg\":145},\"clouds\":{\"all\":0},\"dt\":1388794341,\"id\":4671654,\"name\":\"Austin\",\"cod\":200}";
	
	private OpenWeather opWeather;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.opWeather = new OpenWeather();
    }
    
    public void testApiSet() {
    	assertEquals(OpenWeather.API_URL, opWeather.getApiUrl());	
    }
    
    public void testParse() {
		try {
			Class cls = OpenWeather.class;
			Object obj = cls.newInstance();
			
			Class[] paramString = new Class[1];	
			paramString[0] = String.class;
			
			Method method = cls.getDeclaredMethod("parse", paramString);
			method.setAccessible(true);
			Forecast forecast = (Forecast)method.invoke(obj, TEST_JSON);
			
			assertNotNull(forecast);
			assertNotNull(forecast.temperature);
		} 
		catch (Exception e) {
			fail(e.getMessage());
		}
    }
}
