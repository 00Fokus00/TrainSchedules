package ru.vsu.cs.schedules;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NewTrainUiTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("UI тест: успешное создание поезда")
    void shouldCreateTrainViaUi() {
        driver.get("http://localhost:8080/trains/new");

        WebElement numberInput = driver.findElement(By.id("number"));
        numberInput.sendKeys("Express-100");

        WebElement speedInput = driver.findElement(By.id("maxSpeed"));
        speedInput.sendKeys("250");

        WebElement locomotiveSelect = driver.findElement(By.id("locomotive"));
        Select select = new Select(locomotiveSelect);
        select.selectByIndex(1);

        WebElement saveButton = driver.findElement(By.cssSelector("button[type='submit']"));
        saveButton.click();

        wait.until(ExpectedConditions.urlContains("/trains"));
        assertThat(driver.getCurrentUrl()).contains("/trains");
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}