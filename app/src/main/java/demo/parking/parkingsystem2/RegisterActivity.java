package demo.parking.parkingsystem2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class RegisterActivity extends Activity {
	String	un ;
	String pw ;
	String pn ;
	String cn;
	String cpw;

	private ProgressDialog progressDialog;
	JSONParser jsonParser = new JSONParser();
	EditText username;
	EditText password;
	EditText confirmpassword;
	EditText phonenumber;
	EditText carnumber;
	private static String url_register = "http://120.27.43.73/parking/engage_register.php";
	private static final String TAG_SUCCESS = "success";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		username = (EditText) findViewById(R.id.newUser_input);
		password = (EditText) findViewById(R.id.newPassword_input);
		confirmpassword = (EditText) findViewById(R.id.Confirm_input);
		phonenumber = (EditText) findViewById(R.id.phonenumber_input);
		carnumber = (EditText) findViewById(R.id.carnumber_input);
		Button registerbtn = (Button) findViewById(R.id.registerbtn);
		registerbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new RegisterUser().execute();
			}
		});

	}


	class RegisterUser extends AsyncTask<String, String, String> {
		protected void onPreExecute() {

			cpw = confirmpassword.getText().toString();
			un = username.getText().toString();
			pw = password.getText().toString();
			pn = phonenumber.getText().toString();
			cn = carnumber.getText().toString();


			super.onPreExecute();
			Log.d("qeqwfdafz", password + "  " + confirmpassword);
			if( pw.equals(cpw)) {
				progressDialog = new ProgressDialog(RegisterActivity.this);
				progressDialog.setMessage("注册中.....");
				progressDialog.setIndeterminate(false);
				progressDialog.setCancelable(true);
				progressDialog.show();


			}else
			{
				Toast.makeText(getApplicationContext(), "两次输入密码不一致", Toast.LENGTH_SHORT).show();
			}


		}

		@Override
		protected String doInBackground(String... arg0) {


			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", un));
			params.add(new BasicNameValuePair("passwd", pw));
			params.add(new BasicNameValuePair("phonenumb", pn));
			params.add(new BasicNameValuePair("carnumb", cn));

			Log.d("----info-----",un+" "+pw+" "+cpw);


			JSONObject jsonObject = jsonParser.makeHttpRequest(url_register, "POST", params);
			Log.d("Creating Response", jsonObject.toString());

			try {
				int success = jsonObject.getInt(TAG_SUCCESS);
				if (success == 1) {
					Intent i = new Intent(getApplicationContext(), MenuActivity.class);
					startActivity(i);
					finish();
				} else {
					Log.d("Log_tag", "Failed to register.");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d("Creating Response", "222222222222222");
			return null;
		}

		protected void onPostExecute(String file_url) {

			progressDialog.dismiss();
			Log.d("Creating Response", "3333333333333333333");
		}
	}
}