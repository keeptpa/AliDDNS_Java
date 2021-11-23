import com.aliyun.alidns20150109.models.DescribeDomainRecordsRequest;
import com.aliyun.alidns20150109.models.DescribeDomainRecordsResponse;
import com.aliyun.alidns20150109.models.DescribeDomainRecordsResponseBody;
import com.aliyun.alidns20150109.models.UpdateDomainRecordRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Entry {
    public static AppConfig aconfig;
    public static void main(String[] args) throws Exception {

        //Set config and config directory
        aconfig = new AppConfig();
        File configDirectory = new File(aconfig.path + "\\config\\");
        if(!configDirectory.exists()){
            configDirectory.mkdir();
        }
        File appConfig = new File(aconfig.path + "\\config\\appConfig.json");

        //App run once
        Logger.Log("<==============================>");
        Logger.Log("App running...");
        System.out.println("App path: " + aconfig.path);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if(appConfig.exists()){
            Logger.Log("App config found, importing..");
            String aconfigLocalJson = FileIO.Read(aconfig.path + "\\config\\appConfig.json");
            AppConfig currentLocalConfig = gson.fromJson(aconfigLocalJson,AppConfig.class);

            aconfig.key = currentLocalConfig.key;
            aconfig.sec = currentLocalConfig.sec;
            aconfig.domain = currentLocalConfig.domain;

        }else {
            Logger.Log("App config not found, create one");
            Scanner sc = new Scanner(System.in);
            //ASK
            System.out.println("Input accessKey:");
            aconfig.key = sc.nextLine();
            System.out.println("Input accessSecret:");
            aconfig.sec = sc.nextLine();
            System.out.println("Input domain");
            aconfig.domain = sc.nextLine();

            //Save
            appConfig.createNewFile();
            FileIO.Write(aconfig.path + "\\config\\appConfig.json", gson.toJson(aconfig),false);

        }
        //====>Create client
        com.aliyun.alidns20150109.Client client = ClientCreate.createClient(aconfig.key,aconfig.sec);

        //Query
        DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest()
                .setDomainName(aconfig.domain);

        DescribeDomainRecordsResponse resp = client.describeDomainRecords(describeDomainRecordsRequest);

        ArrayList<ClientConfig> thisQuery = new ArrayList<ClientConfig>();
        for (DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord temp: resp.body.domainRecords.record
             ) {
            ClientConfig cc = new ClientConfig();
            cc.currentRemoteValue = temp.value;
            cc.status = temp.status;
            cc.RR = temp.RR;
            cc.type = temp.type;
            cc.recordID = temp.recordId;

            thisQuery.add(cc);
        }

        //Check if a client config exist
        File clientConfig = new File(aconfig.path + "\\config\\clientConfig.json");
        if(!clientConfig.exists()){
            clientConfig.createNewFile();

           FileIO.Write(aconfig.path + "\\config\\clientConfig.json",gson.toJson(thisQuery),false);
        }

        //Then load the client config;
        Logger.Log("Loading client config..");
        ArrayList<ClientConfig> localClientConfig = new ArrayList<ClientConfig>();
        localClientConfig = gson.fromJson(FileIO.Read(aconfig.path + "\\config\\clientConfig.json"),new TypeToken<ArrayList<ClientConfig>>(){}.getType());
        //And check their controlling status
        Boolean needUpdate = false;
        for (ClientConfig local:localClientConfig
             ) {
            for (ClientConfig remote:thisQuery
                 ) {
                if(local.recordID.equals(remote.recordID)){
                    Logger.Log("Checking " + local.recordID + " ...");
                    remote.isControlling = local.isControlling;
                    //If it's controlling, and ip not match, set expectedValue to current external ip
                    if(remote.isControlling){
                        if(!remote.currentRemoteValue.equals(aconfig.externalIP)){
                            Logger.Log("Found RecordID: " +local.recordID +" needs update.");
                            remote.expectedValue = aconfig.externalIP;
                            needUpdate = true;
                        }
                    }
                }
            }
        }

        //Cover it.
        FileIO.Write(aconfig.path + "\\config\\clientConfig.json",gson.toJson(thisQuery),false);

        //Start update
        if(needUpdate){
            for (ClientConfig remote:thisQuery
            ) {
                if(remote.isControlling && !remote.currentRemoteValue.equals(aconfig.externalIP)){
                    UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest()
                            .setRecordId(remote.recordID)
                            .setRR(remote.RR)
                            .setType(remote.type)
                            .setValue(remote.expectedValue);
                    client.updateDomainRecord(updateDomainRecordRequest);
                    Logger.Log(remote.recordID + " is updated from " + remote.currentRemoteValue + " to " + remote.expectedValue);
                }
            }
        }


    }
}
