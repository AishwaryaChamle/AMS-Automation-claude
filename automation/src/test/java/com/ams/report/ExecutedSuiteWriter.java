package com.ams.report;

import com.ams.base.Config;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Creates the execution result file (testcases_result.xlsx) in the QA_Testcase_Template_V1 format.
 * The first test case workbook is used as the base (QA Dashboard, Test Cases, Lists), and the
 * test cases of the second workbook are added below its own on the "Test Cases" sheet.
 * The original workbooks are only read. Only the execution columns are filled in:
 * Actual Result, Status, Defect ID and Remarks (failed test cases only: the reason of the failure),
 * Executed By and Execution Date.
 * The template has no "Invalid" status, so it is added to the result file (see addInvalidStatus).
 */
public final class ExecutedSuiteWriter {

    private static final String SHEET_NAME = TestCaseBook.SHEET_NAME;
    private static final String DASHBOARD = "QA Dashboard";

    /** Last row the template formulas (Risk Score, Risk Level, Priority) are prepared for. */
    private static final int LAST_TEMPLATE_ROW = 500;

    private ExecutedSuiteWriter() {
    }

    /** Writes the results of this execution to its result file, Execution_Reports/testcases_result.xlsx. */
    public static void write(Map<String, ExecutionResult> results, Path executionReport) throws Exception {
        write(results, executionReport, null);
    }

    /**
     * Same as above; the file opens on the row of {@code focusTestCaseId}
     * (the test case just executed), so its new status is visible straight away.
     */
    public static void write(Map<String, ExecutionResult> results, Path executionReport, String focusTestCaseId) throws Exception {
        String[] keys = TestCaseBook.WORKBOOK_KEYS;
        try (InputStream in = Files.newInputStream(Path.of(Config.get(keys[0]))); XSSFWorkbook target = new XSSFWorkbook(in)) {
            XSSFSheet sheet = target.getSheet(SHEET_NAME);
            Map<String, Integer> column = TestCaseBook.columns(sheet.getRow(0));

            // Add the test cases of the other workbook(s) below the first workbook's test cases
            int nextRow = lastTestCaseRow(sheet, column.get("Test Case ID")) + 1;
            for (int k = 1; k < keys.length; k++) {
                nextRow = appendTestCases(Path.of(Config.get(keys[k])), sheet, nextRow);
            }

            fillResults(sheet, column, results);
            keepOnlyExecuted(sheet, column.get("Test Case ID"), results);
            fillDashboard(target);
            addInvalidStatus(target, column.get("Status"));

            // Calculate the template formulas (Priority, dashboard counts) so the file shows them straight away
            FormulaEvaluator evaluator = target.getCreationHelper().createFormulaEvaluator();
            try {
                evaluator.evaluateAll();
            } catch (RuntimeException e) {
                System.out.println("Formulas will be calculated when the file is opened: " + e.getMessage());
            }
            target.setForceFormulaRecalculation(true);

            lastTable = snapshot(sheet, column, evaluator);
            if (focusTestCaseId != null) {
                focusOn(target, focusTestCaseId);
            }
            saveSafely(target, executionReport);
        }
    }

    /** Expected Result text of a test case ("" when unknown). */
    public static String expectedResultOf(String testCaseId) {
        return TestCaseBook.expectedResultOf(testCaseId);
    }

    /** Columns shown on the live status page: all 23 columns of the template, in template order. */
    private static final String[] LIVE_COLUMNS = {"Test Case ID", "Module / Feature", "Requirement ID / User Story",
            "Test Case Description", "Test Type", "Test Steps", "Expected Result", "Actual Result", "Status",
            "Business Criticality (Business importance)", "Impact (Business/customer impact)",
            "Likelihood (Probability of failure)", "Risk Score (Impact * Likelihood)",
            "Risk Level (Risk if functionality fails)", "Priority (Execution priority)", "Scope Category",
            "Automation Candidate", "Regression Candidate", "Environment", "Defect ID", "Executed By",
            "Execution Date", "Remarks"};

