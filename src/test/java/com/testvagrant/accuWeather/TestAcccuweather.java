package com.testvagrant.accuWeather;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.testvagrant.openWeatherMap.OpenWeatherMap;
import com.testvagrant.reusableComponents.AutomationMethods;

public class TestAcccuweather extends AutomationMethods
{

	AccuWeather accuWeather;
	OpenWeatherMap openWeatherMap;
	
	@BeforeClass
	public void beforetest() 
	{
		initialise();
		startTest("Navigate to accuweather");
		openBrowser(prop.getProperty("url"), prop.getProperty("browser"));
		accuWeather=PageFactory.initElements(driver, AccuWeather.class);
		accuWeather.verifysearchFieldIsDisplayed();
		endTest();
		
		openWeatherMap=new OpenWeatherMap(driver);
	}
	
	@Test
	public void testTemperature() 
	{
		startTest("Test temperature in AccuWeather with openweatherMap");
		String temperature=openWeatherMap.getTemperatureByCityName(prop.getProperty("cityInOpenWeather"));
		accuWeather.compareTemperatureWithOpenWeather(prop.getProperty("cityInAccuWeather"),temperature);
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