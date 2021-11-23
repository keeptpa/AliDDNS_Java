import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;

public class AppConfig {
    public AppConfig() throws IOException {

        this.path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

        if(System.getProperty("os.name").contains("dows"))
        {
            this.path = this.path.substring(1,this.path.length());
        }
        if(path.contains(".jar"))
        {
            this.path = this.path.substring(0,this.path.lastIndexOf("."));
            this.path = this.path.substring(0,this.path.lastIndexOf("/"));
        }
        this.path = this.path.replace("target/classes/", "");
        //System.out.println("App config created in " + this.path);

        externalIP = Jsoup.connect("http://ipv4.icanhazip.com/").get().outerHtml();

        externalIP = externalIP.replaceAll("head","");
        externalIP = externalIP.replaceAll("html","");
        externalIP = externalIP.replaceAll("body","");
        externalIP = externalIP.replaceAll("<","");
        externalIP = externalIP.replaceAll(">","");
        externalIP = externalIP.replaceAll("\n","");
        externalIP = externalIP.replaceAll("/","");
        externalIP = externalIP.replaceAll(" ","");

        String pathSeperator = File.separator;

        appConfigPath = path + pathSeperator + "config" + pathSeperator + "appConfig.json";
        clientConfigPath = path + pathSeperator + "config" + pathSeperator + "clientConfig.json";
        logPath = path + pathSeperator + "log.txt";

        File Directory = new File(path + pathSeperator + "config" + pathSeperator);
        if(!Directory.exists()){
            Directory.mkdir();
        }
    }

    public String path;
    public String appConfigPath;
    public String clientConfigPath;
    public String logPath;
    protected String key;
    protected String sec;
    public  String domain;
    public String externalIP;
}
