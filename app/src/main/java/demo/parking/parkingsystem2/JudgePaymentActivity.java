package demo.parking.parkingsystem2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JudgePaymentActivity extends Activity{

    JSONParser jsonParser = new JSONParser();
    /*private static String timechoose = "http://192.168.1.101:9096/car/timechoose.php";*/
    private static final String TAG_SUCCESS="success";
    private static final String ISPAID="ispaid";

    private ProgressDialog progressDialog;
    public String year2,month2,day2,hour2,minute2,second2;
    TextView TextIntime;
    TextView TextOuttime;
    TextView Texttimeminus;
    TextView Fee;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fee);
        TextIntime=(TextView)findViewById(R.id.intime);
        TextOuttime=(TextView)findViewById(R.id.outtime);
        Texttimeminus=(TextView)findViewById(R.id.parkingtime);
        Fee=(TextView)findViewById(R.id.feetopay);

        Button btnRefresh = (Button)findViewById(R.id.refresh2);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetInfo().execute();
            }
        });
        Button back = (Button)findViewById(R.id.back2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }



    class GetInfo extends AsyncTask<String,String,String>
    {
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(JudgePaymentActivity.this);
            progressDialog.setMessage("刷新中...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... args){
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            /*final JSONObject jsonObject = jsonParser.makeHttpRequest(timechoose,"GET",params);
            try{
                int success = jsonObject.getInt(TAG_SUCCESS);
                if(success==1){

                }
            }catch (JSONException e){
                e.printStackTrace();
            }*/
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    Calendar c=Calendar.getInstance();
                    year2=Integer.toString(c.get(Calendar.YEAR));
                    month2=Integer.toString(c.get(Calendar.MONTH)+1);
                    day2=Integer.toString(c.get(Calendar.DAY_OF_MONTH));
                    hour2=Integer.toString(c.get(Calendar.HOUR_OF_DAY));
                    minute2=Integer.toString(c.get(Calendar.MINUTE));
                    second2=Integer.toString(c.get(Calendar.SECOND));

                    SharedPreferences sharedPreferences = getSharedPreferences("check", Context.MODE_PRIVATE);
                    String username = sharedPreferences.getString("username", "");
                    String intime = sharedPreferences.getString("intime","");
                    String outtime=year2+"-"+month2+"-"+day2+" "+hour2+":"+minute2+":"+second2;
                    Log.d("-qwert", username + "##" + intime + "##" + outtime);
                    try
                    {
                        Date d1 = df.parse(outtime);
                        Date d2 = df.parse(intime);
                        long diff = d1.getTime() - d2.getTime();
                        long days = diff / (1000 * 60 * 60 * 24);
                        long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
                        long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
                        String timeminus =""+days+"Days"+hours+"Hours"+minutes+"Minutes";
                        double fee=(days*20+hours*3+minutes*0.05);
                        String fee2=Double.toString(fee);

                        Texttimeminus.setText(timeminus);
                        Fee.setText(fee2+"RMB");

                        Log.d("23123123123"," "+days+" "+hours+" "+minutes+"");
                    }
                    catch (Exception e)
                    {
                    }

                    TextIntime.setText(intime);
                    TextOuttime.setText(outtime);


                }
            });
            return null;
        }

        protected void onPostExecute(String file_url) {
// dismiss the dialog once got all details
            progressDialog.dismiss();
        }
    }
}
