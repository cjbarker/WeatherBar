package com.cjbarker.wb.ws;

public interface Weather {
	
	public enum Unit {
		Celcius,
		Farenheit;
	}
	
	public class Temperature {
		public double hi;
		public double low;
		public double current;
		public Unit unit;
	}
	
	public class Location {
		public double latitude;
		public double longitude;
	}
	
	public class Sun {
		public String rise;
		public String set;
		public int timeZone;
	}
	
	public class Wind {
		public double speed;
		public double gust;
		public int degree;
	}
	
	public class Forecast {
		public Location location;
		public Sun sun;
		public Temperature temperature;
		public Wind wind;
		public String cloudDescp;
		public int precipitation;
		public int humidity;
	}

	public Forecast getToday(Location loc);
	public Forecast getForecast(Location loc, long daytime);
	public String getApiUrl();
}
