package com.ams.report;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * A web page that shows testcases_result.xlsx while the execution runs: all test cases in
 * file order with all 23 columns of the template (Test Case ID through Remarks). It reloads itself every 5 seconds and scrolls to the test case
 * executed most recently. (Spreadsheet programs such as WPS do not reload an open .xlsx.)
 */
public final class LiveStatusPage {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private LiveStatusPage() {
    }

    /**
     * Writes the page.
     *
     * @param page         the .html file next to the result file
     * @param resultFile   the .xlsx result file of this execution
     * @param table        the result sheet as text: header row first, then one row per test case
     * @param lastExecuted Test Case ID executed most recently (the page scrolls to it), or null
     * @param planned      number of test cases selected for this run (0 = not known yet)
     * @param finished     true when the whole execution is over (the page then stops reloading)
     */
    public static void write(Path page, Path resultFile, List<List<String>> table, String lastExecuted,
                             int planned, boolean finished) throws Exception {
        List<String> header = table.isEmpty() ? List.of() : table.get(0);
        int idColumn = header.indexOf("Test Case ID");
        int statusColumn = header.indexOf("Status");

        long pass = 0;
        long fail = 0;
        long blocked = 0;
        long invalid = 0;
        StringBuilder headerCells = new StringBuilder();
        for (String name : header) {
            headerCells.append("<th>").append(html(name)).append("</th>");
        }
        StringBuilder rows = new StringBuilder();
        for (List<String> row : table.subList(Math.min(1, table.size()), table.size())) {
            String id = idColumn >= 0 ? row.get(idColumn) : "";
            String status = statusColumn >= 0 ? row.get(statusColumn) : "";
            pass += status.equals("Pass") ? 1 : 0;
            fail += status.equals("Fail") ? 1 : 0;
            blocked += status.equals("Blocked") ? 1 : 0;
            invalid += status.equals("Invalid") ? 1 : 0;
            rows.append("<tr id='").append(html(id)).append("'").append(id.equals(lastExecuted) ? " class='latest'" : "").append(">");
            for (int c = 0; c < row.size(); c++) {
                String value = html(row.get(c));
                if (c == statusColumn) {
                    String css = status.equals("Pass") ? "pass" : status.equals("Fail") ? "fail" : status.equals("Blocked") ? "blocked"
                            : status.equals("Invalid") ? "invalid" : "pending";
                    rows.append("<td><span class='badge ").append(css).append("'>").append(value).append("</span></td>");
                } else {
                    rows.append("<td>").append(value).append("</td>");
                }
            }
            rows.append("</tr>\n");
        }
        long executed = pass + fail + blocked + invalid;
        String progress = planned > 0 ? executed + " of " + planned : String.valueOf(executed);
        String state = finished
                ? "Execution completed at " + LocalDateTime.now().format(TIME)
                : "Running&hellip; this page refreshes every 5 seconds (last update " + LocalDateTime.now().format(TIME) + ")";

        String content = """
                <!DOCTYPE html>
                <html lang="en"><head><meta charset="utf-8">
                %s
                <title>Test Execution Status</title>
                <style>
                  body { font-family: Arial, sans-serif; margin: 16px; background: #fafafa; color: #222; }
                  h1 { font-size: 20px; margin: 0 0 4px; color: #790011; }
                  .state { color: #555; margin-bottom: 12px; }
                  .tiles { display: flex; gap: 12px; flex-wrap: wrap; margin-bottom: 12px; }
                  .tile { background: #fff; border: 1px solid #ddd; border-radius: 6px; padding: 8px 14px; min-width: 100px; }
                  .tile b { display: block; font-size: 20px; }
                  .scroll { overflow: auto; max-height: calc(100vh - 150px); border: 1px solid #ccc; }
                  table { border-collapse: collapse; min-width: 100%%; background: #fff; }
                  th, td { border: 1px solid #ccc; padding: 5px 7px; text-align: left; vertical-align: top; font-size: 12px; }
                  th { background: #1F4E78; color: #fff; position: sticky; top: 0; z-index: 1; min-width: 70px; }
                  td { min-width: 70px; }
                  td:nth-child(4), td:nth-child(6), td:nth-child(7), td:nth-child(8), td:nth-child(23) { min-width: 220px; }
                  td:nth-child(1), td:nth-child(9), td:nth-child(15), td:nth-child(22) { white-space: nowrap; }
                  tr.latest td { background: #fff7d6; }
                  .badge { padding: 2px 8px; border-radius: 10px; font-weight: bold; white-space: nowrap; }
                  .pass { background: #C6EFCE; color: #006100; }
                  .fail { background: #FFC7CE; color: #9C0006; }
                  .blocked { background: #FFEB9C; color: #7F6000; }
                  .invalid { background: #E4DFEC; color: #5B3F86; }
                  .pending { background: #eee; color: #666; }
                </style></head><body>
                <h1>Test Execution Status</h1>
                <div class="state">%s<br>Result file: %s</div>
                <div class="tiles">
                  <div class="tile">Executed<b>%s</b></div>
                  <div class="tile">Pass<b>%d</b></div>
                  <div class="tile">Fail<b>%d</b></div>
                  <div class="tile">Blocked<b>%d</b></div>
                  <div class="tile">Invalid<b>%d</b></div>
                </div>
                <div class="scroll"><table><thead><tr>%s</tr></thead>
                <tbody>
                %s</tbody></table></div>
                <script>
                  // Scroll to the test case executed most recently once the page has loaded
                  window.addEventListener('load', function () {
                    var latest = document.querySelector('tr.latest');
                    if (latest) { latest.scrollIntoView({block: 'center'}); }
                  });
                </script>
                </body></html>
                """.formatted(finished ? "" : "<meta http-equiv=\"refresh\" content=\"5\">", state, html(resultFile.toString()),
                progress, pass, fail, blocked, invalid, headerCells, rows);

        // Write to a temporary file and swap it in, so the browser never shows a half-written page
        Path folder = page.toAbsolutePath().getParent();
        Files.createDirectories(folder);
        Path temp = Files.createTempFile(folder, ".writing-", ".html");
        Files.writeString(temp, content);
        try {
            // Temporary files are private (rw-------); give the page normal read permissions
            Files.setPosixFilePermissions(temp, java.nio.file.attribute.PosixFilePermissions.fromString("rw-rw-r--"));
        } catch (UnsupportedOperationException ignored) {
            // not a POSIX file system (e.g. Windows): nothing to do
        }
        Files.move(temp, page, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    private static String html(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&#39;").replace("\n", "<br>");
    }
}
