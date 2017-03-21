package demo.parking.parkingsystem2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DistributionActivity extends Activity {

    private ProgressDialog progressDialog;
    JSONParser jsonParser = new JSONParser();
    ImageView car11;
    ImageView car12;
    ImageView car13;
    ImageView car21;
    ImageView car22;
    ImageView car23;
    ImageView car31;
    ImageView car32;
    ImageView car33;

    private static String ocptable = "http://120.27.43.73/parking/ocptable.php";
    private static final String TAG_SUCCESS="success";
    private static final String TAG_OCCUPYINFO="occupyinfo";
    private static final String TAG_LOCATION="location";
    private static final String TAG_ISOCP="isocp";
    private ArrayList<ImageView> cars = new ArrayList<ImageView>();
    JSONArray occupyinfo=null;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distribution);
        cars=new ArrayList<ImageView>();
        car11=(ImageView)findViewById(R.id.car11);
        car12=(ImageView)findViewById(R.id.car12);
        car13=(ImageView)findViewById(R.id.car13);
        car21=(ImageView)findViewById(R.id.car21);
        car22=(ImageView)findViewById(R.id.car22);
        car23=(ImageView)findViewById(R.id.car23);
        car31=(ImageView)findViewById(R.id.car31);
        car32=(ImageView)findViewById(R.id.car32);
        car33=(ImageView)findViewById(R.id.car33);

        cars.add(car11);
        cars.add(car12);
        cars.add(car13);
        cars.add(car21);
        cars.add(car22);
        cars.add(car23);
        cars.add(car31);
        cars.add(car32);
        cars.add(car33);

        new JudgeCar().execute();


        Button back = (Button)findViewById(R.id.back1);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button refresh = (Button)findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new JudgeCar().execute();
            }
        });


    }



    class JudgeCar extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(DistributionActivity.this);
            progressDialog.setMessage("刷新中...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            Log.d("Refresh", "-----------");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... args){
            List<NameValuePair>params = new ArrayList<NameValuePair>();
            final JSONObject jsonObject = jsonParser.makeHttpRequest(ocptable,"GET",params);

            Log.d("----------","==========-=-=-=-");
            try{
                int success = jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                occupyinfo = jsonObject.getJSONArray(TAG_OCCUPYINFO);

                                for (int i = 0; i < cars.size(); i++) {
                                    JSONObject c = occupyinfo.getJSONObject(i);
                                    String location = c.getString(TAG_LOCATION);
                                    String isocp = c.getString(TAG_ISOCP);
                                    Log.d("-----------------", location + ":" + isocp);
                                    int j = Integer.parseInt(isocp);
                                    if (j == 1) {
                                        cars.get(i).setImageResource(R.drawable.red);
                                    } else {
                                        cars.get(i).setImageResource(R.drawable.green);
                                    }
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    });

                }else{
                    Intent i = new Intent(getApplicationContext(),
                            MenuActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String file_url){
            progressDialog.dismiss();


        }
    }
}
