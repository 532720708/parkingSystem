package demo.parking.parkingsystem2;


import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

public class LoginDemoActivity extends Activity {

	String un;
	String pw;

	Button loginBtn;
	Button registerBtn;
	EditText username;
	EditText passwd;



	private ProgressDialog progressDialog;
	JSONParser jsonParser = new JSONParser();

	private static final String url_login = "http://120.27.43.73/parking/login.php";
	private static final String TAG_SUCCESS="success";

	public void register(){
		this.registerBtn = (Button)findViewById(R.id.registerbtn);
		this.registerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginDemoActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
	}



	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		username = (EditText)findViewById(R.id.loginusername);
		passwd = (EditText)findViewById(R.id.loginpassword);
		loginBtn = (Button)findViewById(R.id.loginbtn);
		loginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new UserLogin().execute();
			}
		});
		Button register = (Button)findViewById(R.id.login_btn_zhuce);
		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginDemoActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
	}



	class UserLogin extends AsyncTask<String,String,String>{
		private boolean isEmpty=true;
		protected void onPreExecute(){

			super.onPreExecute();

			if(username.getText().toString().equals("") || passwd.getText().toString().equals("") ){

				Toast.makeText(getApplicationContext(),"请填写完整",Toast.LENGTH_SHORT).show();

			}else
			{	isEmpty=false;
				progressDialog = new ProgressDialog(LoginDemoActivity.this);
				progressDialog.setMessage("登录中....");
				progressDialog.setIndeterminate(false);
				progressDialog.setCancelable(true);
				progressDialog.show();

				un = username.getText().toString();
				pw = passwd.getText().toString();
				Log.d("---string--",un+" "+pw);

			}
		}

		@Override
		protected String doInBackground(String... arg0){
			List<NameValuePair>params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", un));
			params.add(new BasicNameValuePair("passwd", pw));
			SharedPreferences mySharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = mySharedPreferences.edit();

			JSONObject jsonObject = jsonParser.makeHttpRequest(url_login,"POST",params);
			Log.d("Creating Response----",jsonObject.toString());

			try{
				int success = jsonObject.getInt(TAG_SUCCESS);
				if (success == 1) {
					//登录成功
					Intent i = new Intent(getApplicationContext(), MenuActivity.class);
					startActivity(i);
					editor.putString("username", un);
					Log.d("--===s=s=s=", un);
					editor.commit();
				} else {
					//登录失败
					Log.d("Log_tag", "Faile to login.");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d("Creating Response", "222222222222222");
			return null;
			}
		protected void onPostExecute(String file_url) {
			if(!isEmpty)
			progressDialog.dismiss();
			Log.d("Creating Response", "3333333333333333333");
		}
	}

}
