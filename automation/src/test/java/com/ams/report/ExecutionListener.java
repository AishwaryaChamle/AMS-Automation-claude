package com.ams.report;

import com.ams.base.BaseTest;
import com.ams.base.Blocked;
import com.ams.base.Config;
import org.testng.IExecutionListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Collects the result of every test class (class name = TestCase ID).
 * When "mvn clean test" starts, the previous report files are deleted and a fresh
 * Execution_Reports/testcases_result.xlsx is created; it is saved again after every test case.
 */
public class ExecutionListener implements ITestListener, IExecutionListener {

    private static final Map<String, ExecutionResult> RESULTS = java.util.Collections.synchronizedMap(new LinkedHashMap<>());

    @Override
    public void onTestSuccess(ITestResult result) {
        recordPass(result);
    }

    /**
     * Passed = the expected result happened: write it in the words of the Expected Result.
     * Remarks stay blank for a passed test case (they are only written for a failed one).
     */
    private void recordPass(ITestResult result) {
        String testCaseId = result.getTestClass().getRealClass().getSimpleName();
        String observed = attribute(result, "actual", "All checks passed.");
        String expected = ExecutedSuiteWriter.expectedResultOf(testCaseId);
        String actual = expected.isBlank() ? observed : expected;
        record(result, "Pass", actual, "");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Written in plain words (which step, what went wrong), see FailureReason
        String reason = FailureReason.of(result.getThrowable());
        String actual = attribute(result, "actual", "Execution stopped. " + reason);
        recordFailure(result, actual, reason);
    }

    /** A failed test case is recorded as Fail straight away (not asked), with the reason in Remarks. */
    private void recordFailure(ITestResult result, String actual, String remark) {
        record(result, "Fail", actual, remark);
    }

    /**
     * A blocked test case: the execution pauses and the tester confirms it (see FailureConfirmation):
     * keep it Blocked, or change it to Pass or Fail (Fail gets the reason in Remarks). Then the execution resumes.
     */
    private void recordConfirmed(ITestResult result, String status, String actual, String remark) {
        if (FailureConfirmation.enabled()) {
            String testCaseId = result.getTestClass().getRealClass().getSimpleName();
            // The browser of a failed test case is still open here: show the tester what it shows now
            byte[] screen = result.getInstance() instanceof BaseTest test ? test.screenshotNow() : null;
            FailureConfirmation.Decision decision = FailureConfirmation.ask(testCaseId, status,
                    ExecutedSuiteWriter.expectedResultOf(testCaseId), actual, remark, screen);
            if ("Pass".equals(decision.status())) {
                recordPass(result);
                return;
            }
            status = decision.status();
            remark = decision.reason();
        }
        record(result, status, actual, remark);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Throwable reason = result.getThrowable();
        String message = reason == null ? "" : String.valueOf(reason.getMessage());
        if (message.startsWith(Blocked.PREFIX)) {
            String why = message.substring(Blocked.PREFIX.length());
            recordConfirmed(result, "Blocked", "Not executed: " + why, why);
        } else if (message.startsWith(Blocked.INVALID_PREFIX)) {
            String why = message.substring(Blocked.INVALID_PREFIX.length());
            record(result, "Invalid", "Not executed: " + why, "Invalid. " + why);
        } else {
            String why = "The test case could not start: " + FailureReason.of(reason);
            recordFailure(result, why, why);
        }
    }

    /** Result file of this execution: Execution_Reports/testcases_result.xlsx. */
    private static final Path EXECUTION_REPORT = ExecutedSuiteWriter.resultFile();

    /** Live status page next to the result file (same name, .html). */
    private static final Path LIVE_PAGE = Path.of(EXECUTION_REPORT.toString().replaceAll("\\.xlsx$", ".html"));

    /** Test case executed most recently (the file opens on its row). */
    private static volatile String lastExecuted;

    /** What the result file shows during this run. */
    private static final Map<String, ExecutionResult> REPORT = new LinkedHashMap<>();

    /**
     * As soon as "mvn clean test" starts:
     * 1. delete the report files of earlier runs (Execution_Reports and old failure screenshots),
     * 2. create a fresh testcases_result.xlsx where every test case is "Not Executed",
     * 3. open the live status page (testcases_result.html) in the browser; it refreshes every
     *    5 seconds and shows each test case result as soon as the test case has been executed.
     * After each test case the result is written into testcases_result.xlsx and the live page.
     */
    @Override
    public void onExecutionStart() {
        // If the run is stopped (Ctrl+C / Stop in VS Code, or VS Code is closed), still save the result file
        Runtime.getRuntime().addShutdownHook(new Thread(ExecutionListener::finishReport, "save-results-on-stop"));
        deletePreviousReports();
        writeReport();
        writeLivePage(false);
        if (showReports()) {
            ReportOpener.openInBrowser(LIVE_PAGE);
            // Opens the result file in the spreadsheet program once this run has ended, however it ended
            // (even when VS Code is closed and the run is killed: the file is saved after every test case)
            ReportOpener.openWhenRunEnds(EXECUTION_REPORT);
        }
    }

