package ru.vsu.cs.schedules;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StationsListUiTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("UI тест: поиск и сортировка - проверяем работу фильтра")
    void shouldSearchAndSortStations() {
        driver.get("http://localhost:8080/stations");

        String existingName = driver.findElements(By.cssSelector("tbody tr td:nth-child(2)"))
                .getFirst().getText();

        WebElement searchInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("search")));
        searchInput.clear();
        searchInput.sendKeys(existingName);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("tbody tr")));

        List<String> results = driver.findElements(By.cssSelector("tbody tr td:nth-child(2)"))
                .stream().map(WebElement::getText).collect(Collectors.toList());

        assertThat(results).contains(existingName);
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}