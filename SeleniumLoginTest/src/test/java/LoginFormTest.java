import org.junit.*;
import org.junit.rules.TestName;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;

public class LoginFormTest {

    static WebDriver driver;
    // đường dẫn tới file Test_Login.html (relative OK)
    static String filePath = new File("Test_Login.html").getAbsolutePath();

    @Rule
    public TestName testName = new TestName();

    @BeforeClass
    public static void setUpClass() {
        // chỉnh đường dẫn chromedriver theo project của bạn
        System.setProperty("webdriver.chrome.driver", "chromedriver-win32/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterClass
    public static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Before
    public void openPage() {
        driver.get("file:///" + filePath);
    }

    @After
    public void takeScreenshot() {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String name = testName.getMethodName();
            File target = new File("screenshots/" + name + ".png");
            target.getParentFile().mkdirs();
            FileHandler.copy(src, target);
        } catch (Exception e) {
            System.out.println("Screenshot error: " + e.getMessage());
        }
    }

    // 1) Đăng nhập thành công (theo USERS trong HTML)
    @Test
    public void testLoginSuccess() {
        WebElement u = driver.findElement(By.id("username"));
        WebElement p = driver.findElement(By.id("password"));
        WebElement btn = driver.findElement(By.id("btnLogin"));

        u.clear(); p.clear();
        u.sendKeys("sv1@ptit.edu.vn");
        p.sendKeys("P@ssw0rd");
        btn.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("msg-success")));
        Assert.assertTrue(driver.findElement(By.id("msg-success")).isDisplayed());

        // script trong HTML redirect sau 700ms -> chờ url chứa dashboard.html
        wait.until(ExpectedConditions.urlContains("dashboard.html"));
        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("dashboard.html"));
    }

    // 2) Sai thông tin đăng nhập
    @Test
    public void testInvalidLogin() {
        WebElement u = driver.findElement(By.id("username"));
        WebElement p = driver.findElement(By.id("password"));
        WebElement btn = driver.findElement(By.id("btnLogin"));

        u.clear(); p.clear();
        u.sendKeys("sv1@ptit.edu.vn");
        p.sendKeys("wrongpass");
        btn.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("msg-error")));
        WebElement err = driver.findElement(By.id("msg-error"));
        Assert.assertTrue(err.isDisplayed());
        Assert.assertEquals("Invalid credentials.", err.getText().trim());
    }

    // 3) Bỏ trống trường
    @Test
    public void testEmptyFields() {
        WebElement u = driver.findElement(By.id("username"));
        WebElement p = driver.findElement(By.id("password"));
        WebElement btn = driver.findElement(By.id("btnLogin"));

        u.clear(); p.clear();
        btn.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("msg-error")));
        WebElement err = driver.findElement(By.id("msg-error"));
        Assert.assertTrue(err.isDisplayed());
        Assert.assertEquals("Please fill all required fields.", err.getText().trim());
    }

    // 4) Link Forgot password?
    @Test
    public void testForgotPasswordLink() {
        WebElement forgot = driver.findElement(By.id("linkForgot"));
        Assert.assertTrue(forgot.isDisplayed());
        // href="#" trong HTML -> click không điều hướng, nhưng click phải không lỗi
        forgot.click();
        // kiểm tra tồn tại attribute href (ít nhất đảm bảo link có)
        Assert.assertNotNull(forgot.getAttribute("href"));
    }

    // 5) Link SIGN UP
    @Test
    public void testSignUpLink() {
        WebElement signup = driver.findElement(By.id("linkSignup"));
        Assert.assertTrue(signup.isDisplayed());
        signup.click();
        Assert.assertNotNull(signup.getAttribute("href"));
    }

    // 6) Nút social login
    @Test
    public void testSocialButtons() {
        WebElement fb = driver.findElement(By.id("btnFacebook"));
        WebElement tw = driver.findElement(By.id("btnTwitter"));
        WebElement gg = driver.findElement(By.id("btnGoogle"));

        Assert.assertTrue(fb.isDisplayed());
        Assert.assertTrue(tw.isDisplayed());
        Assert.assertTrue(gg.isDisplayed());

        // Click từng nút để đảm bảo không exception
        fb.click();
        tw.click();
        gg.click();
    }
}
