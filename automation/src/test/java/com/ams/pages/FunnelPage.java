package com.ams.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Recruitment Leads Mgmt > Recruitment Funnel (Agency Admin).
 * The funnel is a chart: stage names are on the right and the counts are in the funnel,
 * each count on the same height as its stage name.
 */
public class FunnelPage extends BasePage {

    private final By svgTexts = By.cssSelector("main svg text");

    public FunnelPage(WebDriver driver) {
        super(driver);
    }

    /** Stage name (e.g. "HOB INTERVIEW") -> count shown on the funnel, once the chart has finished loading. */
    public Map<String, Long> stageCounts() {
        // The stages are filled one after another; wait until the PROSPECT count is shown
        // and the same counts are read on two checks in a row
        List<Map<String, Long>> previous = new ArrayList<>();
        return wait.until(d -> {
            Map<String, Long> counts;
            try {
                counts = readCounts();
            } catch (AssertionError notDrawnYet) {
                return null;
            }
            boolean stable = !previous.isEmpty() && previous.get(0).equals(counts);
            previous.clear();
            previous.add(counts);
            return stable && counts.getOrDefault("PROSPECT", 0L) > 0 ? counts : null;
        });
    }

    private Map<String, Long> readCounts() {
        List<WebElement> labels = new ArrayList<>();
        List<WebElement> numbers = new ArrayList<>();
        for (WebElement text : driver.findElements(svgTexts)) {
            String value = textOf(text);
            if (value.matches("[0-9,]+")) {
                numbers.add(text);
            } else if (value.matches("[A-Z ]+") && !value.isBlank()) {
                labels.add(text);
            }
        }
        if (numbers.isEmpty()) {
            throw new AssertionError("No counts found on the Recruitment Funnel");
        }

        // Each count belongs to the stage name closest to it vertically
        Map<String, Long> counts = new LinkedHashMap<>();
        for (WebElement label : labels) {
            WebElement closest = null;
            for (WebElement number : numbers) {
                if (closest == null || distance(label, number) < distance(label, closest)) {
                    closest = number;
                }
            }
            counts.put(textOf(label), Long.parseLong(textOf(closest).replace(",", "")));
        }
        return counts;
    }

    /** Text of a chart label; names such as "SUPERVISOR INTERVIEW" are split over two lines (tspan). */
    private static String textOf(WebElement svgText) {
        List<String> lines = svgText.findElements(By.tagName("tspan")).stream().map(t -> t.getText().trim()).toList();
        String text = lines.isEmpty() ? svgText.getText() : String.join(" ", lines);
        return text.replaceAll("\\s+", " ").trim();
    }

    private static double distance(WebElement a, WebElement b) {
        return Math.abs(centerY(a.getRect()) - centerY(b.getRect()));
    }

    private static double centerY(Rectangle r) {
        return r.getY() + r.getHeight() / 2.0;
    }
}
