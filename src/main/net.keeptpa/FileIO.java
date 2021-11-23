import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.*;

public class FileIO {
    public static String Read(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));

        String line = "";
        String res = "";

        while((line = br.readLine()) != null){
            res += line;
        }
        br.close();
        return res;
    }

    public static void Write(String path,String str, Boolean append ) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path,append));

        bw.write(str);
        bw.close();
    }


}
