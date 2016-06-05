package pl.edu.pw.elka.rso.manage.screen;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract class that creates Thread for printing Node status.
 * DirNode and FileNode need to extends this class on their own.
 */
public abstract class NodeScreen implements Runnable {
    private static final int SCREEN_REFRESH_INTERVAL = 1000; // ms
    private static final int MAX_LOG_ENTRIES = 10;
    protected static final String DASHED_LINE_THIN = "12345678".replaceAll("\\d", "----------");
    protected static final String DASHED_LINE_THICK = "12345678".replaceAll("\\d", "==========");

    private final boolean isWindowsOS;

    protected static NodeScreen instance;
    private List<String> logEntries = new LinkedList<>();
    private int currentLogEntryNumber = 0;

    protected NodeScreen() {
        this.isWindowsOS = System.getProperty("os.name").contains("Windows");
    }

    protected static void start(NodeScreen instance) {
        Thread t = new Thread(instance);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void run() {
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                clearScreen();
                print(DASHED_LINE_THICK);
                print("Czas serwera: " + getTimestampFormat(1, false), new Date());
                print(DASHED_LINE_THICK);
                printAllInfo();
                print(DASHED_LINE_THICK);
                printLog();
                print(DASHED_LINE_THICK);
                Thread.sleep(SCREEN_REFRESH_INTERVAL);
            }
        } catch (Throwable t) {
            System.err.println("Exception in screen loop. Screen is terminating but node is still working.");
            t.printStackTrace();
        }
    }

    private void clearScreen() throws IOException, InterruptedException {
        if (isWindowsOS) { // http://stackoverflow.com/a/17015039
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); // http://stackoverflow.com/a/33379766
        } else {
            Runtime.getRuntime().exec("clear");
        }
    }

    protected abstract void printAllInfo();

    // http://stackoverflow.com/a/3758880/2104291
    protected String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    protected void print(String format, Object... arguments) {
        System.out.println(String.format(format, arguments));
    }

    private void printLog() {
        print("LOG:");
        for (String logEntry : logEntries) {
            print(logEntry);
        }
    }

    public static synchronized void addLogEntry(String logEntry) {
        if (instance.logEntries.size() == MAX_LOG_ENTRIES) {
            instance.logEntries.remove(0); // remove first log entry (oldest)
        }
        instance.logEntries.add(String.format("%1$4d. " + getTimestampFormat(2, true) + " %3$s",
                ++instance.currentLogEntryNumber, new Date(), logEntry));
    }

    protected static String getTimestampFormat(int formatterObjectIndex, boolean showMilis) {
        return String.format("%%%1$d$tY/%%%1$d$tm/%%%1$d$td %%%1$d$tH:%%%1$d$tM:%%%1$d$tS" + (showMilis ? ".%%%1$d$tL" : ""), formatterObjectIndex);
    }

    // used in stream's forEach
    public static void addLogEntry(Object o) {
        addLogEntry(String.valueOf(o));
    }
}
