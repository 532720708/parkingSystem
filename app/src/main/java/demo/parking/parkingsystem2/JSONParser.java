package demo.parking.parkingsystem2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

    static InputStream is=null;
    static JSONObject jObj=null;
    static String json="";
    //constructor
    public JSONParser()
    {}

    public JSONObject makeHttpRequest(String url,String method,List<NameValuePair> params)
    {
        try{
            // check for request method
            if(method.equals("POST")){
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                Log.d("-------------",url);
                HttpPost httpPost = new HttpPost(url);
                Log.d("-------------","11111111");
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                Log.d("-------------","21111111");
                HttpResponse httpResponse = httpClient.execute(httpPost);
                Log.d("-------------","31111111");
                HttpEntity httpEntity = httpResponse.getEntity();
                Log.d("-------------","41111111");
                is = httpEntity.getContent();
            }else if(method.equals("GET")){
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?"+ paramString;
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
                Log.e("Log_tag", " GET url is"+url.toString());
            }
            else
            {
                Log.e("---------------", "------!!!");
            }
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch(ClientProtocolException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        try{
            Log.d("============","Buffreader");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null) {
                sb.append(line + "\n");

            }
            Log.d("------------", "line is :"+sb.toString());

            is.close();
            json = sb.toString();
            Log.e("-------------------", "this json is "+json.toString());
        } catch(Exception e) {
            Log.e("---------------", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try{
            jObj = new JSONObject(json);
        } catch(JSONException e) {
            Log.d("----------", "this json"+json.toString());
            Log.e("-------------", "Error parsing data " + e.toString());
        }
        return  jObj;
    }

}