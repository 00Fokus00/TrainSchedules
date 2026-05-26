package ru.vsu.cs.schedules;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainDashboardUiTest {

    private WebDriver driver;

    @BeforeAll
    void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @Test
    @DisplayName("UI тест: проверка доступности всех API ссылок")
    void shouldNavigateToAllApiEndpoints() {
        driver.get("http://localhost:8080/");

        assertThat(driver.findElement(By.tagName("h1")).getText()).contains("Railway Schedules System");

        String[] endpoints = {"stations", "trains", "locomotives", "schedules", "train-carriages", "station-entries"};

        for (String endpoint : endpoints) {
            driver.findElement(By.cssSelector("a[href='/api/" + endpoint + "']")).click();

            assertThat(driver.getCurrentUrl()).contains("/api/" + endpoint);

            driver.navigate().back();
        }
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}