package com.cjbarker.wb.ws;

import com.cjbarker.wb.Util;

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
		
		public String toString() {
			return "Current " + current + "\n" + 
					"High " + hi + "\n" + 
					"Low " + low;
		}
	}
	
	public class Location {
		public double latitude;
		public double longitude;
		public String city;
		public String state;
		
		public Location() {}
		public Location(double lat, double lon) {
			this.latitude = lat;
			this.longitude = lon;
		}
		public Location(String city, String state) {
			this.city = city;
			this.state = state;
		}
		public String toString() {
			if (Util.isEmpty(city) || Util.isEmpty(state)) {
				return "Lat " + latitude + " Lon " + longitude;
			}
			else {
				return city + ", " + state;
			}
		}
	}
	
	public class Sun {
		public String rise;
		public String set;
		public int timeZone;
		
		public String toString() {
			return "Rise " + rise + "\n" + 
					"Set " + set;
		}
	}
	
	public class Wind {
		public double speed;
		public double gust;
		public int degree;
		
		public String toString() {
			return "Winds at " + speed + " with Gusts up to " + gust;
		}
	}
	
	public class Forecast {
		public Location location;
		public Sun sun;
		public Temperature temperature;
		public Wind wind;
		public String cloudDescp;
		public int precipitation;
		public int humidity;
		
		public String toString() {
			return location.toString() + sun.toString() + temperature.toString() + 
					wind.toString() + "\n" + cloudDescp + "\n" +
					"Precp " + precipitation + "%\n" + 
					" Humidity" + humidity + "%";
		}
	}

	public Forecast getToday(Location loc);
	public Forecast getForecast(Location loc, long daytime);
	public String getApiUrl();
}
