package accesscontrol;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
    public static void logCardEvent(String cardID, String event) {
        try {
            String timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date());
            String logEntry = String.format("%s,%s,%s\n", cardID, event, timestamp);
            FileWriter writer = new FileWriter("card_audit.txt", true);
            writer.write(logEntry);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error logging card event: " + e.getMessage());
        }
    }
}