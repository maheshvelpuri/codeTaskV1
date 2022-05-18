package com.testvagrant.accuWeatherPages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import com.testvagrant.reusableComponents.AutomationMethods;

public class SearchResult extends AutomationMethods
{
	
	public SearchResult(WebDriver driver) {
		this.driver=driver;
	}

	@FindBys({
		@FindBy(className="header-loc")
	})	
	WebElement resultHeader;
	
	@FindBy(xpath="//h2[contains(text(),'Current Weather')]/..//div[@class='temp']")
	WebElement cityTemperatureField;
	
	public SearchResult verifyResultHeaderIsDisplayed(String cityName) {
		verifyTextDisplayed(resultHeader,cityName);
		return this;
	}

	public String getTemperatureOfCity() {
		return getText(cityTemperatureField, "city Temperature Field");
	}
	
}