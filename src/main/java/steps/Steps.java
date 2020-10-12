package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.util.List;
import java.util.NoSuchElementException;


public class Steps {
    WebDriver chromeDriver;

    String oldPageTitle = "", newPageTitle = "";
    String oldPageUrl = "", newPageUrl = "";
    String paperbackPrice = "£4.00";
    String itemTitle = "";
    int quantityItems = 0;

    @Given("^I start Chrome browser")
    public void startChrome() {
        String currentUserDir = System.getProperty("user.dir").replace("\\", "\\\\");
        String chromeDriverPath = currentUserDir + "\\src\\main\\resources\\chromedriver.exe";

        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        chromeDriver = new ChromeDriver();
    }

    @When("^I navigate to (.*)$")
    public void browsePage(String pageUrl) throws InterruptedException {
        chromeDriver.get(pageUrl);
        chromeDriver.manage().window().maximize();

        WebElement homepageLogo = chromeDriver.findElement(By.className("nav-logo-link"));

        WebDriverWait wait = new WebDriverWait(chromeDriver, 5);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("nav-logo-link")));

        Assert.assertEquals(homepageLogo.getAttribute("aria-label"), "Amazon.co.uk");

        this.acceptCookies();
    }


    @And("^I search for (.*) in (.*) category$")
    public void searchForItem(String searchItem, String category) {
        Select dropdown = new Select(chromeDriver.findElement(By.id("searchDropdownBox")));

        dropdown.selectByVisibleText(category);
        Assert.assertEquals(dropdown.getFirstSelectedOption().getText(), category);

        WebElement searchbox = chromeDriver.findElement(By.id("twotabsearchtextbox"));

        this.oldPageTitle = chromeDriver.getTitle();
        searchbox.clear();
        searchbox.sendKeys(searchItem);
        searchbox.sendKeys(Keys.RETURN);

        WebDriverWait wait = new WebDriverWait(chromeDriver, 3);
        this.newPageTitle = chromeDriver.getTitle();
        Assert.assertNotEquals(this.oldPageTitle, this.newPageTitle);

        WebElement firstResultTitle = chromeDriver.findElement(By.xpath
                ("//h2[@class='a-size-mini a-spacing-none a-color-base s-line-clamp-2']//a//span"));

        this.itemTitle = firstResultTitle.getText();
        boolean titleCorrect = this.itemTitle.contains(searchItem + " - Parts One and Two");

        Assert.assertTrue(titleCorrect);


        /*
        List<WebElement> prices = chromeDriver.findElements(By.className("a-offscreen"));

        for (int i = 0; i < prices.size(); i++) {
            if (this.isOldPrice(prices.get(i))){
                prices.remove(i);
            }
        }

        for (int i = 0; i < prices.size(); i++) {
            System.out.println(prices.get(i).getText());
        }
*/

    }

    @And("^I go to details of (.*)$")
    public void goToDetails(String expectedTitle) {
        WebElement firstResultTitle = chromeDriver.findElement(By.xpath
                ("//h2[@class='a-size-mini a-spacing-none a-color-base s-line-clamp-2']//a//span"));

        firstResultTitle.click();

        WebDriverWait wait = new WebDriverWait(chromeDriver, 3);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("productTitle")));
        WebElement actualTitle = chromeDriver.findElement((By.id("productTitle")));

        Assert.assertEquals(expectedTitle, actualTitle.getText());

        List<WebElement> bookOptions = chromeDriver.findElements(By.className
                ("a-unordered-list a-nostyle a-button-list a-horizontal"));

        WebElement paperback = chromeDriver.findElement(By.xpath
                ("//ul[@class='a-unordered-list a-nostyle a-button-list a-horizontal']//li[3]//span//span//span//a//span[2]"));
        String actualPaperbackPrice = paperback.getText();
        paperback.click();
        //wait.until(ExpectedConditions.presenceOfElementLocated(By.id("productTitle")));

        Assert.assertEquals(this.paperbackPrice, actualPaperbackPrice);

    }

    @And("^I add to basket$")
    public void addToBasket() {
        WebDriverWait wait = new WebDriverWait(chromeDriver, 3);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("add-to-cart-button")));
        this.oldPageTitle = chromeDriver.getTitle();

        WebElement addToBasket = chromeDriver.findElement(By.id("add-to-cart-button"));
        int elementPosition = addToBasket.getLocation().getY();
        String js = String.format("window.scroll(0, %s)", elementPosition);
        ((JavascriptExecutor) chromeDriver).executeScript(js);
        addToBasket.click();
        this.quantityItems++;

        this.newPageTitle = chromeDriver.getTitle();
        Assert.assertNotEquals(this.oldPageTitle, this.newPageTitle);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("huc-v2-order-row-with-divider")));
        WebElement notificationPanel = chromeDriver.findElement(By.id("huc-v2-order-row-with-divider"));
        if (notificationPanel.isDisplayed()) {

           /* String script = "return window.getComputedStyle(document." +
                    "querySelector('.a-size-medium.a-text-bold')).getPropertyValue('content')";
            JavascriptExecutor jse = (JavascriptExecutor) chromeDriver;
            String content = (String) jse.executeScript(script);*/

            WebElement notificationBasketTitle = chromeDriver.
                    findElement(By.xpath("//div[@id='huc-v2-order-row-confirm-text']//h1"));
            Assert.assertEquals(notificationBasketTitle.getText(), "Added to Basket");

            /*WebElement quantityItemsWebElement = chromeDriver.findElement(By.xpath
                    ("//span[@class='a-size-medium.a-align-center.huc-subtotal']//span"));
            String quantityItemsString = quantityItemsWebElement.getText();
            int actualQuantityItems = Integer.parseInt(quantityItemsString.replace("[^0-9]", ""));

            Assert.assertEquals(actualQuantityItems, this.quantityItems);*/
        }
    }

    @And("^I edit basket$")
    public void editBasket() {
        this.oldPageUrl = chromeDriver.getCurrentUrl();
        WebElement editBasket = chromeDriver.findElement(By.id("hlb-view-cart-announce"));

        editBasket.click();
        this.newPageUrl = chromeDriver.getCurrentUrl();

        Assert.assertNotEquals(this.newPageUrl, this.oldPageUrl);

        /*
        String script = "return window.getComputedStyle(document." +
                "querySelector('.a-size-medium.sc-product-title.a-text-bold')).getPropertyValue('content')";
        JavascriptExecutor js = (JavascriptExecutor) chromeDriver;
        String content = (String) js.executeScript(script);

        WebDriverWait wait = new WebDriverWait(chromeDriver, 3);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className
                ("a-size-medium.sc-product-title.a-text-bold")));
        */

        /*
        WebElement itemTitle = chromeDriver.findElement(By.className
                ("a-size-medium sc-product-title a-text-bold"));
        WebElement itemType = chromeDriver.findElement(By.className
                ("a-size-small a-color-secondary sc-product-binding"));
        WebElement itemPrice = chromeDriver.findElement(By.className
                ("a-size-medium a-color-base sc-price sc-white-space-nowrap sc-product-price a-text-bold"));
        WebElement itemQuantity = chromeDriver.findElement(By.className
                ("a-dropdown-prompt"));
        WebElement totalPrice = chromeDriver.findElement(By.id
                ("//span[@id='sc-subtotal-amount-activecart']//span"));
        */

    }

    @Then("^I stop Chrome browser$")
    public void stopBrowser() {
        chromeDriver.close();
    }

    private void acceptCookies() {
        try {
            WebElement cookiesButton = chromeDriver.findElement(By.id("sp-cc-accept"));

            if (cookiesButton.isDisplayed()) {
                cookiesButton.click();
            }
        } catch (NoSuchElementException e) {

        }
    }

    private boolean isOldPrice(WebElement element) {
        boolean result = false;
        try {
            String value = element.getAttribute("data-a-strike");
            if (value.equalsIgnoreCase("true")) {
                result = true;
            }
        } catch (Exception e) {
        }

        return result;

    }
}
