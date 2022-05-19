package com.testvagrant.accuWeather;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testvagrant.accuWeatherPages.Home;
import com.testvagrant.openWeatherMap.OpenWeatherMap;
import com.testvagrant.openWeatherPOJO.WeatherByCityName;
import com.testvagrant.reusableComponents.AutomationMethods;
import com.testvagrant.reusableComponents.TemperatureMismatchException;

import io.restassured.response.Response;

public class TestAcccuweather extends AutomationMethods
{

	OpenWeatherMap openWeatherMap;
	Home home;

	@BeforeClass
	public void beforetest() 
	{
		initialise();
		startTest("Navigate to accuweather");
		openBrowser(prop.getProperty("url"), prop.getProperty("browser"));
		home=PageFactory.initElements(driver, Home.class);
		home.verifysearchFieldIsDisplayed();
		endTest();
		
		openWeatherMap=new OpenWeatherMap(driver);
	}
	
	@Test
	public void testTemperature() throws JsonMappingException, JsonProcessingException 
	{
		startTest("Test temperature in AccuWeather with openweatherMap");
		
		Map<String,String> qParameters=new HashMap<String, String>();
		qParameters.put("q", prop.getProperty("cityInOpenWeather"));
		qParameters.put("appid", prop.getProperty("appid"));
		qParameters.put("units", prop.getProperty("units"));
		Response response =	get("", qParameters);
		verifyStatusCode(response, 200);

		ObjectMapper objectMapper = new ObjectMapper();
		WeatherByCityName weatherByCityName =objectMapper.readValue(response.asString(), WeatherByCityName.class);
		String openWeatherTemperature=""+weatherByCityName.getMain().getTemp();
		
		String accuWeatherTempetarure=home.enterCityINSearchField(prop.getProperty("cityInAccuWeather"))
											.clickSearchIcon()
											.verifyResultHeaderIsDisplayed(prop.getProperty("cityInAccuWeather"))
											.getTemperatureOfCity();
		accuWeatherTempetarure=accuWeatherTempetarure.substring(0, accuWeatherTempetarure.length()-2);
		int accuWeatherTempetarureAsInteger=parseStringToDoubleNroundToInt(accuWeatherTempetarure,"accuweather temperature");
		int openWeatherTemperatureAsInteger=parseStringToDoubleNroundToInt(openWeatherTemperature,"open Weather Temperature");
		
		try {
			if(openWeatherTemperatureAsInteger-2<=accuWeatherTempetarureAsInteger && accuWeatherTempetarureAsInteger<=openWeatherTemperatureAsInteger+2) {
				logPass("Temperature in Accuweather is in expected range of ["+(openWeatherTemperatureAsInteger-2)+","+(openWeatherTemperatureAsInteger+2)
						+"], temperature in Accuweather is "+accuWeatherTempetarureAsInteger);
			}
			else {
				logFail("Temperature in Accuweather is not in expected range of ["+(openWeatherTemperatureAsInteger-2)+","+(openWeatherTemperatureAsInteger+2)
						+"], temperature in Accuweather is "+accuWeatherTempetarureAsInteger);
					throw new TemperatureMismatchException("Temperature mismatch between accuweather and openweatherMap");
			}
		} catch (TemperatureMismatchException e) {
			logFail("Temperature mismatch between accuweather and openweatherMap");
			new Assertion().fail();
		}

	}
	
	@AfterMethod
	public void afterMethod() {
		endTest();
	}
	@AfterClass
	public void afterTest() {
		driver.quit();
	}
}