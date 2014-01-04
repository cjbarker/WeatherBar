package com.cjbarker.wb.ws;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import android.util.Log;

import com.cjbarker.wb.ws.Weather.Forecast;
import com.cjbarker.wb.ws.Weather.Location;
import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

public class OpenWeather implements Weather {
	
	public static final String API_URL = "http://api.openweathermap.org/data/2.5/weather?q=Austin,Tx&units=imperial";
	private static final String TAG = "OpenWeather";
	
	private String apiUrl;
	
	public OpenWeather() {
		this(API_URL);
	}
	
	public OpenWeather(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	
	public Forecast getToday(Location loc) {
		if (loc == null) {
			return null;
		}
		String result = null;			// get from HTTP call
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
		return API_URL;
	}
	
	private Forecast parse(String json) {
		if (json == null || json.trim().equals("")) {
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
