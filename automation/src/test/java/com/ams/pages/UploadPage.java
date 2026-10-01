package com.ams.pages;

import com.ams.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

/**
 * The file upload screens. Campaign Lead Bulk Upload, SLII Exam Management and
 * Blacklisting all share the same layout: an uploader, a "Download Sample Template"
 * link, an UPLOAD button and a table of uploaded files with a View (eye) icon.
 */
public class UploadPage extends BasePage {

    private final By fileInput = By.cssSelector("main input[type='file']");
    private final By uploadButton = By.xpath("//main//button[starts-with(normalize-space(),'UPLOAD')]");
    private final By templateLink = By.xpath("//button[normalize-space()='Download Sample Template']");
    private final By uploadedRows = By.xpath("(//main//table)[1]/tbody/tr[count(td)>=3]");
    private final By viewPopupClose = By.xpath("//button[normalize-space()='×']");

    public UploadPage(WebDriver driver) {
        super(driver);
    }

    /** Text shown in the upload box, e.g. "Please upload .xlsx or .xls or .csv files only". */
    public String uploadInstruction() {
        return visible(By.xpath("//main//p[starts-with(normalize-space(),'Please upload')]")).getText();
    }

    /** True when the uploader (Download Sample Template + UPLOAD) is shown within a few seconds. */
    public boolean isUploaderShown() {
        return appearsWithin(templateLink, 8);
    }

    /** Reloads the page and waits for the uploader to be shown again. */
    public void reload() {
        driver.navigate().refresh();
        visible(templateLink);
        waitForTableLoaded();
    }

    public boolean isUploadEnabled() {
        return visible(uploadButton).isEnabled();
    }

    /** Selects a file in the uploader (without clicking UPLOAD). */
    public void chooseFile(Path file) {
        wait.until(ExpectedConditions.presenceOfElementLocated(fileInput)).sendKeys(file.toAbsolutePath().toString());
    }

    /**
     * Selects the file, clicks UPLOAD and returns the message shown in the result pop-up.
     * Files with an unsupported extension are refused as soon as they are selected; in that
     * case the red message under the uploader is returned and no pop-up is shown.
     */
    public String upload(Path file) {
        chooseFile(file);
        wait.until(d -> isUploadEnabled() || popupShown() || inlineError() != null);
        if (inlineError() != null && !isUploadEnabled()) {
            return inlineError();
        }
        if (!popupShown()) {
            click(uploadButton);
        }
        wait.until(d -> popupShown());
        return popupMessage();
    }

    /** Red message under the uploader (e.g. "Unsupported file format ..."), or null. */
    public String inlineError() {
        return driver.findElements(By.xpath("//main//*[contains(text(),'Unsupported file format') or contains(text(),'File size')]")).stream()
                .filter(e -> e.isDisplayed()).map(e -> e.getText()).findFirst().orElse(null);
    }

    /** Closes the result pop-up if one is shown (after upload(...) returned a pop-up message). */
    public void closePopupIfShown() {
        if (popupShown()) {
            clickOk();
        }
    }

    private boolean popupShown() {
        return !driver.findElements(POPUP_OK).stream().filter(e -> e.isDisplayed()).toList().isEmpty();
    }

    /** Clicks "Download Sample Template" and waits for a new file in the download folder. */
    public Path downloadTemplate() throws Exception {
        long clickedAt = System.currentTimeMillis();
        click(templateLink);
        Path folder = BaseTest.DOWNLOAD_DIR;
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> newestDownload(folder, clickedAt) != null);
        return newestDownload(folder, clickedAt);
    }

    private static Path newestDownload(Path folder, long after) {
        try (Stream<Path> files = Files.list(folder)) {
            return files.filter(p -> !p.toString().endsWith(".crdownload") && !p.toString().endsWith(".tmp"))
                    .filter(p -> p.toFile().lastModified() >= after - 1000)
                    .findFirst().orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    // ---------- Uploaded files table ----------

    /** Each row as "file name | date of upload". Newest uploads are listed first. */
    public List<String> uploadedFiles() {
        waitForTableLoaded();
        showMaxRows();
        return driver.findElements(uploadedRows).stream()
                .map(r -> r.findElements(By.tagName("td")))
                .map(c -> c.get(0).getText().trim() + " | " + c.get(1).getText().trim())
                .toList();
    }

    /** Opens the View (eye) popup of an uploaded file row (0 = newest) and returns its text. */
    /** Opens the View popup of the newest upload with this file name and returns its text. */
    public String viewUploadedFile(String fileName) {
        int index = indexOfFile(fileName);
        if (index < 0) {
            throw new AssertionError("File '" + fileName + "' is not in the uploaded files list");
        }
        return viewUploadedFile(index);
    }

    /** Number of rows in the uploaded-files list with this exact file name. */
    public long countOfFile(String fileName) {
        return uploadedFiles().stream().filter(r -> r.startsWith(fileName + " |")).count();
    }

    public String viewUploadedFile(int rowIndex) {
        WebElement row = wait.until(d -> d.findElements(uploadedRows).size() > rowIndex ? d.findElements(uploadedRows).get(rowIndex) : null);
        // The eye icon is in the last column
        jsClick(row.findElement(By.xpath("./td[last()]//*[self::img or local-name()='svg' or self::button]")));
        WebElement popup = visible(By.xpath("//p[contains(.,'total records')]/ancestor::div[3]"));
        wait.until(d -> !popup.getText().contains("Loading"));
        return popup.getText();
    }

    public void closeViewPopup() {
        click(viewPopupClose);
    }

    /** Helper to find an uploaded row index by file name. Returns -1 when missing. */
    public int indexOfFile(String fileName) {
        List<String> rows = uploadedFiles();
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).startsWith(fileName + " |")) {
                return i;
            }
        }
        return -1;
    }

    public static String fileName(Path file) {
        return new File(file.toString()).getName();
    }
}
