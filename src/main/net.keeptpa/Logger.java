import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    static File log = new File(Entry.aconfig.logPath);
    static Date date = new Date();
    static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    public static void Log(String str) throws IOException {
        if (!log.exists()) {
            log.createNewFile();
        }
        str = "[" + formatter.format(date) + "]: " + str;
        FileIO.Write(log.getPath(),str,true);
        FileIO.Write(log.getPath(),"\n",true);
    }
}
