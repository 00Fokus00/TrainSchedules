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
class ScheduleUiTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("UI тест: успешное создание расписания")
    void shouldCreateScheduleViaUi() {
        driver.get("http://localhost:8080/schedules/new");

        WebElement trainDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("train")));
        Select trainSelect = new Select(trainDropdown);
        trainSelect.selectByIndex(1);

        WebElement departureInput = driver.findElement(By.id("departureTime"));
        departureInput.clear();
        departureInput.sendKeys("2026-05-25T18:00");

        WebElement arrivalInput = driver.findElement(By.id("arrivalTime"));
        arrivalInput.clear();
        arrivalInput.sendKeys("2026-05-25T20:00");

        WebElement submitButton = driver.findElement(By.id("submitButton"));
        wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();

        wait.until(ExpectedConditions.urlContains("/schedules"));
        assertThat(driver.getCurrentUrl()).contains("/schedules");
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}