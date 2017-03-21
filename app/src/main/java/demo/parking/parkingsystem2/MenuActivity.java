package demo.parking.parkingsystem2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.Toast;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/16.
 */
public class MenuActivity extends Activity {
    JSONParser jsonParser = new JSONParser();
    private static String findcar = "http://120.27.43.73/parking/find.php";
    private static String cancel_findcar = "http://120.27.43.73/parking/cancelfind.php";
    private static final String TAG_SUCCESS = "success";


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        Button guide = (Button)findViewById(R.id.guide);
        guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,GuideActivity.class);
                startActivity(intent);
            }
        });

        Button distribution = (Button)findViewById(R.id.distribution);
        distribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, DistributionActivity.class);
                startActivity(intent);
            }
        });


        Button sign = (Button)findViewById(R.id.sign);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MenuActivity.this,CaptureActivity.class);
                startActivity(intent);
            }
        });

        Button consult = (Button)findViewById(R.id.record);
        consult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MenuActivity.this,FeeActivity.class);
                startActivity(intent);
            }
        });

        Button zhuxiao = (Button)findViewById(R.id.zhuxiao);
        zhuxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences bSharedPreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = bSharedPreferences.edit();
                editor.putString("location", "");
                editor.commit();
                finish();
            }
        });

        Button find = (Button)findViewById(R.id.find);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences bsharedPreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
                String location = bsharedPreferences.getString("location", "");
                Log.d("--------",location);
                if(location == ""){
                    Toast.makeText(getApplicationContext(), "您尚未停车", Toast.LENGTH_SHORT).show();
                }else{
                    new FindCar().execute();
                    Toast.makeText(getApplicationContext(), "找车请求已发送，请观察闪烁灯", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button cancel_find = (Button)findViewById(R.id.cancel_find);
        cancel_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences bsharedPreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
                String location = bsharedPreferences.getString("location", "");
                Log.d("--------",location);
                if(location == ""){
                    Toast.makeText(getApplicationContext(), "您尚未停车", Toast.LENGTH_SHORT).show();
                }else{
                    new Cancel_FindCar().execute();
                    Toast.makeText(getApplicationContext(), "找车请求已取消", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    class FindCar extends AsyncTask <String,String,String>
    {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            SharedPreferences bsharedPreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
            String location = bsharedPreferences.getString("location", "");
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("location", location));

            final JSONObject jsonObject = jsonParser.makeHttpRequest(findcar, "POST", params);
            try {
                int success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1) {
                } else {
                    Log.d("Log_tag", "Failed to find.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url){

        }
    }

    class Cancel_FindCar extends AsyncTask <String,String,String>
    {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            SharedPreferences bsharedPreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
            String location = bsharedPreferences.getString("location", "");
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("location", location));

            final JSONObject jsonObject = jsonParser.makeHttpRequest(cancel_findcar, "POST", params);
            try {
                int success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1) {
                } else {
                    Log.d("Log_tag", "Failed to find.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url){

        }
    }
}
