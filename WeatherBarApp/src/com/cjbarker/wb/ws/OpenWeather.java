package com.cjbarker.wb.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.apache.http.HttpStatus;

import android.util.Log;

import com.cjbarker.wb.Util;
import com.cjbarker.wb.ws.Weather.Forecast;
import com.cjbarker.wb.ws.Weather.Location;
import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

public class OpenWeather implements Weather {
	
	public static final String BASE_API_URL = "http://api.openweathermap.org/data/2.5/weather?";
	public static final String ATX_API_URL = "http://api.openweathermap.org/data/2.5/weather?q=Austin,Tx&units=imperial";
	private static final String TAG = "OpenWeather";
	
	private String apiUrl;
	
	public OpenWeather() {
		this(ATX_API_URL);
	}
	
	public OpenWeather(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	
	public Forecast getToday(Location loc) {
		if (loc == null) {
			return null;
		}
		
		final String url = buildQueryUrl(loc);
		
		ClientRequest request = new ClientRequest(url);
		ClientResponse response = null;
		String result = null;
		
    	try {
    		response = request.sendRequest(ClientRequest.Method.GET);
    		if (response != null && HttpStatus.SC_OK == response.getResponseCode()) {
    			result = new String(response.getResponseMessage());
    		}
    		else {
    			Log.w(TAG, "Failed to successfullyi get response from URL: " + url);
    			if (response != null) Log.d(TAG, "Http Status Code: " + response.getResponseCode());
    		}
    	}
    	catch (IOException ioe) {
    		Log.e(TAG, "IOException on HTTP request: " + ioe.getMessage());
    	}
  	  		
		Forecast fc = parse(result);
		return fc;
	}
	
	public Forecast getForecast(Location loc, long daytime) {
		if (loc == null || daytime <= 0) {
			return null;
		}
		Forecast fc = new Forecast();
		return fc;
	}
	
	public void setApiUrl(String uri) {
		this.apiUrl = uri;
	}
	
	public String getApiUrl() {
		return BASE_API_URL;
	}
	
	private String buildQueryUrl(Location loc) {
		if (loc == null) {
			return null;
		}
		
		if (this.apiUrl.contains("q=")) {
			return this.apiUrl;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(this.apiUrl + "q=");
		
		if (!Util.isEmpty(loc.city) && !Util.isEmpty(loc.state)) {
			sb.append(loc.city + "," + loc.state);
		}
		else {
			sb.append(loc.latitude + "," + loc.longitude);
		}
		
		// TODO read SharedPrefs and see what units set
		sb.append("&units=imperial");
		
		return sb.toString();
	}
	
	private Forecast parse(String json) {
		if (Util.isEmpty(json)) {
			return null;
		}
		
		Forecast fc = new Forecast();
		
		JsonParserFactory factory=JsonParserFactory.getInstance();
		JSONParser parser=factory.newJsonParser();
		Map jsonData = parser.parseJson(json);
		
		Location loc = new Location();
		Map coord = (Map)jsonData.get("coord");
		loc.latitude = Double.parseDouble( (String)coord.get("lat") );
		loc.longitude = Double.parseDouble( (String)coord.get("lon") );
	
		Sun sun = new Sun();
		Map sys = (Map)jsonData.get("sys");
		int rise = Integer.parseInt( (String)sys.get("sunrise") );
		int set = Integer.parseInt( (String)sys.get("sunset") );
		sun.rise = new Date((long)rise * 1000).toString();
		sun.set = new Date((long)set * 1000).toString();
				
		Map weather = (Map)( (ArrayList)jsonData.get("weather") ).get(0);
		fc.cloudDescp = (String)weather.get("description"); 
		
		Map main = (Map)jsonData.get("main");
		Temperature temp = new Temperature();
		temp.current = Double.parseDouble( (String)main.get("temp") );
		temp.low = Double.parseDouble( (String)main.get("temp_min") );
		temp.hi = Double.parseDouble( (String)main.get("temp_max") );
		temp.unit = Unit.Farenheit;
		fc.humidity = Integer.parseInt( (String)main.get("humidity") );
		
		Map w = (Map)jsonData.get("wind");
		Wind wind = new Wind();
		wind.speed = Double.parseDouble( (String)w.get("speed") );
		wind.gust = Double.parseDouble( (String)w.get("gust") );
		wind.degree = Integer.parseInt( (String)w.get("deg") );

		fc.location = loc;
		fc.sun = sun;
		fc.wind = wind;
		fc.temperature = temp;
		//fc.precipitation;
		return fc;
	}
}
