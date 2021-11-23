import com.aliyun.tea.*;
import com.aliyun.alidns20150109.*;
import com.aliyun.alidns20150109.models.*;
import com.aliyun.teaopenapi.*;
import com.aliyun.teaopenapi.models.*;

public class ClientCreate {
    public static com.aliyun.alidns20150109.Client createClient(String key,String sec) throws Exception {
            Config config = new Config()
                    .setAccessKeyId(key)
                    .setAccessKeySecret(sec);
            config.endpoint = "alidns.cn-beijing.aliyuncs.com";

            return new com.aliyun.alidns20150109.Client(config);
    }
}
