package com.ams.report;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Saves the latest result of every test case in results/execution-results.tsv,
 * so a re-run of a few tests updates Executed_Suits.xls without losing the others.
 */
public final class ResultStore {

    private static final Path FILE = Path.of("results", "execution-results.tsv");

    private ResultStore() {
    }

    public static Map<String, ExecutionResult> load() throws Exception {
        Map<String, ExecutionResult> results = new LinkedHashMap<>();
        if (Files.exists(FILE)) {
            for (String line : Files.readAllLines(FILE)) {
                String[] p = line.split("\t", -1);
                if (p.length == 5) {
                    results.put(p[0], new ExecutionResult(p[0], p[1], unescape(p[2]), unescape(p[3]), p[4]));
                }
            }
        }
        return results;
    }

    public static void save(Map<String, ExecutionResult> results) throws Exception {
        List<String> lines = new ArrayList<>();
        for (ExecutionResult r : results.values()) {
            lines.add(String.join("\t", r.testCaseId(), r.status(), escape(r.actualResult()), escape(r.remark()), r.executionDate()));
        }
        Files.createDirectories(FILE.getParent());
        Files.write(FILE, lines);
    }

    private static String escape(String text) {
        return text == null ? "" : text.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n");
    }

    private static String unescape(String text) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\' && i + 1 < text.length()) {
                char next = text.charAt(++i);
                out.append(next == 'n' ? '\n' : next == 't' ? '\t' : next);
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
