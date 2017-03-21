package demo.parking.parkingsystem2;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class FeeActivity extends ListActivity {

    private ProgressDialog progressDialog;
    JSONParser jsonParser = new JSONParser();
    ArrayList<HashMap<String,String>> timeList;
    private static String timetable="http://120.27.43.73/parking/timetable.php";

    public String year2,month2,day2,hour2,minute2,second2;

    private static final String TAG_SUCCESS="success";
    private static final String TAG_TIMEINFO="timeinfo";
    private static final String TAG_USERNAME="username";
    private static final String TAG_INTIME="intime";
    private static final String TAG_OUTTIME="outtime";
    private static final String TAG_ISPAID="ispaid";

    JSONArray timeinfo=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        timeList = new ArrayList<HashMap<String, String>>();

        new LoadTime().execute();

        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences aSharedPreferences = getSharedPreferences("check", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = aSharedPreferences.edit();

                Calendar c=Calendar.getInstance();
                year2=Integer.toString(c.get(Calendar.YEAR));
                month2=Integer.toString(c.get(Calendar.MONTH)+1);
                day2=Integer.toString(c.get(Calendar.DAY_OF_MONTH));
                hour2=Integer.toString(c.get(Calendar.HOUR_OF_DAY));
                minute2=Integer.toString(c.get(Calendar.MINUTE));
                second2=Integer.toString(c.get(Calendar.SECOND));

                String username =((TextView)view.findViewById(R.id.username)).getText().toString();
                String intime =((TextView)view.findViewById(R.id.intime2)).getText().toString();
                String outtime=year2+"-"+month2+"-"+day2+" "+hour2+":"+minute2+":"+second2;

                Intent in = new Intent(getApplicationContext(),JudgePaymentActivity.class);
                in.putExtra(TAG_USERNAME,username);
                in.putExtra(TAG_INTIME,intime);
                in.putExtra(TAG_OUTTIME,outtime);

                editor.putString("username", username);
                editor.putString("intime", intime);
                editor.commit();

                startActivityForResult(in, 100);
            }
        });

        Button back = (Button) findViewById(R.id.back3);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

        @Override
        protected void onActivityResult(int requestCode,int resultCode,Intent data){
            super.onActivityResult(requestCode,resultCode,data);
            if(resultCode == 100){
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }

        class LoadTime extends AsyncTask<String,String,String>
        {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(FeeActivity.this);
                progressDialog.setMessage("Loading....");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
            @Override
            protected String doInBackground(String... args) {

                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("username", "");
                Log.d("-qqqqqqq",username);

                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username",username));
                // getting JSON string from URL
                JSONObject json = jsonParser.makeHttpRequest(timetable, "POST", params);
                // Check your log cat for JSON reponse
                Log.d("-------------","987654321");
                Log.d("Time ", json.toString());
                try{
                    // Checking for SUCCESS TAG
                    int success = json.getInt(TAG_SUCCESS);
                    if(success == 1)
                    {
// products found
// Getting Array of Products
                        timeinfo = json.getJSONArray(TAG_TIMEINFO);
// looping through All Products
                        for(int i = 0; i < timeinfo.length(); i++) {
                            JSONObject c = timeinfo.getJSONObject(i);
// Storing each json item in variable
                            String intime = c.getString(TAG_INTIME);
                            String outtime = c.getString(TAG_OUTTIME);
                            String ispaid = c.getString(TAG_ISPAID);
// creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();
// adding each child node to HashMap key => value
                            map.put(TAG_USERNAME, username);
                            map.put(TAG_INTIME,intime);
                            map.put(TAG_OUTTIME,outtime);
                            map.put(TAG_ISPAID,ispaid);
// adding HashList to ArrayList
                            timeList.add(map);
                        }
                    } else{
// no products found
// Launch Add New product Activity
                        Intent i = new Intent(getApplicationContext(),
                                MenuActivity.class);
// Closing all previous activities
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
            /**
             * After completing background task Dismiss the progress dialog
             * **/

            protected void onPostExecute(String file_url) {
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //updating parsed JSON data into ListView
                        ListAdapter adapter=new SimpleAdapter
                                (FeeActivity.this,timeList,R.layout.list,
                                        new String[]{TAG_USERNAME,TAG_INTIME,TAG_OUTTIME,TAG_ISPAID},
                                        new int[]{R.id.username,R.id.intime2,R.id.outtime2,R.id.ispaid});
                        setListAdapter(adapter);
                    }
                });
            }
        }

}
