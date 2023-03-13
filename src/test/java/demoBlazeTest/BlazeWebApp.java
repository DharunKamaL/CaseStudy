package demoBlazeTest;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import CommonUtils.Utility;
import io.github.bonigarcia.wdm.WebDriverManager;

public class BlazeWebApp {
	WebDriver driver;
	Properties prop;
	Actions actions;
	WebDriverWait wait;
	ExtentReports reports;
	ExtentSparkReporter spark;
	ExtentTest extentTest;

	@BeforeClass(groups = "featureOne")
	public void setup() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		actions = new Actions(driver);
	}

	@BeforeTest(groups = "featureOne")
	public void extentSetup() {
		reports = new ExtentReports();
		spark = new ExtentSparkReporter("target\\BlazeWebAppReport.html");
		reports.attachReporter(spark);
	}

	@Test(priority = 1)
	public void invalidLogin() throws IOException, InterruptedException {
		extentTest = reports.createTest("invalidLogin");
		driver.get("https://www.demoblaze.com/");
		wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		driver.findElement(By.id("login2")).click();
		Thread.sleep(5000);
		driver.findElement(By.cssSelector("input[id=\"loginusername\"]")).sendKeys("Dharun_K");
		driver.findElement(By.id("loginpassword")).sendKeys("dkvk2313");
		driver.findElement(By.cssSelector("button[onclick=\"logIn()\"]")).click();
		wait.until(ExpectedConditions.alertIsPresent());
//		TakesScreenshot screen = (TakesScreenshot) driver;
//		File scr = screen.getScreenshotAs(OutputType.FILE);
//		FileUtils.copyFile(scr, new File("F:\\Dharun\\Screenshot\\invalidLogin1.png"));
		Alert alert = driver.switchTo().alert();
		alert.accept();
		WebElement wel = driver.findElement(By.xpath("//a[contains(text(),'Welcome')]"));
		Assert.assertEquals(wel.getText(), "Welcome Dharun_K");
	}

	@Test(groups = "featureOne", priority = 2)
	public void login() throws IOException, InterruptedException {
		extentTest = reports.createTest("login");
		prop = new Properties();
		String path1 = System.getProperty("user.dir")
				+ "//src//test//resources//propertyFiles//loginDetails.properties";
		FileInputStream file1 = new FileInputStream(path1);
		prop.load(file1);
		driver.get("https://www.demoblaze.com/");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
//		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(50));
		driver.findElement(By.id("login2")).click();
		Thread.sleep(5000);
		driver.findElement(By.cssSelector("input[id=\"loginusername\"]")).sendKeys(prop.getProperty("username"));
		driver.findElement(By.id("loginpassword")).sendKeys(prop.getProperty("password"));
		driver.findElement(By.cssSelector("button[onclick=\"logIn()\"]")).click();
		WebElement wel = driver.findElement(By.xpath("//a[contains(text(),'Welcome')]"));
		Assert.assertEquals(wel.getText(), "Welcome Dharun_K");
	}

	@Test(dataProvider = "items", groups = "featureOne", priority = 3)
	public void addItemtoCart(String productCategory,String productName) throws InterruptedException {
		extentTest = reports.createTest("addItemtoCart");
		wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		//Thread.sleep(5000);
		//actions.scrollByAmount(0, 200);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		Thread.sleep(5000);
		driver.findElement(By.partialLinkText(productCategory)).click();
		driver.findElement(By.partialLinkText(productName)).click();
		driver.findElement(By.xpath("//a[contains(text(),'Add to')]")).click();
		wait.until(ExpectedConditions.alertIsPresent());
		Alert alert = driver.switchTo().alert();
		alert.accept();
		//driver.findElement(By.id("cartur")).click();
		Thread.sleep(3000);
		//driver.findElement(By.xpath("(//ul[@class='navbar-nav ml-auto']//li/child::a)[1]"));
		driver.navigate().to("https://www.demoblaze.com/index.html");
	}
	
	@DataProvider(name = "items")
	public Object[][] singleItem() throws CsvValidationException, IOException{
		String path = System.getProperty("user.dir") +
						"//src//test//resources//csvFiles//items.csv";
		CSVReader reader = new CSVReader(new FileReader(path));
		String cols[];
		ArrayList<Object> dList = new ArrayList<Object>();
		while((cols = reader.readNext()) != null) {
			Object[] record = {cols[0],cols[1]};
			dList.add(record);
		}
		return dList.toArray(new Object[dList.size()][]);
	}
 
