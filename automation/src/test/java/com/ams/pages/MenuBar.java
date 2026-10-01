package com.ams.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/** Side menu, top bar (notifications, logged-in role) and logout. */
public class MenuBar extends BasePage {

    private final By openDrawerButton = By.cssSelector("button[aria-label='open drawer']");
    private final By notificationsButton = By.xpath("//img[@alt='notifications']/ancestor::button");
    private final By notificationsPanel = By.cssSelector(".MuiPopover-paper");
    private final By pageTitle = By.cssSelector("header h6");

    public MenuBar(WebDriver driver) {
        super(driver);
    }

    /** Opens a side-menu entry such as "Recruitment Leads Mgmt" or "Approval Tray". */
    public void open(String menuName) {
        By menuItem = By.xpath("//nav//*[normalize-space(text())='" + menuName + "']");
        // The side menu can be collapsed; expand it first if needed
        if (!isDisplayed(menuItem) && isDisplayed(openDrawerButton)) {
            click(openDrawerButton);
        }
        click(menuItem);
        wait.until(ExpectedConditions.textToBe(pageTitle, menuName));
    }

    /** True when the side menu contains the given entry for the logged-in user. */
    public boolean hasMenu(String menuName) {
        By menuItem = By.xpath("//nav//*[normalize-space(text())='" + menuName + "']");
        return !driver.findElements(menuItem).isEmpty();
    }

    /** Opens one of the cards on the Recruitment Leads Mgmt page, e.g. "Blacklisting". */
    public void openRecruitmentCard(String cardName) {
        open("Recruitment Leads Mgmt");
        click(By.cssSelector("main img[alt='" + cardName + "']"));
        wait.until(ExpectedConditions.textToBe(pageTitle, cardName));
    }

    /** Names of the cards shown on the Recruitment Leads Mgmt page (empty for users that only see the campaign list). */
    public List<String> recruitmentCards() {
        open("Recruitment Leads Mgmt");
        // Wait for either the cards or the campaign table
        wait.until(d -> !d.findElements(By.cssSelector("main img[alt='Campaign Management'], main table")).isEmpty());
        return driver.findElements(By.cssSelector("main button img")).stream()
                .map(img -> img.getDomAttribute("alt"))
                .toList();
    }

    /** Opens the notification panel, returns its text and closes the panel again. */
    public String notificationsText() {
        click(notificationsButton);
        WebElement panel = visible(notificationsPanel);
        // Wait until the notification entries have loaded
        wait.until(d -> {
            String text = panel.getText().toLowerCase();
            return !text.contains("loading") && (text.contains(" for ") || text.contains("no notification"));
        });
        String text = panel.getText();
        // Close the panel by clicking on the background around it
        jsClick(driver.findElement(By.cssSelector(".MuiPopover-root .MuiBackdrop-root")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(notificationsPanel));
        return text;
    }

    /** Role name shown in the top-right corner, e.g. "Agency Admin". */
    public String loggedInRole() {
        return visible(By.xpath("//img[@alt='profile']/following-sibling::div/p[2]")).getText();
    }
}