    /** The test case rows as text (header row first), as last written; used by the live status page. */
    private static volatile List<List<String>> lastTable = List.of();

    public static List<List<String>> lastTable() {
        return lastTable;
    }

    private static List<List<String>> snapshot(Sheet sheet, Map<String, Integer> column, FormulaEvaluator evaluator) {
        DataFormatter formatter = new DataFormatter();
        List<List<String>> table = new ArrayList<>();
        table.add(List.of(LIVE_COLUMNS));
        int idColumn = column.get("Test Case ID");
        for (Row row : sheet) {
            if (row.getRowNum() == 0 || formatter.formatCellValue(row.getCell(idColumn)).isBlank()) {
                continue;
            }
            List<String> values = new ArrayList<>();
            for (String name : LIVE_COLUMNS) {
                Integer c = column.get(name);
                values.add(c == null ? "" : formatter.formatCellValue(row.getCell(c), evaluator));
            }
            table.add(values);
        }
        return table;
    }

    /** Where a test case's Status cell is: sheet index (0 = first sheet), row and column (0-based). */
    public record CellLocation(int sheetIndex, int row, int column) {
    }

    /** Status cell of the test case written most recently (used to move WPS there). */
    private static volatile CellLocation lastFocus;

    public static CellLocation lastFocus() {
        return lastFocus;
    }

    /** Makes the workbook open on the Test Cases sheet at the row of the given test case (Status cell selected). */
    private static void focusOn(XSSFWorkbook workbook, String testCaseId) {
        int s = workbook.getSheetIndex(SHEET_NAME);
        XSSFSheet sheet = workbook.getSheetAt(s);
        Map<String, Integer> column = TestCaseBook.columns(sheet.getRow(0));
        int statusColumn = column.get("Status");
        for (Row row : sheet) {
            Cell id = row.getCell(column.get("Test Case ID"));
            if (row.getRowNum() > 0 && id != null && testCaseId.equals(id.toString().trim())) {
                workbook.setActiveSheet(s);
                workbook.setSelectedTab(s);
                sheet.setActiveCell(new org.apache.poi.ss.util.CellAddress(row.getRowNum(), statusColumn));
                // Scroll so the row is visible (a few rows above it for context), starting at column A
                sheet.showInPane(Math.max(1, row.getRowNum() - 5), 0);
                lastFocus = new CellLocation(s, row.getRowNum(), statusColumn);
                return;
            }
        }
    }

    /** The result file of every execution: Execution_Reports/testcases_result.xlsx (created fresh for each run). */
    public static Path resultFile() {
        return reportsFolder().resolve(Config.get("result.file.name"));
    }

    /** Folder that holds the result file and the live status page. */
    public static Path reportsFolder() {
        return Path.of(Config.get("execution.reports.folder")).toAbsolutePath().normalize();
    }

