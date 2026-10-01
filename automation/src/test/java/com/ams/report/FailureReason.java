package com.ams.report;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Turns a test failure into a sentence a tester can understand, for Actual Result and Remarks, e.g.
 * "Step 'approve in tray' (Lead page, pick schedule date): the screen did not show the expected result within 25 seconds."
 * The step names come from the automation code itself (method names of the flows and pages).
 */
public final class FailureReason {

    private FailureReason() {
    }

    /** Plain-language reason of the failure (at most about 400 characters). */
    public static String of(Throwable error) {
        if (error == null) {
            return "The test case stopped for an unknown reason.";
        }
        String problem = problem(error);
        String where = where(error);
        String text = where.isEmpty() ? problem : where + ": " + lowerFirst(problem);
        return text.length() > 400 ? text.substring(0, 400) + "..." : text;
    }

    /** What went wrong, without Selenium / Java details. */
    private static String problem(Throwable error) {
        String name = error.getClass().getSimpleName();
        String message = clean(String.valueOf(error.getMessage()));
        if (error instanceof AssertionError) {
            // TestNG adds "expected [true] but found [false]" to the tester's own message
            message = message.replaceAll("\\s*expected \\[.*?\\] but found \\[.*?\\]", "").trim();
            return message.isEmpty() ? "A check of the expected result did not pass." : sentence(message);
        }
        return switch (name) {
            case "TimeoutException" -> "the screen did not show the expected result within " + seconds(message) + ".";
            case "NoSuchElementException" -> "a field or button the test needs is not on the screen.";
            case "ElementNotInteractableException" -> "a field or button is on the screen but could not be used (hidden or disabled).";
            case "ElementClickInterceptedException" -> "the click did not reach the button because a pop-up or another element was covering it.";
            case "StaleElementReferenceException" -> "the screen refreshed while the test was using it.";
            case "NoSuchSessionException", "NoSuchWindowException", "UnreachableBrowserException", "WebDriverException"
                    -> "the Chrome browser closed or stopped responding during the test (a test environment problem, not an application defect).";
            case "SessionNotCreatedException" -> "the Chrome browser could not be started (a test environment problem, not an application defect).";
            case "UnhandledAlertException" -> "an unexpected browser alert was shown: " + message;
            case "SQLException", "SQLSyntaxErrorException", "CommunicationsException"
                    -> "the AMS database could not be read: " + message;
            default -> "the test stopped with an error (" + name + "): " + message;
        };
    }

    /**
     * Where it happened: the business step (flow method) and the screen action (page method),
     * taken from the first automation code lines in the stack trace.
     */
    private static String where(Throwable error) {
        String flowStep = null;
        String pageAction = null;
        for (StackTraceElement frame : error.getStackTrace()) {
            String cls = frame.getClassName();
            if (cls.contains("$$Lambda") || frame.getMethodName().startsWith("lambda$")) {
                // A wait condition inside a page method: its method name tells what was awaited
                String owner = frame.getMethodName().startsWith("lambda$") ? frame.getMethodName().split("\\$")[1] : null;
                if (pageAction == null && owner != null && cls.startsWith("com.ams.pages.")) {
                    pageAction = page(cls) + ", " + words(owner);
                }
                continue;
            }
            if (pageAction == null && cls.startsWith("com.ams.pages.") && !cls.endsWith("BasePage")) {
                pageAction = page(cls) + ", " + words(frame.getMethodName());
            } else if (flowStep == null && cls.startsWith("com.ams.utils.") && cls.endsWith("Flows")
                    && !frame.getMethodName().startsWith("expect")) {
                flowStep = words(frame.getMethodName());
            }
        }
        if (flowStep == null && pageAction == null) {
            return "";
        }
        if (flowStep == null) {
            return "On the " + pageAction.replaceFirst(", ", " (") + ")";
        }
        return "Step '" + flowStep + "'" + (pageAction == null ? "" : " (" + pageAction + ")");
    }

    /** "com.ams.pages.LeadPage" -> "Lead page" */
    private static String page(String className) {
        String simple = words(className.substring(className.lastIndexOf('.') + 1).split("\\$")[0]);
        return Character.toUpperCase(simple.charAt(0)) + simple.substring(1);
    }

    /** "pickScheduleDate" -> "pick schedule date" */
    private static String words(String camelCase) {
        return camelCase.replaceAll("([a-z0-9])([A-Z])", "$1 $2").replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2")
                .toLowerCase(Locale.ENGLISH);
    }

    private static String seconds(String message) {
        Matcher m = Pattern.compile("tried for (\\d+) second").matcher(message);
        return m.find() ? m.group(1) + " seconds" : "the waiting time";
    }

    /** Removes Selenium's build / session / driver details and joins the lines. */
    private static String clean(String message) {
        for (String marker : new String[]{"\nBuild info", "(Session info", "\nFor documentation", "\nSystem info"}) {
            int cut = message.indexOf(marker);
            if (cut > 0) {
                message = message.substring(0, cut);
            }
        }
        return message.replace('\n', ' ').replaceAll("\\s+", " ").trim();
    }

    private static String sentence(String text) {
        return text.endsWith(".") || text.endsWith("!") || text.endsWith("?") ? text : text + ".";
    }

    /** "The screen ..." -> "the screen ...", but abbreviations such as "HOB approval" stay as they are. */
    private static String lowerFirst(String text) {
        if (text.isEmpty() || (text.length() > 1 && Character.isUpperCase(text.charAt(1)))) {
            return text;
        }
        return Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }
}
