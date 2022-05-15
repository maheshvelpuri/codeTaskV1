package com.testvagrant.accuWeather;

import org.openqa.selenium.WebDriver;
import org.testng.asserts.Assertion;

import com.testvagrant.reusableComponents.AutomationMethods;
import com.testvagrant.reusableComponents.TemperatureMismatchException;

public class AccuWeather extends AutomationMethods
{
	
	public AccuWeather(WebDriver driver) {
		this.driver=driver;
	}

	protected String searchField				="//input[@name='query']";
	public void verifysearchFieldIsDisplayed() {verifyIsDisplayed(searchField, "searchField");}
	
	protected String searchIcon					="//*[local-name()='svg' and @data-qa='searchIcon']/*[local-name()='path']";

	protected String resultHeader				="//h1[@class='header-loc']";
	
	protected String temperature				="//h2[contains(text(),'Current Weather')]/..//div[@class='temp']";
	
	public void compareTempWithOpenWeather(String cityName,String openWeatherTemperature) {

		int expectedTemperature=parseStringToDoubleNroundToInt(openWeatherTemperature,"open Weather Temperature");
		
		clearNEnterText(searchField, cityName, "search Field");
		doubleClick(searchIcon, "search Icon");
		verifyTextDisplayed(resultHeader,cityName);
		
		String temperatureInAccuWeather=getText(temperature, "Temperature");
		temperatureInAccuWeather=temperatureInAccuWeather.substring(0, temperatureInAccuWeather.length()-2);
		
		int actualTemperature=parseStringToDoubleNroundToInt(temperatureInAccuWeather,"accuweather temperature");
		try {
			if(expectedTemperature-2<=actualTemperature && actualTemperature<=expectedTemperature+2) {
				logPass("Temperature in Accuweather is in expected range of ["+(expectedTemperature-2)+","+(expectedTemperature+2)
						+"], temperature in Accuweather is "+actualTemperature);
			}
			else {
				logFail("Temperature in Accuweather is not in expected range of ["+(expectedTemperature-2)+","+(expectedTemperature+2)
						+"], temperature in Accuweather is "+actualTemperature);
					throw new TemperatureMismatchException("Temperature mismatch betwwen accuweather and openweatherMap");
			}
		} catch (TemperatureMismatchException e) {
			logFail("Temperature mismatch between accuweather and openweatherMap");
			new Assertion().fail();
		}
	}
	
}