    /**
     * Writes to a temporary file first and then replaces the report in one step,
     * so a spreadsheet program never reads a half-written file.
     */
    private static void saveSafely(XSSFWorkbook workbook, Path output) throws Exception {
        Path folder = output.toAbsolutePath().getParent();
        Files.createDirectories(folder);
        Path temp = Files.createTempFile(folder, ".writing-", ".xlsx");
        try (OutputStream out = Files.newOutputStream(temp)) {
            workbook.write(out);
        }
        try {
            // Temporary files are private (rw-------); give the report normal read permissions
            Files.setPosixFilePermissions(temp, java.nio.file.attribute.PosixFilePermissions.fromString("rw-rw-r--"));
        } catch (UnsupportedOperationException ignored) {
            // not a POSIX file system (e.g. Windows): nothing to do
        }
        try {
            try {
                Files.move(temp, output, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(temp, output, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (java.nio.file.FileSystemException e) {
            // Windows: the file cannot be replaced while it is open in Excel / WPS; the next save tries again
            Files.deleteIfExists(temp);
            System.out.println("Could not update " + output.getFileName() + " (close it in the spreadsheet program): " + e.getMessage());
        }
    }

    /** Row number of the last test case on the sheet (0 when there is none). */
    private static int lastTestCaseRow(Sheet sheet, int idColumn) {
        int last = 0;
        for (Row row : sheet) {
            Cell id = row.getCell(idColumn);
            if (row.getRowNum() > 0 && id != null && !id.toString().isBlank()) {
                last = row.getRowNum();
            }
        }
        return last;
    }

    /**
     * Copies the test cases of another template workbook into the rows below {@code nextRow}.
     * Only values are copied: the target rows already have the template's formatting,
     * dropdowns and formulas (Risk Score, Risk Level, Priority). Returns the next free row.
     */
    private static int appendTestCases(Path source, Sheet target, int nextRow) throws Exception {
        try (InputStream in = Files.newInputStream(source); XSSFWorkbook workbook = new XSSFWorkbook(in)) {
            Sheet from = workbook.getSheet(SHEET_NAME);
            int idColumn = TestCaseBook.columns(from.getRow(0)).get("Test Case ID");
            for (Row row : from) {
                Cell id = row.getCell(idColumn);
                if (row.getRowNum() == 0 || id == null || id.toString().isBlank()) {
                    continue;
                }
                if (nextRow > LAST_TEMPLATE_ROW) {
                    throw new IllegalStateException("The template has room for " + LAST_TEMPLATE_ROW + " test cases only");
                }
                Row copy = target.getRow(nextRow) == null ? target.createRow(nextRow) : target.getRow(nextRow);
                copy.setHeight(row.getHeight());
                for (Cell cell : row) {
                    if (cell.getCellType() == CellType.FORMULA) {
                        continue; // the template row already has this formula
                    }
                    Cell newCell = writableCell(copy, cell.getColumnIndex());
                    switch (cell.getCellType()) {
                        case STRING -> newCell.setCellValue(cell.getStringCellValue());
                        case NUMERIC -> newCell.setCellValue(cell.getNumericCellValue());
                        case BOOLEAN -> newCell.setCellValue(cell.getBooleanCellValue());
                        default -> newCell.setBlank();
                    }
                }
                nextRow++;
            }
        }
        return nextRow;
    }

    /**
     * Writes the execution result of each test case into its row.
     * A failed test case also gets a Defect ID: DEF-001, DEF-002, ... in the order of the rows.
     */
    private static void fillResults(Sheet sheet, Map<String, Integer> column, Map<String, ExecutionResult> results) {
        String tester = Config.get("tester");
        int defects = 0;
        for (Row row : sheet) {
            Cell idCell = row.getCell(column.get("Test Case ID"));
            String id = idCell == null ? "" : idCell.toString().trim();
            if (row.getRowNum() == 0 || id.isEmpty()) {
                continue;
            }
            // Remarks: only the reason of a failed test case; otherwise empty
            setText(row, column.get("Remarks"), "");
            ExecutionResult result = results.get(id);
            if (result == null) {
                continue; // not executed: status stays "Not Run"
            }
            setText(row, column.get("Actual Result"), result.actualResult());
            setText(row, column.get("Status"), result.status());
            boolean failed = "Fail".equals(result.status());
            setText(row, column.get("Defect ID"), failed ? String.format("DEF-%03d", ++defects) : "");
            setText(row, column.get("Remarks"), failed ? result.remark() : "");
            setText(row, column.get("Executed By"), tester);
            Cell date = writableCell(row, column.get("Execution Date"));
            date.setCellValue(LocalDate.parse(result.executionDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    }

    /**
     * Removes the test cases that were not executed, so the result file lists only executed ones.
     * The kept rows are moved up (values only); every template row already has the same formulas
     * (Risk Score, Risk Level, Priority), so those keep working on the new row.
     */
    private static void keepOnlyExecuted(Sheet sheet, int idColumn, Map<String, ExecutionResult> results) {
        // Remember the values of the executed test case rows, in their current order
        List<Map<Integer, Object>> kept = new ArrayList<>();
        List<Short> heights = new ArrayList<>();
        for (int r = 1; r <= LAST_TEMPLATE_ROW; r++) {
            Row row = sheet.getRow(r);
            Cell id = row == null ? null : row.getCell(idColumn);
            if (id == null || !results.containsKey(id.toString().trim())) {
                continue;
            }
            Map<Integer, Object> values = new java.util.HashMap<>();
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING -> values.put(cell.getColumnIndex(), cell.getStringCellValue());
                    case NUMERIC -> values.put(cell.getColumnIndex(), cell.getNumericCellValue());
                    case BOOLEAN -> values.put(cell.getColumnIndex(), cell.getBooleanCellValue());
                    default -> { } // formulas stay in the template row; blanks need nothing
                }
            }
            kept.add(values);
            heights.add(row.getHeight());
        }

        // Clear every test case row (formulas are left in place), then write the kept rows from row 2
        for (int r = 1; r <= LAST_TEMPLATE_ROW; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            for (Cell cell : toArray(row)) { // a copy: writableCell may replace cells
                if (cell.getCellType() != CellType.FORMULA && cell.getCellType() != CellType.BLANK) {
                    writableCell(row, cell.getColumnIndex()).setBlank();
                }
            }
        }
        for (int i = 0; i < kept.size(); i++) {
            Row row = sheet.getRow(i + 1) == null ? sheet.createRow(i + 1) : sheet.getRow(i + 1);
            row.setHeight(heights.get(i));
            for (Map.Entry<Integer, Object> value : kept.get(i).entrySet()) {
                Cell cell = writableCell(row, value.getKey());
                if (value.getValue() instanceof String text) {
                    cell.setCellValue(text);
                } else if (value.getValue() instanceof Double number) {
                    cell.setCellValue(number);
                } else {
                    cell.setCellValue((Boolean) value.getValue());
                }
            }
        }
    }

    private static Cell[] toArray(Row row) {
        List<Cell> cells = new ArrayList<>();
        row.forEach(cells::add);
        return cells.toArray(new Cell[0]);
    }

    /** Fills the dashboard input cells: Project / Release and the tester of this execution. */
    private static void fillDashboard(XSSFWorkbook workbook) {
        Sheet dashboard = workbook.getSheet(DASHBOARD);
        if (dashboard == null) {
            return;
        }
        String project = Config.get("report.project");
        if (!project.isBlank()) {
            setText(dashboard.getRow(1), 2, project); // C2
        }
        String tester = Config.get("tester");
        if (!tester.isBlank()) {
            // "Execution by Tester" table (G14:G22): only this execution's tester
            setText(dashboard.getRow(13), 6, tester); // G14
            for (int r = 14; r <= 21; r++) {
                if (dashboard.getRow(r).getCell(6) != null) {
                    writableCell(dashboard.getRow(r), 6).setBlank();
                }
            }
        }
    }

    /** Status of a test case marked 'Not in scope' by ASPL; the template does not have it. */
    private static final String INVALID = "Invalid";

    /**
     * Adds the "Invalid" status to the result file, so it can be chosen and is counted like the others:
     * 1. Lists sheet: "Invalid" below the other statuses, and the Status dropdown includes it,
     * 2. Test Cases sheet: an "Invalid" Status cell gets its own colour,
     * 3. QA Dashboard: an "Invalid" row in the Test Status table (Total moves one row down)
     *    and in the Test Execution Status chart.
     */
    private static void addInvalidStatus(XSSFWorkbook workbook, int statusColumn) {
        Sheet lists = workbook.getSheet("Lists");
        XSSFSheet testCases = workbook.getSheet(SHEET_NAME);
        if (lists == null || testCases == null) {
            return;
        }
        // 1. Status list and dropdown
        int listColumn = TestCaseBook.columns(lists.getRow(0)).getOrDefault("Status", -1);
        if (listColumn < 0) {
            return;
        }
        int lastListRow = 0;
        DataFormatter formatter = new DataFormatter();
        for (Row row : lists) {
            String value = formatter.formatCellValue(row.getCell(listColumn));
            if (row.getRowNum() > 0 && !value.isBlank()) {
                lastListRow = row.getRowNum();
                if (value.equals(INVALID)) {
                    return; // the workbook has it already
                }
            }
        }
        String columnLetter = org.apache.poi.ss.util.CellReference.convertNumToColString(listColumn);
        String oldList = "Lists!$" + columnLetter + "$2:$" + columnLetter + "$" + (lastListRow + 1);
        String newList = "Lists!$" + columnLetter + "$2:$" + columnLetter + "$" + (lastListRow + 2);
        Row listRow = lists.getRow(lastListRow + 1) == null ? lists.createRow(lastListRow + 1) : lists.getRow(lastListRow + 1);
        writableCell(listRow, listColumn).setCellValue(INVALID);
        writableCell(listRow, listColumn).setCellStyle(lists.getRow(lastListRow).getCell(listColumn).getCellStyle());
        if (testCases.getCTWorksheet().isSetDataValidations()) {
            for (var validation : testCases.getCTWorksheet().getDataValidations().getDataValidationArray()) {
                if (oldList.equals(validation.getFormula1())) {
                    validation.setFormula1(newList);
                }
            }
        }

        // 2. Colour of an "Invalid" Status cell (light purple, like the live status page)
        String statusLetter = org.apache.poi.ss.util.CellReference.convertNumToColString(statusColumn);
        var formatting = testCases.getSheetConditionalFormatting();
        var rule = formatting.createConditionalFormattingRule("$" + statusLetter + "2=\"" + INVALID + "\"");
        rule.createPatternFormatting().setFillBackgroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(new byte[]{(byte) 0xE4, (byte) 0xDF, (byte) 0xEC}, null));
        rule.createFontFormatting().setFontColor(new org.apache.poi.xssf.usermodel.XSSFColor(new byte[]{(byte) 0x5B, (byte) 0x3F, (byte) 0x86}, null));
        formatting.addConditionalFormatting(new org.apache.poi.ss.util.CellRangeAddress[]{
                org.apache.poi.ss.util.CellRangeAddress.valueOf(statusLetter + "2:" + statusLetter + (LAST_TEMPLATE_ROW + 1))}, rule);

        // 3. Dashboard: Test Status table (Status in column D) and its chart
        XSSFSheet dashboard = workbook.getSheet(DASHBOARD);
        if (dashboard == null) {
            return;
        }
        int lastStatus = -1; // row of the last status ("Not Applicable"); the Total row is below it
        for (int r = 12; r <= 25; r++) {
            if (dashboard.getRow(r) != null && "Not Applicable".equals(formatter.formatCellValue(dashboard.getRow(r).getCell(3)))) {
                lastStatus = r;
            }
        }
        if (lastStatus < 0) {
            return;
        }
        int invalidRow = lastStatus + 1; // the Total row becomes the "Invalid" row
        int totalRow = lastStatus + 2;
        Row last = dashboard.getRow(lastStatus);
        Row invalid = dashboard.getRow(invalidRow);
        Row total = dashboard.getRow(totalRow) == null ? dashboard.createRow(totalRow) : dashboard.getRow(totalRow);
        // Total label (merged over columns A-D): move it one row down
        for (int m = dashboard.getNumMergedRegions() - 1; m >= 0; m--) {
            var region = dashboard.getMergedRegion(m);
            if (region.getFirstRow() == invalidRow && region.getFirstColumn() == 0) {
                dashboard.removeMergedRegion(m);
            }
        }
        for (int c = 0; c <= 5; c++) {
            Cell from = invalid.getCell(c);
            if (from != null) {
                writableCell(total, c).setCellStyle(from.getCellStyle());
            }
        }
        writableCell(total, 0).setCellValue(formatter.formatCellValue(invalid.getCell(0)));
        dashboard.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(totalRow, totalRow, 0, 3));
        int firstStatus = lastStatus;
        while (firstStatus > 0 && dashboard.getRow(firstStatus - 1) != null
                && dashboard.getRow(firstStatus - 1).getCell(4) != null
                && dashboard.getRow(firstStatus - 1).getCell(4).getCellType() == CellType.FORMULA) {
            firstStatus--;
        }
        writableCell(total, 4).setCellFormula("SUM(E" + (firstStatus + 1) + ":E" + (invalidRow + 1) + ")");
        writableCell(total, 5).setCellFormula("SUM(F" + (firstStatus + 1) + ":F" + (invalidRow + 1) + ")");
        // "Invalid" row: same look and formulas as the row above it
        for (int c = 0; c <= 2; c++) {
            Cell cell = writableCell(invalid, c);
            cell.setBlank();
            cell.setCellStyle(workbook.getCellStyleAt(0));
        }
        for (int c = 3; c <= 5; c++) {
            writableCell(invalid, c).setCellStyle(last.getCell(c).getCellStyle());
        }
        writableCell(invalid, 3).setCellValue(INVALID);
        writableCell(invalid, 4).setCellFormula(last.getCell(4).getCellFormula().replace("D" + (lastStatus + 1), "D" + (invalidRow + 1)));
        writableCell(invalid, 5).setCellFormula(last.getCell(5).getCellFormula().replace("E" + (lastStatus + 1), "E" + (invalidRow + 1)));
        // Chart: the series that ends at the last status row now ends at the "Invalid" row
        String oldEnd = "$" + (lastStatus + 1);
        String newEnd = "$" + (invalidRow + 1);
        for (var chart : dashboard.createDrawingPatriarch().getCharts()) {
            for (var doughnut : chart.getCTChart().getPlotArea().getDoughnutChartList()) {
                for (var series : doughnut.getSerList()) {
                    if (series.isSetVal() && series.getVal().isSetNumRef() && series.getVal().getNumRef().getF().endsWith(oldEnd)) {
                        String f = series.getVal().getNumRef().getF();
                        series.getVal().getNumRef().setF(f.substring(0, f.length() - oldEnd.length()) + newEnd);
                    }
                    if (series.isSetCat() && series.getCat().isSetStrRef() && series.getCat().getStrRef().getF().endsWith(oldEnd)) {
                        String f = series.getCat().getStrRef().getF();
                        series.getCat().getStrRef().setF(f.substring(0, f.length() - oldEnd.length()) + newEnd);
                    }
                }
            }
        }
    }

    private static void setText(Row row, int columnIndex, String text) {
        Cell cell = writableCell(row, columnIndex);
        if (text == null || text.isEmpty()) {
            cell.setBlank();
        } else {
            cell.setCellValue(text);
        }
    }

    /**
     * The cell at the given column, ready for a new value, keeping its template formatting.
     * Text stored "inline" in the workbook cannot be overwritten by POI, so such a cell is recreated.
     */
    private static Cell writableCell(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return row.createCell(columnIndex);
        }
        boolean inline = cell instanceof org.apache.poi.xssf.usermodel.XSSFCell xssf
                && xssf.getCTCell().getT() == org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType.INLINE_STR;
        if (!inline) {
            return cell;
        }
        org.apache.poi.ss.usermodel.CellStyle style = cell.getCellStyle();
        row.removeCell(cell);
        Cell fresh = row.createCell(columnIndex);
        fresh.setCellStyle(style);
        return fresh;
    }
}
