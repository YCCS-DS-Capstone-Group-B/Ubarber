package services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClientBuilder;
import com.amazonaws.services.elasticbeanstalk.model.DescribeInstancesHealthRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeInstancesHealthResult;
import com.amazonaws.services.elasticbeanstalk.model.InstanceHealthSummary;
import com.amazonaws.services.elasticbeanstalk.model.SingleInstanceHealth;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.*;


public class ListEBSEnvironmentInstances {
   /* public static void main(String[] args) {
        HashMap<String, String> ip = getAllDatabase();
        for(String value : ip.values()) {
            System.out.println("calling put on " + value);
            tryCall(value);
        }

    }

    */

    public static ConcurrentHashMap<String, String> getAllDatabase() {
        ConcurrentHashMap<String, String> ip = new ConcurrentHashMap<>();
        String accessKey = "AKIA36GS42GQDYUX2EPP";
        String secretKey = "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";
        String region = "us-east-2";
        String environmentName = "Database-env";

        AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(accessKey, secretKey));

        // Set up Elastic Beanstalk client with region and endpoint
        AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(
                "https://elasticbeanstalk." + region + ".amazonaws.com", region);
        AWSElasticBeanstalk client = AWSElasticBeanstalkClientBuilder.standard()
                .withEndpointConfiguration(endpoint)
                .withCredentials(credentialsProvider)
                .build();

        // Make API call to describe instances health in Elastic Beanstalk environment
        //String environmentName = "Database-env";
        DescribeInstancesHealthRequest request = new DescribeInstancesHealthRequest()
                .withEnvironmentName(environmentName);
        DescribeInstancesHealthResult result = client.describeInstancesHealth(request);
        List<SingleInstanceHealth> instances = result.getInstanceHealthList();
        for (SingleInstanceHealth instance : instances) {
            String instanceId = instance.getInstanceId();
            DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest().withInstanceIds(instanceId);
            AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(credentialsProvider).withRegion("us-east-2").build();
            DescribeInstancesResult describeInstancesResult = ec2Client.describeInstances(describeInstancesRequest);
            Instance instanceEC2 = describeInstancesResult.getReservations().get(0).getInstances().get(0);
            String ipAddress = instanceEC2.getPublicIpAddress();
            String fullUrl ="http://" + ipAddress;
            ip.put(instanceId, fullUrl);
        }
        return ip;
    }

    public static void tryCall(String ipAddress) {
        HttpClient httpClient = HttpClient.newHttpClient();

        String json = "{\n" +
                "    \"id\": 4,\n" +
                "    \"location\": \" 07621\",\n" +
                "    \"firstName\": \"Binyamin\",\n" +
                "    \"middleName\": \"wait for it\",\n" +
                "    \"lastName\": \"Jachter\",\n" +
                "    \"email\": null\n" +
                "}";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://" + ipAddress + "/barbers"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response code: " + response.statusCode());
            System.out.println("Response body: " + response.body());

        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
