package com.ams.report;

import com.ams.base.Os;

import java.awt.Desktop;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Opens the live status page in the web browser and the result file in the spreadsheet program,
 * on Windows, macOS and Linux.
 * <ul>
 *   <li>Windows / macOS: the program the file type is associated with (Excel, WPS, Numbers, ...).</li>
 *   <li>Linux with WPS: the helper script wps_open.py (src/test/resources), which also replaces a WPS
 *       window it opened earlier that still shows an older copy of the file. Otherwise xdg-open.</li>
 * </ul>
 * The result file is opened by a small watcher process that is started when the execution starts and
 * waits until the test run has ended, however it ended (finished, stopped, or killed because VS Code /
 * its terminal was closed). The watcher only needs the JDK, so it keeps working on every system.
 */
public final class ReportOpener {

    private static final Path WPS_SCRIPT = Path.of("src", "test", "resources", "wps_open.py").toAbsolutePath();
    private static final Path WPS_LAUNCHER = Path.of("/usr/bin/et");

    private ReportOpener() {
    }

    /**
     * Starts the watcher, detached from the test run, that opens the file as soon as this test run's
     * Java process has ended.
     */
    public static void openWhenRunEnds(Path file) {
        String java = Path.of(System.getProperty("java.home"), "bin", Os.isWindows() ? "javaw.exe" : "java").toString();
        String classes;
        try {
            classes = Path.of(ReportOpener.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (Exception e) {
            System.out.println("Could not start the result file opener: " + e.getMessage());
            return;
        }
        List<String> command = List.of(java, "-cp", classes, ReportOpener.class.getName(),
                "--watch", String.valueOf(ProcessHandle.current().pid()), file.toAbsolutePath().toString());
        try {
            startDetached(command);
        } catch (Exception e) {
            System.out.println("Could not start the result file opener for " + file + ": " + e.getMessage());
        }
    }

    /**
     * Starts a process that is not stopped together with the test run.
     * Linux: own session (setsid). macOS: immune to the terminal closing (nohup).
     * Windows: javaw has no console, so closing the terminal does not reach it.
     */
    private static void startDetached(List<String> command) throws Exception {
        List<String> full = new ArrayList<>();
        if (Os.isLinux() && Files.isExecutable(Path.of("/usr/bin/setsid"))) {
            full.addAll(List.of("setsid", "-f"));
        } else if (!Os.isWindows()) {
            full.add("nohup");
        }
        full.addAll(command);
        new ProcessBuilder(full).redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
    }

    /**
     * Opens the live status page in the default WEB BROWSER.
     * (On Linux not with xdg-open: .html files may be associated with a mail program such as
     * Thunderbird, which would put the page into a new e-mail.)
     */
    public static void openInBrowser(Path page) {
        String url = page.toUri().toString();
        try {
            if (Os.isWindows()) {
                Desktop.getDesktop().browse(page.toUri());
            } else if (Os.isMac()) {
                start(List.of("open", url));
            } else {
                String browser = "";
                try {
                    browser = new String(new ProcessBuilder("xdg-settings", "get", "default-web-browser")
                            .start().getInputStream().readAllBytes()).trim();
                } catch (Exception ignored) {
                    // xdg-settings not installed
                }
                if (browser.endsWith(".desktop")) {
                    start(List.of("gtk-launch", browser, url));
                } else {
                    start(List.of("xdg-open", url));
                }
            }
            System.out.println("Live status page opened in the browser: " + page);
        } catch (Exception e) {
            System.out.println("Could not open " + page + " in the browser: " + e.getMessage());
        }
    }

    /** Opens the file in the spreadsheet program. Returns true when it was opened. */
    public static boolean openFile(Path file) {
        try {
            if (Os.isWindows()) {
                Desktop.getDesktop().open(file.toFile());
            } else if (Os.isMac()) {
                start(List.of("open", file.toString()));
            } else if (Files.isExecutable(WPS_LAUNCHER) && Files.exists(WPS_SCRIPT)) {
                Process process = new ProcessBuilder("python3", WPS_SCRIPT.toString(), file.toString())
                        .redirectErrorStream(true).start();
                String output = new String(process.getInputStream().readAllBytes()).trim();
                if (process.waitFor() != 0) {
                    System.out.println("WPS did not open " + file.getFileName() + (output.isEmpty() ? "" : ": " + output));
                    return false;
                }
            } else {
                start(List.of("xdg-open", file.toString()));
            }
            return true;
        } catch (Exception e) {
            System.out.println("Could not open " + file + ": " + e.getMessage());
            return false;
        }
    }

    private static void start(List<String> command) throws Exception {
        new ProcessBuilder(command).redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
    }

    /** If the run ended before it could finish the live page, stop its reloading and say so. */
    private static void markLivePageStopped(Path resultFile) throws Exception {
        Path page = Path.of(resultFile.toString().replaceAll("\\.xlsx$", ".html"));
        if (page.equals(resultFile) || !Files.exists(page)) {
            return;
        }
        String content = Files.readString(page, StandardCharsets.UTF_8);
        String refresh = "<meta http-equiv=\"refresh\" content=\"5\">";
        if (!content.contains(refresh)) {
            return; // the run finished the page itself
        }
        content = content.replace(refresh, "");
        int start = content.indexOf("Running&hellip;");
        int end = content.indexOf("<br>", start);
        if (start >= 0 && end > start) {
            content = content.substring(0, start) + "Execution stopped at "
                    + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    + " (test cases not reached stay Not Run)" + content.substring(end);
        }
        Files.writeString(page, content, StandardCharsets.UTF_8);
    }

    /**
     * The watcher: java com.ams.report.ReportOpener --watch &lt;test run pid&gt; &lt;result file&gt;
     * (or without --watch: java com.ams.report.ReportOpener &lt;result file&gt; opens the file now).
     */
    public static void main(String[] args) throws Exception {
        Path file;
        if (args.length == 3 && args[0].equals("--watch")) {
            ProcessHandle.of(Long.parseLong(args[1])).ifPresent(run -> run.onExit().join());
            file = Path.of(args[2]);
            try {
                markLivePageStopped(file);
            } catch (Exception ignored) {
                // the page is only a convenience
            }
        } else {
            file = Path.of(args[0]);
        }
        boolean opened = openFile(file);
        System.exit(opened ? 0 : 1);
    }
}
