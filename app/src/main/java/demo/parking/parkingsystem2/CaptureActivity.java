package demo.parking.parkingsystem2;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtr.zbar.build.ZBarDecoder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


public class CaptureActivity extends Activity {

	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;
	private CameraManager mCameraManager;

	private TextView scanResult;
	private FrameLayout scanPreview;
	private Button scanRestart;
	private RelativeLayout scanContainer;
	private RelativeLayout scanCropView;
	private ImageView scanLine;

	private Rect mCropRect = null;
	private boolean barcodeScanned = false;
	private boolean previewing = true;

	public String a=null;
	public String b=null;
	public String year,month,day,hour,minute,second;



	JSONParser jsonParser = new JSONParser();
	private ProgressDialog progressDialog;
	private static String carwrite = "http://120.27.43.73/parking/carwrite.php";
	private static final String TAG_SUCCESS = "success";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		findViewById();
		addEvents();
		initViews();
	}

	private void findViewById() {
		scanPreview = (FrameLayout) findViewById(R.id.capture_preview);
		scanResult = (TextView) findViewById(R.id.capture_scan_result);
		scanRestart = (Button) findViewById(R.id.capture_restart_scan);
		scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
		scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
		scanLine = (ImageView) findViewById(R.id.capture_scan_line);
	}

	private void addEvents() {
		scanRestart.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (barcodeScanned) {
					barcodeScanned = false;
					scanResult.setText("Scanning...");
					mCamera.setPreviewCallback(previewCb);
					mCamera.startPreview();
					previewing = true;
					mCamera.autoFocus(autoFocusCB);
				}
			}
		});
	}

	private void initViews() {
		autoFocusHandler = new Handler();
		mCameraManager = new CameraManager(this);
		try {
			mCameraManager.openDriver();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mCamera = mCameraManager.getCamera();
		mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
		scanPreview.addView(mPreview);

		TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
				0.85f);
		animation.setDuration(3000);
		animation.setRepeatCount(-1);
		animation.setRepeatMode(Animation.REVERSE);
		scanLine.startAnimation(animation);
	}

	public void onPause() {
		super.onPause();
		releaseCamera();
	}

	private void releaseCamera() {
		if (mCamera != null) {
			previewing = false;
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Size size = camera.getParameters().getPreviewSize();

			byte[] rotatedData = new byte[data.length];
			for (int y = 0; y < size.height; y++) {
				for (int x = 0; x < size.width; x++)
					rotatedData[x * size.height + size.height - y - 1] = data[x + y * size.width];
			}

			int tmp = size.width;
			size.width = size.height;
			size.height = tmp;

			initCrop();
			ZBarDecoder zBarDecoder = new ZBarDecoder();
			String result = zBarDecoder.decodeCrop(rotatedData, size.width, size.height, mCropRect.left, mCropRect.top, mCropRect.width(), mCropRect.height());

			if (!TextUtils.isEmpty(result)) {
				previewing = false;
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();

				scanResult.setText("" + result);
				Log.d("-----", result);
				barcodeScanned = true;
				a=result.substring(0,5);
				Log.d("---carlocation",a);
				b=result.substring(5,10);
				Log.d("-----",b);
				if(b.equals("#time"))
				{
					Calendar c=Calendar.getInstance();
					year=Integer.toString(c.get(Calendar.YEAR));
					month=Integer.toString(c.get(Calendar.MONTH)+1);
					day=Integer.toString(c.get(Calendar.DAY_OF_MONTH));
					hour=Integer.toString(c.get(Calendar.HOUR_OF_DAY));
					minute=Integer.toString(c.get(Calendar.MINUTE));
					second=Integer.toString(c.get(Calendar.SECOND));
					Log.d("----time",year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second);
					SharedPreferences bSharedPreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = bSharedPreferences.edit();
					editor.putString("location", a);
					editor.commit();
					new WriteCar().execute();
				}

			}


		}
	};

	// Mimic continuous auto-focusing
	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};

	/**
	 * 初始化截取的矩形区域
	 */
	private void initCrop() {
		int cameraWidth = mCameraManager.getCameraResolution().y;
		int cameraHeight = mCameraManager.getCameraResolution().x;

		/** 获取布局中扫描框的位置信息 */
		int[] location = new int[2];
		scanCropView.getLocationInWindow(location);

		int cropLeft = location[0];
		int cropTop = location[1] - getStatusBarHeight();

		int cropWidth = scanCropView.getWidth();
		int cropHeight = scanCropView.getHeight();

		/** 获取布局容器的宽高 */
		int containerWidth = scanContainer.getWidth();
		int containerHeight = scanContainer.getHeight();

		/** 计算最终截取的矩形的左上角顶点x坐标 */
		int x = cropLeft * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的左上角顶点y坐标 */
		int y = cropTop * cameraHeight / containerHeight;

		/** 计算最终截取的矩形的宽度 */
		int width = cropWidth * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的高度 */
		int height = cropHeight * cameraHeight / containerHeight;

		/** 生成最终的截取的矩形 */
		mCropRect = new Rect(x, y, width + x, height + y);
	}

	private int getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	class WriteCar extends AsyncTask<String,String,String>{
		protected void onPreExecute(){
			super.onPreExecute();
		}

		@Override
		protected  String doInBackground(String... arg0){
			SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
			String username = sharedPreferences.getString("username", "");
			SharedPreferences bsharedPreferences = getSharedPreferences("location", Context.MODE_PRIVATE);
			String location = bsharedPreferences.getString("location", "");





			String intime=year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
			String outtime=year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
			String ispaid="unpaid";
			String timeinfo="";

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("intime",intime));
			params.add(new BasicNameValuePair("username",username));
			params.add(new BasicNameValuePair("outtime",outtime));
			params.add(new BasicNameValuePair("ispaid",ispaid));
			params.add(new BasicNameValuePair("timeinfo",timeinfo));
			params.add(new BasicNameValuePair("location",location));
			Log.d("zaaaaaaaaaaaaa",location);
			Log.d("--===s=s=s=", intime+"  "+username+"  "+outtime+"  "+ispaid);
			JSONObject jsonObject = jsonParser.makeHttpRequest(carwrite, "POST", params);
			Log.d("Creating Response", jsonObject.toString());

			try {
				int success = jsonObject.getInt(TAG_SUCCESS);
				if (success == 1) {
					Intent i = new Intent(getApplicationContext(), DistributionActivity.class);
					startActivity(i);
					finish();
				} else {
					Log.d("Log_tag", "Failed to write.");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d("Creating Response", "222222222222222");
			return null;
		}

		protected void onPostExecute(String file_url){
			Log.d("Creating Response", "3333333333333333333");
		}
	}

}
