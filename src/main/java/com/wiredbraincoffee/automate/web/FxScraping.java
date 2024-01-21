package com.wiredbraincoffee.automate.web;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FxScraping implements AutoCloseable {

    private final WebDriver driver;

    public FxScraping(String browser){
        if (browser.equals("chrome")){
            System.setProperty("webdriver.chrome.driver", "automate-web/chromedriver");
            ChromeOptions chromeOptions = new ChromeOptions().addArguments("--disable-gpu", "--window-size=800,900", "--ignore-certificate-errors", "--silent");
            this.driver = new ChromeDriver(chromeOptions);
        } else if (browser.equals("firefox")) {
            System.setProperty("webdriver.gecko.driver", "automate-web/geckodriver");
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            driver = new FirefoxDriver(firefoxOptions);
        } else {
                throw new IllegalArgumentException("Unknown browser");
        }
    }

    public double convertCurrency(LocalDate fxDate, VisaUKCurrency fromCurrency, VisaUKCurrency toCurrency) {
        // Test name: Convert Currency
        // Step # | name | target | value
        // 1 | open | /support/consumer/travel-support/exchange-rate-calculator.html |
        driver.get("https://www.visa.co.uk/support/consumer/travel-support/exchange-rate-calculator.html");
        // 2 | setWindowSize | 800x900 |
        driver.manage().window().setSize(new Dimension(800, 900));
        try {
            Thread.sleep(750);
            // 3 | click | linkText=Accept |
            WebElement buttonPrivacyAccept = driver.findElement(By.linkText("Accept"));
            buttonPrivacyAccept.click();
        } catch (NoSuchElementException | InterruptedException e) {
            // privacy policy already accepted in the past for this browser
        }
        // 5 | type | id=amount | 1
        driver.findElement(By.id("amount")).clear();
        driver.findElement(By.id("amount")).sendKeys("1");
        // 7 | type | id=fee | 2.5
        driver.findElement(By.id("fee")).clear();
        driver.findElement(By.id("fee")).sendKeys("0.75");
        // 18 | type | id=exchangedate | 09/20/2019
        driver.findElement(By.id("exchangedate")).clear();
        driver.findElement(By.id("exchangedate")).sendKeys(fxDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        // 19 | click | css=.input-mobile .input-group-addon |
        driver.findElement(By.cssSelector(".input-mobile .input-group-addon")).click();
        // 20 | click | linkText=British Pound (GBP) |
        driver.findElement(By.linkText(toCurrency.getLabel())).click();
        // 21 | click | css=.input-group-addon:nth-child(2) |
        driver.findElement(By.cssSelector(".input-group-addon:nth-child(2)")).click();
        // 22 | click | linkText=Euro (EUR) |
        driver.findElement(By.linkText(fromCurrency.getLabel())).click();
        // 23 | click | id=submitButton |
        driver.findElement(By.id("submitButton")).click();
        // 25 | click | css=.converted-amount-value:nth-child(1) |
        //String markUp = driver.findElement(By.cssSelector("strong.converted-amount-value:nth-child(4)")).getText();
        String rateString = driver.findElement(By.cssSelector("strong.converted-amount-value:nth-child(7)")).getText();
        Matcher rateMatcher = Pattern.compile("^(\\d+\\.\\d+) " + toCurrency.getCurrencyName() + "$").matcher(rateString);
        if (rateMatcher.matches()) {
            return Double.parseDouble(rateMatcher.group(1));
        } else {
            throw new IllegalStateException("Page did not return the expected response");
        }
    }

    @Override
    public void close(){ driver.quit(); }

    public static void main(String[] args) {
        Set<VisaUKCurrency> ratesFrom = new HashSet<>();
        ratesFrom.add(VisaUKCurrency.EUR);
        ratesFrom.add(VisaUKCurrency.JPY);
        ratesFrom.add(VisaUKCurrency.CAD);
        VisaUKCurrency rateTo = VisaUKCurrency.GBP;
        YearMonth yearMonth = YearMonth.of(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        int lastDayOfMonth = yearMonth.atEndOfMonth().getDayOfMonth();
        Map<LocalDate, Map<VisaUKCurrency,Double>> rates = new HashMap<>();
        try (FxScraping fxScrapper = new FxScraping("chrome")) {
            for (int day = 1; day <= lastDayOfMonth; day++) {
                LocalDate localDate = yearMonth.atDay(day);
                HashMap<VisaUKCurrency, Double> ratesForDay = new HashMap<>();
                for (VisaUKCurrency rateFrom : ratesFrom) {
                    int retries = 3;
                    while (retries > 0) try {
                        double rate = fxScrapper.convertCurrency(localDate, rateFrom, rateTo);
                        ratesForDay.put(rateFrom, rate);
                        retries = 0;
                    } catch (NoSuchElementException e) {
                        if (--retries <= 0)
                            throw new IllegalStateException("Exceeded number of retries (" + e.getMessage() + ")");
                    }
                }
                rates.put(localDate, ratesForDay);
            }
        }
        try (FileOutputStream fos = new FileOutputStream("automate/datasink/rates-" + yearMonth + ".ser")) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(rates);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
