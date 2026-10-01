package com.ams.report;

import com.ams.base.Config;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reads the two test case workbooks (QA_Testcase_Template_V1 format, sheet "Test Cases"):
 * Test Case ID, Expected Result and Priority of every test case.
 * Priority (P1..P4) is a formula in the template (Impact x Likelihood), so it is calculated here.
 */
public final class TestCaseBook {

    /** Name of the sheet that lists the test cases in the template. */
    public static final String SHEET_NAME = "Test Cases";

    /** Workbook keys in config.properties, in the order their test cases are listed. */
    public static final String[] WORKBOOK_KEYS = {"input.workbook.uat", "input.workbook.scenario"};

    public record TestCase(String id, String expectedResult, String priority) {
    }

    private static Map<String, TestCase> testCases;

    private TestCaseBook() {
    }

    /** Every test case of both workbooks, in workbook order. */
    public static synchronized Map<String, TestCase> all() {
        if (testCases == null) {
            testCases = new LinkedHashMap<>();
            for (String key : WORKBOOK_KEYS) {
                read(Path.of(Config.get(key)));
            }
        }
        return testCases;
    }

    public static String expectedResultOf(String testCaseId) {
        TestCase testCase = all().get(testCaseId);
        return testCase == null ? "" : testCase.expectedResult();
    }

    public static String priorityOf(String testCaseId) {
        TestCase testCase = all().get(testCaseId);
        return testCase == null ? "" : testCase.priority();
    }

    /** Priorities to execute, from config.properties / -Dpriorities (e.g. "P1,P2,P3"); empty or "all" = every priority. */
    public static Set<String> selectedPriorities() {
        String value = Config.get("priorities");
        if (value.isBlank() || value.equalsIgnoreCase("all")) {
            return Set.of();
        }
        return Arrays.stream(value.split("[,\\s]+")).filter(p -> !p.isBlank())
                .map(String::toUpperCase).collect(Collectors.toSet());
    }

    /** True when the test case's priority is one of the priorities to execute. */
    public static boolean hasSelectedPriority(String testCaseId) {
        Set<String> priorities = selectedPriorities();
        return priorities.isEmpty() || priorities.contains(priorityOf(testCaseId).toUpperCase());
    }

    /**
     * Column index of each header, by name. A template header such as
     * "Priority (Execution priority)" is also stored under its short name ("Priority").
     */
    public static Map<String, Integer> columns(Row header) {
        Map<String, Integer> columns = new HashMap<>();
        for (Cell cell : header) {
            String name = cell.toString().trim();
            columns.put(name, cell.getColumnIndex());
            int bracket = name.indexOf(" (");
            if (bracket > 0) {
                columns.putIfAbsent(name.substring(0, bracket).trim(), cell.getColumnIndex());
            }
        }
        return columns;
    }

    private static void read(Path file) {
        try (InputStream in = Files.newInputStream(file); XSSFWorkbook workbook = new XSSFWorkbook(in)) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            DataFormatter formatter = new DataFormatter();
            Sheet sheet = workbook.getSheet(SHEET_NAME);
            Map<String, Integer> column = columns(sheet.getRow(0));
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                String id = formatter.formatCellValue(row.getCell(column.get("Test Case ID"))).trim();
                if (id.isEmpty()) {
                    continue;
                }
                String expected = formatter.formatCellValue(row.getCell(column.get("Expected Result"))).trim();
                // Priority is calculated by the template formula from Impact and Likelihood
                String priority = formatter.formatCellValue(row.getCell(column.get("Priority")), evaluator).trim();
                testCases.putIfAbsent(id, new TestCase(id, expected, priority));
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not read the test cases from " + file, e);
        }
    }
}
