package com.testvagrant.accuWeather;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.testvagrant.openWeatherMap.OpenWeatherMap;
import com.testvagrant.reusableComponents.AutomationMethods;

public class TestAcccuweather extends AutomationMethods
{

	AccuWeather accuWeather;
	
	
	@BeforeClass
	public void beforetest() 
	{
		initialise();
		startTest("Navigate to accuweather");
		openBrowser(prop.getProperty("url"), prop.getProperty("browser"));
		accuWeather=new AccuWeather(driver);
		accuWeather.verifysearchFieldIsDisplayed();
		endTest();
	}
	
	@Test
	public void testAccuWeather() 
	{
		startTest("test");
		OpenWeatherMap openWeatherMap=new OpenWeatherMap(driver);
		String temperature=openWeatherMap.getTemperatureByCityName(prop.getProperty("cityInOpenWeather"));
		accuWeather.compareTempWithOpenWeather(prop.getProperty("cityInAccuWeather"),temperature);
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