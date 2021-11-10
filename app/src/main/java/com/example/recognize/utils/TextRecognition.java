package com.example.recognize.utils;


import android.util.Log;
import java.net.URLEncoder;

public class TextRecognition {

    private static final String BASIC_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/";

    /**
     * General text detect
     * @param accessToken
     * @param imgData
     * @return
     */
    public String universalTextRecognition(String accessToken, byte[] imgData) {
        try {
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param = "image=" + imgParam;
            String result = HttpUtil.post(BASIC_URL + "accurate_basic", accessToken, param);
            Log.e("recognition", "General text recognition results: " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * card text recognition
     * @param accessToken
     * @param imgData
     * @return
     */
    public String cardTextRecognition(String accessToken, byte[] imgData){
        try {
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;
            String result = HttpUtil.post(BASIC_URL + "business_card", accessToken, param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * id recognition
     * @param accessToken
     * @param imgData
     * @return
     */
    public String idCardRecognition(String accessToken, byte[] imgData){
        try {
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "id_card_side=" + "front" + "&image=" + imgParam;
            String result = HttpUtil.post(BASIC_URL + "idcard", accessToken, param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * handwriting recognition
     * @param accessToken
     * @param imgData
     * @return
     */
    public String handwritingRecognition(String accessToken, byte[] imgData){
        try {
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;
            String result = HttpUtil.post(BASIC_URL + "handwriting", accessToken, param);
            Log.e("TAG", "handwritingRecognition: " + result );
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * image recognition
     * @param accessToken
     * @param imgData
     * @return
     */
    public String webImageRecognition(String accessToken, byte[] imgData){
        try {
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;
            String result = HttpUtil.post(BASIC_URL + "webimage", accessToken, param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String netImageRecognition(String accessToken, String url){
        try {
            String param = "url=" + url;
            String result = HttpUtil.post(BASIC_URL + "webimage", accessToken, param);
            Log.e("recognition", "Web image text recognition results: " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }







}

