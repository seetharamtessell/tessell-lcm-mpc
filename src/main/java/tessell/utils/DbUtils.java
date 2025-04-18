package tessell.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import tessell.mcp.model.DatabaseServiceRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class DbUtils {
    ObjectMapper mapper = new ObjectMapper();
    public DatabaseServiceRequest createServiceRequest(String databaseName) {
        String config = readConfigFile();
        try {
            return mapper.readValue(config, DatabaseServiceRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String readConfigFile() {
//        try (InputStream is = getClass().getClassLoader().getResourceAsStream("provison.json")) {
//            if (is == null) {
//                throw new RuntimeException("File not found in resources");
//            }
//            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
       // return "{\"name\":\"mysql80_222\",\"description\":\"\",\"subscription\":\"aws-test\",\"edition\":\"COMMUNITY\",\"engineType\":\"MYSQL\",\"topology\":\"single_instance\",\"softwareImage\":\"MySQL8.0\",\"softwareImageVersion\":\"MySQL8.0.40\",\"autoMinorVersionUpdate\":true,\"enableDeletionProtection\":false,\"enableStopProtection\":false,\"infrastructure\":{\"cloud\":\"aws\",\"region\":\"us-east-1\",\"availabilityZone\":\"us-east-1a\",\"vpc\":\"tessell-vpc-4th1d\",\"privateSubnet\":\"tessell-vpc-4th1d-private-us-east-1a\",\"computeType\":\"tesl_2_a\",\"enableEncryption\":true,\"encryptionKey\":\"default-encryption-key\",\"additionalStorage\":0,\"computes\":[{\"role\":\"primary\",\"storageConfig\":{\"provider\":\"AWS_EBS\"},\"privateSubnet\":\"tessell-vpc-4th1d-private-us-east-1a\",\"availabilityZone\":\"us-east-1a\"}]},\"serviceConnectivity\":{\"servicePort\":\"3306\",\"enablePublicAccess\":false,\"allowedIpAddresses\":[],\"enableSSL\":false},\"enablePerfInsights\":false,\"creds\":{\"masterUser\":\"master\",\"masterPassword\":\"Tessell123ZX@$\"},\"maintenanceWindow\":{\"day\":\"Sunday\",\"time\":\"02:00\",\"duration\":30},\"engineConfiguration\":{\"mysqlConfig\":{\"parameterProfileId\":\"1482dbc2-0897-402a-bb0a-f878b241878a\"}},\"databases\":[{\"databaseName\":\"db1\",\"databaseConfiguration\":{\"mysqlConfig\":{\"parameterProfileId\":\"1482dbc2-0897-402a-bb0a-f878b241878a\"}}}],\"rpoPolicyConfig\":{\"enableAutoSnapshot\":true,\"standardPolicy\":{\"retentionDays\":2,\"includeTransactionLogs\":true,\"snapshotStartTime\":{\"hour\":8,\"minute\":0}}},\"tags\":[]}";
        return "{\"name\":\"my-service-d2ce2d07\",\"description\":\"\",\"subscription\":\"aws-test\",\"edition\":\"COMMUNITY\",\"engineType\":\"MYSQL\",\"topology\":\"single_instance\",\"softwareImage\":\"MySQL 8.0\",\"softwareImageVersion\":\"MySQL 8.0.40\",\"autoMinorVersionUpdate\":true,\"enableDeletionProtection\":false,\"enableStopProtection\":false,\"infrastructure\":{\"cloud\":\"aws\",\"region\":\"us-east-1\",\"availabilityZone\":\"us-east-1a\",\"vpc\":\"tessell-vpc-4th1d\",\"privateSubnet\":\"tessell-vpc-4th1d-private-us-east-1a\",\"computeType\":\"tesl_2_a\",\"enableEncryption\":true,\"encryptionKey\":\"default-encryption-key\",\"additionalStorage\":0,\"computes\":[{\"role\":\"primary\",\"storageConfig\":{\"provider\":\"AWS_EBS\"},\"privateSubnet\":\"tessell-vpc-4th1d-private-us-east-1a\",\"availabilityZone\":\"us-east-1a\"}]},\"serviceConnectivity\":{\"servicePort\":\"3306\",\"enablePublicAccess\":false,\"allowedIpAddresses\":[],\"enableSSL\":false},\"enablePerfInsights\":false,\"creds\":{\"masterUser\":\"master\",\"masterPassword\":\"Tessell123ZX@$\"},\"maintenanceWindow\":{\"day\":\"Sunday\",\"time\":\"02:00\",\"duration\":30},\"engineConfiguration\":{\"mysqlConfig\":{\"parameterProfileId\":\"1482dbc2-0897-402a-bb0a-f878b241878a\"}},\"databases\":[{\"databaseName\":\"db1\",\"databaseConfiguration\":{\"mysqlConfig\":{\"parameterProfileId\":\"1482dbc2-0897-402a-bb0a-f878b241878a\"}}}],\"rpoPolicyConfig\":{\"enableAutoSnapshot\":true,\"standardPolicy\":{\"retentionDays\":2,\"includeTransactionLogs\":true,\"snapshotStartTime\":{\"hour\":19,\"minute\":30}}},\"tags\":[]}";
    }
}
