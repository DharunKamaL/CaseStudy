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
import org.openqa.selenium.chrome.ChromeOptions;
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
import org.testng.asserts.SoftAssert;

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
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*");
		driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(40));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(40));
		wait = new WebDriverWait(driver, Duration.ofSeconds(40));
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
		driver.findElement(By.id("login2")).click();
		WebElement userName = driver.findElement(By.cssSelector("input[id=\"loginusername\"]"));
		wait.until(ExpectedConditions.elementToBeClickable(userName));
		userName.sendKeys("Dharun_K");
		driver.findElement(By.id("loginpassword")).sendKeys("dkvk2313");
		driver.findElement(By.cssSelector("button[onclick=\"logIn()\"]")).click();
		wait.until(ExpectedConditions.alertIsPresent());
		Alert alert = driver.switchTo().alert();
		alert.accept();
		WebElement wel = driver.findElement(By.xpath("//a[contains(text(),'Welcome')]"));
		wait.until(ExpectedConditions.visibilityOf(wel));
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
		driver.findElement(By.id("login2")).click();
		WebElement userName1 = driver.findElement(By.cssSelector("input[id=\"loginusername\"]"));
		wait.until(ExpectedConditions.elementToBeClickable(userName1));
		userName1.sendKeys(prop.getProperty("username"));
		driver.findElement(By.id("loginpassword")).sendKeys(prop.getProperty("password"));
		driver.findElement(By.cssSelector("button[onclick=\"logIn()\"]")).click();
		WebElement wel = driver.findElement(By.xpath("//a[contains(text(),'Welcome')]"));
		wait.until(ExpectedConditions.visibilityOf(wel));
		Assert.assertEquals(wel.getText(), "Welcome Dharun_K");
	}

	@Test(dataProvider = "items", groups = "featureOne", priority = 3)
	public void addItemtoCart(String productCategory, String productName) throws InterruptedException {
		extentTest = reports.createTest("addItemtoCart");
		WebElement proCategory = driver.findElement(By.partialLinkText(productCategory));
		wait.until(ExpectedConditions.elementToBeClickable(proCategory));
		proCategory.click();
		actions.scrollByAmount(0, 100).perform();
		WebElement proName = driver.findElement(By.partialLinkText(productName));
		wait.until(ExpectedConditions.elementToBeClickable(proName));
		proName.click();
		driver.findElement(By.xpath("//a[contains(text(),'Add to')]")).click();
		wait.until(ExpectedConditions.alertIsPresent());
		Alert alert = driver.switchTo().alert();
		alert.accept();
		driver.findElement(By.xpath("(//ul/li//a)[1]")).click();

	}

	@DataProvider(name = "items")
	public Object[][] singleItem() throws CsvValidationException, IOException {
		String path = System.getProperty("user.dir") + "//src//test//resources//csvFiles//items.csv";
		CSVReader reader = new CSVReader(new FileReader(path));
		String cols[];
		ArrayList<Object> dList = new ArrayList<Object>();
		while ((cols = reader.readNext()) != null) {
			Object[] record = { cols[0], cols[1] };
			dList.add(record);
		}
		return dList.toArray(new Object[dList.size()][]);
	}

	@Test(priority = 4, dependsOnMethods = "addItemtoCart")
	public void delItem() throws InterruptedException {
		extentTest = reports.createTest("delItem");
		driver.findElement(By.xpath("//a[text()='Cart']")).click();
		List<WebElement> itemsBefDel = driver.findElements(By.xpath("//td[2]"));
		wait.until(ExpectedConditions.visibilityOfAllElements(itemsBefDel));
		actions.scrollByAmount(0, 150).perform();
		int itemSizeBefDel = itemsBefDel.size();
		String proTxt = driver.findElement(By.xpath("(//td[2])[1]")).getText();
//		boolean itemsAdded = true;
		if (itemSizeBefDel > 1) {
//			Assert.assertTrue(itemsAdded);
			driver.findElement(By.xpath("(//a[text()='Delete'])[1]")).click();
		}
		
		List<WebElement> itemsAftDel = driver.findElements(By.xpath("//td[2]"));
		wait.until(ExpectedConditions.visibilityOfAllElements(itemsAftDel));
		String proTxt1 = driver.findElement(By.xpath("(//td[2])[1]")).getText();
		if (proTxt != proTxt1) {
			Assert.assertTrue(true);
		}
	}

	@Test(groups = "featureOne", priority = 5)
	public void placeOrder() throws IOException, InterruptedException {
		extentTest = reports.createTest("placeOrder");
		prop = new Properties();
		String path2 = System.getProperty("user.dir")
				+ "//src//test//resources//propertyFiles//placeorderDetails.properties";
		FileInputStream file2 = new FileInputStream(path2);
		prop.load(file2);
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
