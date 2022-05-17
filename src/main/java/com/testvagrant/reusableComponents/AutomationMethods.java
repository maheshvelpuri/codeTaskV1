package com.testvagrant.reusableComponents;
import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.Assertion;
import org.testng.asserts.SoftAssert;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AutomationMethods {

	static Map<Integer,ExtentTest> extentTestMap				 = new HashMap<Integer,ExtentTest>();
	static Map<Integer,ExtentTest> extentParentMap 				 = new HashMap<Integer,ExtentTest>();
	static Map<Integer,ExtentReports> extentreportsMap			 = new HashMap<Integer,ExtentReports>();
	static Map<Integer,String> reportLocationMap				 = new HashMap<Integer,String>();
	public WebDriver driver									      ;
	public static Properties prop             					 = new Properties();
	static String reportLocation 								 = null;	
	static String htmlLocation 									 = null;                           
	static ExtentReports reports 								 = null;
	static String folderName 					                 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
	static String fileName 						                 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss'.html'").format(new Date());
	public SoftAssert softAssert								 = new SoftAssert();
	public Configuration configuration 						     = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider()).mappingProvider(new JacksonMappingProvider()).build();
	public static String token = null;
	public RequestSpecification rest                             = null;
	public Response response                                     = null;

	public synchronized static void startTest(String test) {
		System.out.println(test);
		ExtentTest logger=getreport().startTest(test);
		extentTestMap.put((int) (long) (Thread.currentThread().getId()), logger);
	}

	public synchronized static void initializeReportLocation(String reportlocation) {
		reportLocationMap.put((int) (long) (Thread.currentThread().getId()), reportlocation);
	}

	public static synchronized String getreportlocation() {
		return reportLocationMap.get((int) (long) (Thread.currentThread().getId()));
	}

	public synchronized static void startReporter(String location) {
		extentreportsMap.put((int) (long) (Thread.currentThread().getId()), reports);
	}

	public static synchronized ExtentReports getreport() {
		return extentreportsMap.get((int) (long) (Thread.currentThread().getId()));
	}

	public synchronized static ExtentTest startParentTest(String test) {
		ExtentTest  parent = getreport().startTest(test);
		extentParentMap.put((int) (long) (Thread.currentThread().getId()), parent);
		getreport().flush();
		return parent;
	}

	public synchronized static void endTest(){
		getreport().endTest(extentTestMap.get((int) (long) (Thread.currentThread().getId())));
		getreport().flush();
	}

	public static synchronized ExtentTest getTest() {
		return extentTestMap.get((int) (long) (Thread.currentThread().getId()));
	}

	public String addScreenShot(){
		return getTest().addScreenCapture(tearDown(driver));
	}
	public void logPass(String details){
		getTest().log(LogStatus.PASS,  details);	
	}
	public void logFail(String details){
		getTest().log(LogStatus.FAIL,  details);	
	}
	public void logInfo(String details){
		getTest().log(LogStatus.INFO,  details);	
	}
	public void logWarning(String details){
		getTest().log(LogStatus.WARNING,  details);	
	}
	public void logSkip(String details){
		getTest().log(LogStatus.SKIP,  details);	
	}

	protected void openBrowser(String URL,String browser)
	{	
		if(browser.trim().equalsIgnoreCase("chrome"))
		{
			openChrome(URL);
		}
		else
		{
			logWarning("Please initiate proper browser name in config file<br/>" + getTest().addScreenCapture(tearDown(driver)));
			getreport().endTest(getTest());
			getreport().flush();
			new Assertion().fail();
		}	
	}	

	protected void openChrome(String URL)
	{
		try
		{
			System.out.println("launching chrome browser");
			ChromeOptions options = new ChromeOptions();
			options.addArguments("disable-infobars");   
			options.addArguments("--start-maximized");
			options.addArguments("disable-infobars");   
			Map<String, Object> prefs = new HashMap<String, Object>();
			prefs.put("credentials_enable_service", false);
			prefs.put("profile.password_manager_enabled", false);
			options.setExperimentalOption("prefs", prefs);   
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver(options);       
			driver.manage().window().maximize(); 
			driver.manage().deleteAllCookies();
			driver.get(URL);           
			logPass("Opened chrome Browser Sucessfully and navigated to url : </br>"+URL);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logFail("Launching Chrome browser UnSucessfull02</br>"+e+"</br>"+addScreenShot());
			new Assertion().fail();
		}   
	}

	protected String tearDown(WebDriver driver)	
	{
		try
		{
			UUID uuid = UUID.randomUUID();
			File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File(getreportlocation()+uuid+".png"));
			getTest().addScreenCapture(getreportlocation()+uuid+".png");
			return uuid+".png";
		} 
		catch (IOException e)
		{	
			System.out.println("Error while generating screenshot:\n" + e.toString());
			return "";
		}
	}

	protected void doubleClick(String xpath,String elementName) 
	{
		try {			
			ewait(xpath);
			WebElement element = DX(xpath);
			Actions action=new Actions(driver);
			action.doubleClick(element).build().perform();
			getTest().log(LogStatus.INFO, "double clicked "+elementName+" successfully");
		} catch (Exception e) {
			getTest().log(LogStatus.WARNING, "Exception while double clicking "+elementName+" is due to <br/>"+e+ getTest().addScreenCapture(tearDown(driver)));
			new Assertion().fail();
		}
	}

	protected void doubleClick(WebElement element,String elementName) 
	{
		try {			
			ewait(element);
			Actions action=new Actions(driver);
			action.doubleClick(element).build().perform();
			getTest().log(LogStatus.INFO, "double clicked "+elementName+" successfully");
		} catch (Exception e) {
			getTest().log(LogStatus.WARNING, "Exception while double clicking "+elementName+" is due to <br/>"+e+ getTest().addScreenCapture(tearDown(driver)));
			new Assertion().fail();
		}
	}

	protected void ewait(String xpath){
		new WebDriverWait(driver,5).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
	}

	protected void ewait(WebElement element){
		new WebDriverWait(driver,5).until(ExpectedConditions.visibilityOf(element));
	}

	protected WebElement DX(String XpathKey)	{
		return driver.findElement(By.xpath(XpathKey));
	}

	protected void verifyIsDisplayed(String xpath,String elementName) {
		try {
			ewait(xpath);
			if(DX(xpath).isDisplayed()){
				getTest().log(LogStatus.PASS, "Displayed "+elementName+" Successfully");
			}
			else{
				System.out.println("Not displayed xpath : "+xpath);
				getTest().log(LogStatus.FAIL, "Failed to Display <br/> "+elementName+ getTest().addScreenCapture(tearDown(driver)));
				new Assertion().fail();
			}
		} catch (Exception e) {
			System.out.println("Not displayed xpath : "+xpath);
			getTest().log(LogStatus.FAIL, "Exception while verifying to Display <br/> "+elementName+e+ getTest().addScreenCapture(tearDown(driver)));
			new Assertion().fail();
		}
	}
	
	protected void verifyIsDisplayed(WebElement element,String elementName) {
		try {
			ewait(element);
			if(element.isDisplayed()){
				getTest().log(LogStatus.PASS, "Displayed "+elementName+" Successfully");
			}
			else{
				getTest().log(LogStatus.FAIL, "Failed to Display <br/> "+elementName+ getTest().addScreenCapture(tearDown(driver)));
				new Assertion().fail();
			}
		} catch (Exception e) {
			getTest().log(LogStatus.FAIL, "Exception while verifying to Display <br/> "+elementName+e+ getTest().addScreenCapture(tearDown(driver)));
			new Assertion().fail();
		}
	}
	
	protected String getText(WebElement element,String elementName){
		try {
			ewait(element);
			String text=element.getText();
			getTest().log(LogStatus.INFO, "should read text on "+elementName, "text on "+elementName+" is "+text);
			return text;
		} catch (Exception e) {
			System.out.println("Exception while reading text on "+elementName+" is due to <br/>"+e);
			getTest().log(LogStatus.WARNING, "Exception while reading text on "+elementName+" is due to <br/>"+e+ getTest().addScreenCapture(tearDown(driver))); 
			new Assertion().fail();
			return null;
		}
	}

	protected String getText(String xpath,String elementName){
		try {
			ewait(xpath);
			String text=DX(xpath).getText();
			getTest().log(LogStatus.INFO, "should read text on "+elementName, "text on "+elementName+" is "+text);
			return text;
		} catch (Exception e) {
			System.out.println("Exception while reading text on "+elementName+" is due to <br/>"+e);
			getTest().log(LogStatus.WARNING, "Exception while reading text on "+elementName+" is due to <br/>"+e+ getTest().addScreenCapture(tearDown(driver))); 
			new Assertion().fail();
			return null;
		}
	}

	protected boolean verifyTextDisplayed(String xpath,String text){
		try	{
			ewait(xpath);
			if(DX(xpath).getText().contains(text)){
				getTest().log(LogStatus.PASS, "Should display text '"+text+"'", "Displayed text '"+text+"' successfully");
				return true;
			}
			else{
				getTest().log(LogStatus.FAIL, "Should display text '"+text+"'", "text '"+text+"' is not displayed but showed ' <br/> "+DX(xpath).getText()+"'"+ getTest().addScreenCapture(tearDown(driver)));
				new Assertion().fail();
				return false;
			}
		} catch (Exception e) {
			getTest().log(LogStatus.FAIL, "Failed to verify text:<br/>"+text +"<br/>"+e+ getTest().addScreenCapture(tearDown(driver)));
			new Assertion().fail();
			return false;
		}
	}

	protected boolean verifyTextDisplayed(WebElement element,String text){
		try	{
			ewait(element);
			if(element.getText().contains(text)){
				getTest().log(LogStatus.PASS, "Should display text '"+text+"'", "Displayed text '"+text+"' successfully");
				return true;
			}
			else{
				getTest().log(LogStatus.FAIL, "Should display text '"+text+"'", "text '"+text+"' is not displayed but showed ' <br/> "+element.getText()+"'"+ getTest().addScreenCapture(tearDown(driver)));
				new Assertion().fail();
				return false;
			}
		} catch (Exception e) {
			getTest().log(LogStatus.FAIL, "Failed to verify text:<br/>"+text +"<br/>"+e+ getTest().addScreenCapture(tearDown(driver)));
			new Assertion().fail();
			return false;
		}
	}

	protected static synchronized void initialise() 
	{		
		try {
			if(reportLocation==null && htmlLocation==null && reports==null)
			{			
				reportLocation 			= "C:/Reports/Automation "+new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date())+"/";	
				htmlLocation 			= "Automation_"+new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss'.html'").format(new Date());                           				
				reports 				= new ExtentReports(reportLocation+htmlLocation,false);
				File file 			 = new File("./src/main/java/com/testvagrant/resources/testData.properties");
				FileInputStream fileInput            = new FileInputStream(file);
				prop.load(fileInput);
			}
			RestAssured.baseURI=prop.getProperty("baseURI");
		} catch (Exception e) {			
			e.printStackTrace();
		} 


		initializeReportLocation(reportLocation);
		startReporter(reportLocation+htmlLocation);

	}
	public void verifyStatusCode(Response response,int statusCode) {	
		try {
			int code=response.statusCode();
			if(code==statusCode) {
				logPass("Status code is "+statusCode);				
			}
			else {
				logFail("Expected status code : "+statusCode+"<br/>Actual status code : "+code);
				new Assertion().fail();
			}
		} 
		catch (Exception e) {
			logFail("Exception while verifying statuscode is "+e.getMessage());
			new Assertion().fail();
		}
	}

	public Response get(String URL,Map<String,String> qParameters) {

		try {
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		Response response=given().contentType("application/json").queryParams(qParameters).get(URL);
		logInfo("Performed get method on "+RestAssured.baseURI+URL+" with query parameters as "+qParameters.toString()+"<br/> "
				+ "<div style='height: 500px;margin: 20px;'>"
				+ "<div style='overflow-y: scroll; height: 100%;'>"
				+ "<pre>"+response.jsonPath().prettify()+"</pre>"
				+ "</div></div>");
		return response;
	}

	public String getValueAtJsonPath(String jsonString,String jsonPath) {
		try {
			DocumentContext json = JsonPath.using(configuration).parse(jsonString);
			String value=json.read(jsonPath).toString().replaceAll("\"", "");
			return value;

		} catch (Exception e) {
			logFail("Exception while reading value at jason path "+e);
			return null;
		}
	}


	protected void clearNEnterText(String xpath,String text,String elementName){
		try {
			ewait(xpath);
			((JavascriptExecutor)driver).executeScript("arguments[0].setAttribute('style','background:yellow');", DX(xpath));
			DX(xpath).clear();
			DX(xpath).sendKeys(text);
			((JavascriptExecutor)driver).executeScript("arguments[0].setAttribute('style','background:white');", DX(xpath));
			getTest().log(LogStatus.INFO, "Cleared and entered text "+text+" in "+elementName);
		} catch (Exception e) {
			e.printStackTrace();
			getTest().log(LogStatus.FAIL, "Exception while clearing and entering text "+text+" in "+elementName+" is due to <br/>"+e+ getTest().addScreenCapture(tearDown(driver)));
			new Assertion().fail();
		}
	}

	protected void clearNEnterText(WebElement element,String text,String elementName){
		try {
			ewait(element);
			((JavascriptExecutor)driver).executeScript("arguments[0].setAttribute('style','background:yellow');", element);
			element.clear();
			element.sendKeys(text);
			((JavascriptExecutor)driver).executeScript("arguments[0].setAttribute('style','background:white');", element);
			getTest().log(LogStatus.INFO, "Cleared and entered text "+text+" in "+elementName);
		} catch (Exception e) {
			e.printStackTrace();
			getTest().log(LogStatus.FAIL, "Exception while clearing and entering text "+text+" in "+elementName+" is due to <br/>"+e+ getTest().addScreenCapture(tearDown(driver)));
			new Assertion().fail();
		}
	}

	public int parseStringToDoubleNroundToInt(String value,String valueName) {
		try {
			double d=Double.parseDouble(value);
			int i=(int)Math.round(d);
			logInfo(valueName+" is "+value+", rounded value is "+i);
			return i;
		} catch (Exception e) {
			logFail("Exception while parsng "+valueName+" To Double and rounding to int is due to "+e);
			new Assertion().fail();
			return 0;
		}
	}

	
}