//	@Test(priority = 4, dataProvider = "items")
//	public void multipleItemstoCart(String productCategory, String productName) throws InterruptedException {
//		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
//		extentTest = reports.createTest("multipleItemstoCart");
//		wait = new WebDriverWait(driver, Duration.ofSeconds(50));
//		//Thread.sleep(5000);
//		driver.findElement(By.xpath("//a[contains(text(),'Home')]"));
//		actions.scrollByAmount(0, 200);
//		driver.findElement(By.partialLinkText(productCategory)).click();
//		actions.scrollByAmount(0, 200);
//		driver.findElement(By.partialLinkText(productName)).click();
//		driver.findElement(By.xpath("//a[text()='Add to cart']")).click();
//		wait.until(ExpectedConditions.alertIsPresent());
//		Alert alert = driver.switchTo().alert();
//		alert.accept();
//	}
//
//	@DataProvider(name = "items")
//	public Object[][] getItems() throws CsvValidationException, IOException {
//		String path = System.getProperty("user.dir") + "//src//test//resources//csvFiles//items.csv";
//		CSVReader reader = new CSVReader(new FileReader(path));
//		String cols[];
//		ArrayList<Object> dList = new ArrayList<Object>();
//		while ((cols = reader.readNext()) != null) {
//			Object[] rec = { cols[0], cols[1] };
//			dList.add(rec);
//		}
//		return dList.toArray(new Object[dList.size()][]);
//	}

	@Test(priority = 4, dependsOnMethods = "addItemtoCart")
	public void delItem() throws InterruptedException {
		extentTest = reports.createTest("delItem");
		Thread.sleep(5000);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(50));
		driver.findElement(By.xpath("//a[text()='Cart']")).click();
		Thread.sleep(5000);
		actions.scrollByAmount(0, 300);
		List<WebElement> itemsBefDel = driver.findElements(By.xpath("//td[2]"));
		int itemSizeBefDel = itemsBefDel.size();
		Assert.assertEquals(itemSizeBefDel, 5);
		driver.findElement(By.xpath("(//a[text()='Delete'])[1]")).click();
		List<WebElement> itemsAftDel = driver.findElements(By.xpath("//td[2]"));
		int itemSizeAftDel = itemsAftDel.size();
		Assert.assertEquals(itemSizeAftDel, 4);
	}

	@Test(groups = "featureOne", priority = 5)
	public void placeOrder() throws IOException, InterruptedException {
		extentTest = reports.createTest("placeOrder");
		prop = new Properties();
		String path2 = System.getProperty("user.dir")
				+ "//src//test//resources//propertyFiles//placeorderDetails.properties";
		FileInputStream file2 = new FileInputStream(path2);
		prop.load(file2);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		Thread.sleep(5000);
		driver.findElement(By.xpath("//button[text()='Place Order']")).click();
		driver.findElement(By.cssSelector("input[id=\"name\"]")).sendKeys(prop.getProperty("Name"));
		driver.findElement(By.cssSelector("input[id=\"country\"]")).sendKeys(prop.getProperty("Country"));
		driver.findElement(By.cssSelector("input[id=\"city\"]")).sendKeys(prop.getProperty("City"));
		driver.findElement(By.cssSelector("input[id=\"card\"]")).sendKeys(prop.getProperty("CreditCard"));
		driver.findElement(By.cssSelector("input[id=\"month\"]")).sendKeys(prop.getProperty("Month"));
		driver.findElement(By.cssSelector("input[id=\"year\"]")).sendKeys(prop.getProperty("Year"));
		driver.findElement(By.xpath("//button[text()='Purchase']")).click();
		WebElement sPurchase = driver.findElement(By.xpath("//h2[contains(text(),'Thank you')]"));
		Assert.assertEquals(sPurchase.getText(), "Thank you for your purchase!");
		driver.findElement(By.xpath("//button[text()='OK']")).click();
	}

	@AfterMethod(groups = "featureOne")
	public void failure(ITestResult result) {
		if (ITestResult.FAILURE == result.getStatus()) {
			extentTest.log(Status.FAIL, result.getThrowable().getMessage());
			String path = Utility.getScreenshotPath(driver);
			extentTest.addScreenCaptureFromPath(path);
		}
	}

	@AfterTest(groups = "featureOne")
	public void extentfinishUp() {
		reports.flush();
	}
}
