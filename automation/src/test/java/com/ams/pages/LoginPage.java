package com.ams.pages;

import com.ams.base.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/** The AMS login screen. */
public class LoginPage extends BasePage {

    private final By userIdField = By.name("userId");
    private final By passwordField = By.name("password");
    private final By loginButton = By.cssSelector("button[type='submit']");
    private final By sideMenu = By.cssSelector("nav[aria-label='Main navigation']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /** Opens the application and logs in with the common password. */
    public void loginAs(String userId) {
        driver.get(Config.baseUrl() + "/login");
        // Make sure no previous user session is re-used
        ((JavascriptExecutor) driver).executeScript("localStorage.clear(); sessionStorage.clear();");
        driver.manage().deleteAllCookies();
        driver.get(Config.baseUrl() + "/login");
        trackRequests();

        // Enter user ID and password
        type(userIdField, userId);
        type(passwordField, Config.password());

        // Click Login and wait for the side menu of the home page
        click(loginButton);
        visible(sideMenu);
    }

    /** Tries to log in (e.g. with a new agent's sales code) and returns true when the home page opens. */
    public boolean canLogin(String userId) {
        driver.get(Config.baseUrl() + "/login");
        ((JavascriptExecutor) driver).executeScript("localStorage.clear(); sessionStorage.clear();");
        driver.manage().deleteAllCookies();
        driver.get(Config.baseUrl() + "/login");
        type(userIdField, userId);
        type(passwordField, Config.password());
        click(loginButton);
        return appearsWithin(sideMenu, 15);
    }
}
