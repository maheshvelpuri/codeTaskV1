package com.testvagrant.accuWeather;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.testng.asserts.Assertion;

import com.testvagrant.reusableComponents.AutomationMethods;
import com.testvagrant.reusableComponents.TemperatureMismatchException;

public class AccuWeather extends AutomationMethods
{
	
	public AccuWeather(WebDriver driver) {
		this.driver=driver;
	}

	@CacheLookup
	@FindAll({
		@FindBy(name="query")
	})
	WebElement searchField;
	
	@FindBys({
		@FindBy(className="header-loc")
	})	
	WebElement resultHeader;
	
	@FindBy(xpath="//*[local-name()='svg' and @data-qa='searchIcon']/*[local-name()='path']")
	WebElement searchIcon;
	
	@FindBy(xpath="//h2[contains(text(),'Current Weather')]/..//div[@class='temp']")
	WebElement cityTemperatureField;
	
	public void verifysearchFieldIsDisplayed() {
		verifyIsDisplayed(searchField, "searchField");
	}
	
	public void compareTemperatureWithOpenWeather(String cityName,String openWeatherTemperature) {

		clearNEnterText(searchField, cityName, "search Field");
		doubleClick(searchIcon, "search Icon");
		verifyTextDisplayed(resultHeader,cityName);
		String accuWeatherTempetarure=getText(cityTemperatureField, "city Temperature Field");
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
}