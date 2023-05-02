package utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import ch.hsr.geohash.GeoHash;
import pojos.Barber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShardingUtils {

    private static List<Integer> getBuckets(double latitude, double longitude, int nBuckets) {
        double US_LATITUDE_MIN = 25;
        double US_LATITUDE_MAX = 50;
        double US_LONGITUDE_MIN = -124;
        double US_LONGITUDE_MAX = -66;
        double NEIGHBORHOOD_RANGE = 1.125; //20 miles in degrees

        double US_LONGITUDE_RANGE = Math.abs(Math.abs(US_LONGITUDE_MAX) - Math.abs(US_LONGITUDE_MIN));
        double intervalSize = US_LONGITUDE_RANGE / nBuckets;

        if(latitude < US_LATITUDE_MIN || latitude > US_LATITUDE_MAX) {
            return Collections.singletonList(-1);
        }
        if(longitude < US_LONGITUDE_MIN || longitude > US_LONGITUDE_MAX) {
            return Collections.singletonList(-1);
        }

        int bucketIndex = (int) Math.floor((longitude - US_LONGITUDE_MIN) / intervalSize);
        double bucketMinLongitude = US_LONGITUDE_MIN + bucketIndex * intervalSize;

        List<Integer> result = new ArrayList<>();
        result.add(bucketIndex);

        if (bucketIndex > 0) {
            double leftBucketMaxLongitude = bucketMinLongitude;
            double leftBucketMinLongitude = leftBucketMaxLongitude - intervalSize;
            if (longitude - leftBucketMaxLongitude <= NEIGHBORHOOD_RANGE && longitude >= leftBucketMinLongitude) {
                result.add(bucketIndex - 1);
            }
        }

        if (bucketIndex < nBuckets - 1) {
            double rightBucketMinLongitude = bucketMinLongitude + intervalSize;
            double rightBucketMaxLongitude = rightBucketMinLongitude + intervalSize;
            if (rightBucketMinLongitude - longitude <= NEIGHBORHOOD_RANGE && longitude <= rightBucketMaxLongitude) {
                result.add(bucketIndex + 1);
            }
        }

        return result;
    }

    private static int getBucket(double latitude, double longitude, int nBuckets) {
        double US_LATITUDE_MIN = 25;
        double US_LATITUDE_MAX = 50;
        double US_LONGITUDE_MIN = -124;
        double US_LONGITUDE_MAX = -66;

        double US_LONGITUDE_RANGE = Math.abs(Math.abs(US_LONGITUDE_MAX) - Math.abs(US_LONGITUDE_MIN));
        double intervalSize = US_LONGITUDE_RANGE / nBuckets;
        if(latitude < US_LATITUDE_MIN || latitude > US_LATITUDE_MAX) {
            return -1;
        }
        if(longitude < US_LONGITUDE_MIN || longitude > US_LONGITUDE_MAX) {
            return -1;
        }

        return (int) Math.floor((longitude - US_LONGITUDE_MIN) / intervalSize);
    }

    public static double euclideanDistance(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    /*public static int getBucket(double latitude, double longitude) {
        GeoHash geohash = GeoHash.withCharacterPrecision(latitude, longitude, 10);
        String geohashString = geohash.toBase32();
        int hashValue = geohashString.charAt(0) - '0'; // convert first character to integer
        return hashValue % 3;
    }*/

    public static double[] getLatLong(String zipCode) {
        String apiKey = "2GA1rNSZAuqUT7YrAGUkFb94PJ3IQGnX";
        double[] latLong = new double[2];

        try {
            // Construct the API using the zip
            String apiUrl = String.format("https://api.tomtom.com/search/2/geocode/%s.json?key=%s&countrySet=US", zipCode, apiKey);

            // Send an HTTP GET request to the API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Parse the JSON response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject position = jsonResponse.getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONObject("position");

            // save the lat and long
            double lat = position.getDouble("lat");
            latLong[0] = lat;
            double lng = position.getDouble("lon");
            latLong[1] = lng;

        } catch (Exception e) {
            e.printStackTrace(); //maybe can have it return a 404 or controller can handle it and send back 404
        }

        return latLong;
    }
    public static int getBucket(String zip, int nBuckets){
        double[] latLong = ShardingUtils.getLatLong(zip);
        return ShardingUtils.getBucket(latLong[0], latLong[1], nBuckets);
    }
    public static List<Integer> getBuckets(String zip, int nBuckets){
        double[] latLong = ShardingUtils.getLatLong(zip);
        return ShardingUtils.getBuckets(latLong[0], latLong[1], nBuckets);
    }


    public static String getGeoHash(double latitude, double longitude) {
        GeoHash geohash = GeoHash.withCharacterPrecision(latitude, longitude, 12);
        return geohash.toBase32();
    }
}
