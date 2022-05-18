package com.testvagrant.accuWeatherPages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.testvagrant.reusableComponents.AutomationMethods;


public class Home extends AutomationMethods
{
	
	public Home(WebDriver driver) {
		this.driver=driver;
	}

	@CacheLookup
	@FindAll({
		@FindBy(name="query")
	})
	WebElement searchField;
	
	@FindBy(xpath="//*[local-name()='svg' and @data-qa='searchIcon']/*[local-name()='path']")
	WebElement searchIcon;
	
	public Home verifysearchFieldIsDisplayed() {
		verifyIsDisplayed(searchField, "searchField");
		return this;
	}

	public Home enterCityINSearchField(String cityName) {
		clearNEnterText(searchField, cityName, "search Field");
		return this;
	}
	
	public SearchResult clickSearchIcon() {
		doubleClick(searchIcon, "search Icon");
		return PageFactory.initElements(driver, SearchResult.class);
	}

}