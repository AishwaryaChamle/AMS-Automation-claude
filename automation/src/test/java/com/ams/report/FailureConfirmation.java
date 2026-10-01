package com.ams.report;

import com.ams.base.Config;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

/**
 * When a test case is blocked, the execution pauses and the tester confirms the result in a pop-up:
 * "Keep Blocked", "Mark as Pass" or "Mark as Fail". The reason can be edited; it goes to Remarks when the
 * test case is marked Fail. (Failed test cases are not asked: they are recorded as Fail straight away.)
 * The execution resumes where it paused as soon as the tester has answered.
 * <p>
 * While the pop-up is open no new test case starts; test cases already running in parallel
 * finish their current step first. Not asked in headless runs or without a screen, or when
 * confirm.failures=false (then the status is kept as it is).
 */
public final class FailureConfirmation {

    /** The tester's answer: the status to record (Pass, Fail or Blocked) and the reason. */
    public record Decision(String status, String reason) {
    }

    private static final Object PAUSE = new Object();
    private static boolean paused;

    private FailureConfirmation() {
    }

    public static boolean enabled() {
        return !"false".equalsIgnoreCase(Config.get("confirm.failures")) && !Config.headless()
                && !GraphicsEnvironment.isHeadless();
    }

    /** Called before each test case starts: waits while a result is being confirmed. */
    public static void waitWhilePaused() {
        synchronized (PAUSE) {
            while (paused) {
                try {
                    PAUSE.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    /**
     * Pauses the execution and asks the tester about a "Blocked" result. One question at a
     * time: results of parallel test cases wait for their turn.
     */
    public static Decision ask(String testCaseId, String status, String expected, String actual, String reason, byte[] screenshot) {
        synchronized (FailureConfirmation.class) {
            synchronized (PAUSE) {
                paused = true;
            }
            System.out.println("Execution paused: confirm the result of " + testCaseId + " in the pop-up");
            try {
                Decision[] answer = new Decision[1];
                SwingUtilities.invokeAndWait(() -> answer[0] = showDialog(testCaseId, status, expected, actual, reason, screenshot));
                System.out.println("Execution resumed: " + testCaseId + " = " + answer[0].status()
                        + (answer[0].status().equals(status) ? "" : " (changed by tester from " + status + ")"));
                return answer[0];
            } catch (Exception e) {
                System.out.println("Could not ask about " + testCaseId + ", it stays " + status + ": " + e.getMessage());
                return new Decision(status, reason);
            } finally {
                synchronized (PAUSE) {
                    paused = false;
                    PAUSE.notifyAll();
                }
            }
        }
    }

    private static Decision showDialog(String testCaseId, String status, String expected, String actual, String reason, byte[] screenshot) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(section("Expected Result", expected, false));
        panel.add(section("Actual Result", actual, false));
        JTextArea reasonField = new JTextArea(reason, 3, 70);
        panel.add(section("Reason it is blocked (can be edited; goes to Remarks if you mark it Fail)", reasonField));

        BufferedImage image = image(screenshot);
        if (image != null) {
            int width = Math.min(900, image.getWidth());
            Image scaled = image.getScaledInstance(width, image.getHeight() * width / image.getWidth(), Image.SCALE_SMOOTH);
            JScrollPane picture = new JScrollPane(new JLabel(new ImageIcon(scaled)));
            picture.setPreferredSize(new Dimension(width + 20, Math.min(450, scaled.getHeight(null) + 20)));
            picture.setBorder(BorderFactory.createTitledBorder("Screen of the test case"));
            panel.add(Box.createVerticalStrut(8));
            panel.add(picture);
        }

        String[] options = {"Keep Blocked", "Mark as Pass", "Mark as Fail"};
        JOptionPane pane = new JOptionPane(panel, JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options, options[0]);
        JDialog dialog = pane.createDialog("Test case blocked: " + testCaseId + " - execution paused");
        dialog.setAlwaysOnTop(true);
        dialog.setModal(true);
        dialog.setVisible(true);
        dialog.dispose();

        // Closing the pop-up keeps the status
        Object choice = pane.getValue();
        String chosen = "Mark as Pass".equals(choice) ? "Pass" : "Mark as Fail".equals(choice) ? "Fail" : status;
        String text = reasonField.getText().trim();
        return new Decision(chosen, text.isEmpty() ? reason : text);
    }

    private static JPanel section(String title, String text, boolean editable) {
        JTextArea area = new JTextArea(text, Math.min(4, Math.max(1, text.length() / 90 + 1)), 70);
        area.setEditable(editable);
        return section(title, area);
    }

    private static JPanel section(String title, JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JPanel box = new JPanel(new BorderLayout());
        box.setBorder(BorderFactory.createTitledBorder(title));
        box.add(new JScrollPane(area), BorderLayout.CENTER);
        return box;
    }

    private static BufferedImage image(byte[] png) {
        if (png == null) {
            return null;
        }
        try {
            return ImageIO.read(new ByteArrayInputStream(png));
        } catch (Exception e) {
            return null;
        }
    }
}
