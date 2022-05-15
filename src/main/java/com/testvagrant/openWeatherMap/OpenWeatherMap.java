package com.testvagrant.openWeatherMap;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.testvagrant.reusableComponents.AutomationMethods;

import io.restassured.response.Response;

public class OpenWeatherMap extends AutomationMethods
{
	
	public OpenWeatherMap(WebDriver driver) {
		this.driver=driver;
	}

	public String getTemperatureByCityName(String cityName) {
		Map<String,String> qParameters=new HashMap<String, String>();
		
		qParameters.put("q", cityName);
		qParameters.put("appid", prop.getProperty("appid"));
		qParameters.put("units", prop.getProperty("units"));
		
		Response response =	get("", qParameters);
		verifyStatusCode(response, 200);
		String temperature=getValueAtJsonPath(response.asString(), "$.main.temp");

		return temperature;
		
	}
}