    /** After the run: save the result file once more (the opener started at the beginning then opens it). */
    @Override
    public void onExecutionFinish() {
        finishReport();
    }

    private static final java.util.concurrent.atomic.AtomicBoolean FINISHED = new java.util.concurrent.atomic.AtomicBoolean();

    /**
     * Saves the final result file. Runs once: when the execution is done, or when it is stopped
     * early (then the test cases not reached stay "Not Executed"). When this process has ended,
     * the watcher started in onExecutionStart opens the file in the spreadsheet program.
     */
    private static void finishReport() {
        if (!FINISHED.compareAndSet(false, true)) {
            return;
        }
        BaseTest.closeOpenBrowser(); // stopped in the middle of a test case: do not leave Chrome open
        writeReport(lastExecuted);
        writeLivePage(true);
        System.out.println("Execution result file saved: " + EXECUTION_REPORT);
    }

    /**
     * Stores one test case result and immediately saves the result file
     * (and the saved results), so the file fills in test case by test case.
     */
    private void record(ITestResult result, String status, String actual, String remark) {
        String testCaseId = result.getTestClass().getRealClass().getSimpleName();
        ExecutionResult executed = new ExecutionResult(testCaseId, status, actual, remark,
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        RESULTS.put(testCaseId, executed);
        synchronized (REPORT) {
            REPORT.put(testCaseId, executed);
        }
        saveResult(executed);
        lastExecuted = testCaseId;
        writeReport(testCaseId);
        writeLivePage(false);
        System.out.println("Result saved to " + EXECUTION_REPORT.getFileName() + ": " + testCaseId + " = " + status);
    }

    /** Deletes the report files of earlier executions so every run starts with a fresh result file. */
    private static void deletePreviousReports() {
        int deleted = 0;
        for (Path folder : new Path[]{ExecutedSuiteWriter.reportsFolder(), Path.of("screenshots")}) {
            if (!java.nio.file.Files.isDirectory(folder)) {
                continue;
            }
            try (java.util.stream.Stream<Path> files = java.nio.file.Files.list(folder)) {
                for (Path file : files.filter(java.nio.file.Files::isRegularFile).toList()) {
                    java.nio.file.Files.deleteIfExists(file);
                    deleted++;
                }
            } catch (Exception e) {
                System.out.println("Could not delete old reports in " + folder + ": " + e.getMessage());
            }
        }
        System.out.println("Deleted " + deleted + " report file(s) of earlier runs");
    }

    /** Keeps the result for later partial runs (automation/results/execution-results.tsv). */
    private static synchronized void saveResult(ExecutionResult executed) {
        try {
            Map<String, ExecutionResult> all = ResultStore.load();
            all.put(executed.testCaseId(), executed);
            ResultStore.save(all);
        } catch (Exception e) {
            throw new IllegalStateException("Could not save the result of " + executed.testCaseId(), e);
        }
    }

    /** Rewrites this execution's result file. */
    private static void writeReport() {
        writeReport(null);
    }

    /** Rewrites this execution's result file, opening on the row of the given test case. */
    private static void writeReport(String focusTestCaseId) {
        synchronized (REPORT) {
            try {
                ExecutedSuiteWriter.write(new LinkedHashMap<>(REPORT), EXECUTION_REPORT, focusTestCaseId);
            } catch (Exception e) {
                throw new IllegalStateException("Could not write " + EXECUTION_REPORT, e);
            }
        }
    }

    /** Updates the live status page with the test cases executed so far. */
    private static void writeLivePage(boolean finished) {
        synchronized (REPORT) {
            try {
                LiveStatusPage.write(LIVE_PAGE, EXECUTION_REPORT, ExecutedSuiteWriter.lastTable(), lastExecuted,
                        TestCaseSelector.plannedCount, finished);
            } catch (Exception e) {
                System.out.println("Could not update the live status page: " + e.getMessage());
            }
        }
    }

    /** open.report=true: open the live page at the start and the result file at the end (false: only save them). */
    private static boolean showReports() {
        return Boolean.parseBoolean(Config.get("open.report"));
    }

    private static String attribute(ITestResult result, String name, String fallback) {
        Object value = result.getAttribute(name);
        return value == null ? fallback : value.toString();
    }
}
