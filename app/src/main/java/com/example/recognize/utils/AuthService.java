package com.example.recognize.utils;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Get token class
 */
public class AuthService {

    /**
     * Get permission token
     * @return return：
     * {
     * "access_token": "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567",
     * "expires_in": 2592000
     * }
     */
    public static String getAuth() {
        // The API Key obtained from the official website is updated to your registered
        String clientId = "ca34Qy9cVmS5pF38RXcUF6zn";
        // The Secret Key obtained from the official website is updated to your registered
        String clientSecret = "Bwg2BF24K2zW16ImhuBhHVw3vB4alwNe";
        return getAuth(clientId, clientSecret);
    }

    /**
     * get API to access token
     * The token is valid for a certain period of time and needs to be managed by yourself and reacquired when it expires.
     * @param ak - API Key obtained from Baidu Cloud official website
     * @param sk - Securet Key obtained from Baidu Cloud official website
     * @return assess_token sample：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     */
    private static String getAuth(String ak, String sk) {
        // Get token address
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_typeis a fixed parameter
                + "grant_type=client_credentials"
                // 2.API Key obtained from the official website
                + "&client_id=" + ak
                // 3.Secret Key obtained from the official website
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // Open a link to the URL
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // Get all response header fields
            Map<String, List<String>> map = connection.getHeaderFields();
            // Iterate through all response header fields
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // Define the BufferedReader input stream to read the response to a URL
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * return to final result
             */
            System.err.println("result:" + result);
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.printf("failed to get token！");
            e.printStackTrace(System.err);
        }
        return null;
    }

}
