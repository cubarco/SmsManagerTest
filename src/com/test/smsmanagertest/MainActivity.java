package com.test.smsmanagertest;

import com.test.smsmanagertest.broadcastreceiver.SmsBroadcastReceiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.telephony.gsm.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
@SuppressLint({ "UnlocalizedSms", "WorldWriteableFiles" })
public class MainActivity extends Activity implements Callback {

	Button sendBtn;
	TextView inUsedText;
	TextView remainText;
	TextView allUsedText;
	TextView allContentText;
	ProgressBar progressBar;
	ProgressBar progressBar2;

	SmsManager sManager;
	SmsBroadcastReceiver mSmsBroadcastReceiver;

	Handler mHandler;

	public static SharedPreferencesProcesser mSharedPreferencesProcesser;

	public final static String IN_USED = "in_used";
	public final static String REMAIN = "remain";
	public final static String ALL_USED = "all_used";
	public final static String DURATION = "durration";
	public final static String MODIFIED = "modified";
	public final static String DATA_INFO = "datainfo";
	public final static int FROM_SETTING = 100;
	public final static int SUC_MODIFIED = 101;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSharedPreferencesProcesser = new SharedPreferencesProcesser();

		sManager = SmsManager.getDefault();

		mHandler = new Handler(this);
		mSmsBroadcastReceiver = new SmsBroadcastReceiver(MainActivity.this,
				mHandler);
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(mSmsBroadcastReceiver, filter);

		progressBar = (ProgressBar) this.findViewById(R.id.progressBar1);
		progressBar2 = (ProgressBar) this.findViewById(R.id.progressBar2);
		progressBar2.setMax(100);

		inUsedText = (TextView) this.findViewById(R.id.textview_in_used);
		allUsedText = (TextView) this.findViewById(R.id.textview_all_used);
		remainText = (TextView) this.findViewById(R.id.textview_remain);
		allContentText = (TextView) this
				.findViewById(R.id.textview_all_content);
		sendBtn = (Button) this.findViewById(R.id.button1);
		// sendBtn.setOnClickListener(new sendBtnOnClickListener());

		setText();

	}

	private void showDialog() {
		new AlertDialog.Builder(MainActivity.this)
				.setTitle("欢迎")
				.setMessage("    没有发现本地储存的流量信息，可能是第一次使用，请初始化流量信息。")
				.setNegativeButton("短信校正",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								PendingIntent piIntent = PendingIntent
										.getActivity(MainActivity.this, 0,
												new Intent(), 0);

								sManager.sendTextMessage("10010", null, "cxll",
										piIntent, null);
								sManager.sendTextMessage("10010", null, "1076",
										piIntent, null);
								Toast.makeText(MainActivity.this, "短信发送完成。",
										Toast.LENGTH_SHORT).show();
								progressBar.setVisibility(ProgressBar.VISIBLE);
							}
						})
				.setPositiveButton("手动校正",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent();
								intent.setClass(MainActivity.this,
										MenuActivity.class);
								startActivityForResult(intent, FROM_SETTING);
							}
						}).show();

	}

	private void setText() {
		Bundle bundle = mSharedPreferencesProcesser.getDataInfo();
		if (bundle.getBoolean(MODIFIED)) {
			String inusedTmp, remainTmp;
			int biliTmp;

			inusedTmp = "套餐内已使用： " + bundle.getString(IN_USED) + " MB";
			remainTmp = "套餐内剩余流量： " + bundle.getString(REMAIN) + " MB";
			biliTmp = bundle.getInt(DURATION);

			inUsedText.setText(inusedTmp);
			remainText.setText(remainTmp);
			progressBar2.setProgress(biliTmp);
			allUsedText.setText("流量大约总使用量： " + bundle.getString(ALL_USED)
					+ " MB");

			inUsedText.setVisibility(TextView.VISIBLE);
			remainText.setVisibility(TextView.VISIBLE);
			allUsedText.setVisibility(TextView.VISIBLE);
			progressBar2.setVisibility(ProgressBar.VISIBLE);
		} else {
			// Toast.makeText(MainActivity.this, "    暂无流量数据信息，请选择短信获取或手动输入。",
			// Toast.LENGTH_LONG).show();
			showDialog();
		}
	}

	public class SharedPreferencesProcesser {
		SharedPreferences preferences;
		SharedPreferences.Editor editor;

		public SharedPreferencesProcesser() {
			// TODO Auto-generated constructor stub
			preferences = getSharedPreferences("datainfo", MODE_WORLD_WRITEABLE);
			editor = preferences.edit();
		}

		public Bundle getDataInfo() {
			Bundle bundle = new Bundle();
			bundle.putString(IN_USED, preferences.getString(IN_USED, ""));
			bundle.putString(REMAIN, preferences.getString(REMAIN, ""));
			bundle.putString(ALL_USED, preferences.getString(ALL_USED, ""));
			bundle.putInt(DURATION, preferences.getInt(DURATION, 0));
			bundle.putBoolean(MODIFIED, preferences.getBoolean(MODIFIED, false));
			return bundle;
		}

		public void setDataInfo(Bundle bundle) {
			editor.putString(IN_USED, "" + bundle.getString(IN_USED));
			editor.putString(REMAIN, "" + bundle.getString(REMAIN));
			editor.putString(ALL_USED, "" + bundle.getString(ALL_USED));
			editor.putInt(DURATION, bundle.getInt(DURATION));
			editor.putBoolean(MODIFIED, true);
			editor.commit();
		}

		public SharedPreferences getPreferences() {
			return preferences;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mSmsBroadcastReceiver);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == FROM_SETTING && resultCode == SUC_MODIFIED) {
			setText();
		}
	}

	// class sendBtnOnClickListener implements OnClickListener {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// PendingIntent piIntent = PendingIntent.getActivity(
	// MainActivity.this, 0, new Intent(), 0);
	//
	// mSmsBroadcastReceiver.setFirstSmsReceived(false)
	// .setSecondSmsReceived(false);
	//
	// sManager.sendTextMessage("10010", null, "cxll", piIntent, null);
	// sManager.sendTextMessage("10010", null, "1076", piIntent, null);
	// Toast.makeText(MainActivity.this, "短信发送完成,等待回复短信。",
	// Toast.LENGTH_LONG).show();
	// progressBar.setVisibility(ProgressBar.VISIBLE);
	// }
	//
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("手动校正")) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, MenuActivity.class);
			startActivityForResult(intent, FROM_SETTING);
		} else if (item.getTitle().equals("发送短信校正")) {
			PendingIntent piIntent = PendingIntent.getActivity(
					MainActivity.this, 0, new Intent(), 0);

			mSmsBroadcastReceiver.setFirstSmsReceived(false)
					.setSecondSmsReceived(false);

			sManager.sendTextMessage("10010", null, "cxll", piIntent, null);
			sManager.sendTextMessage("10010", null, "1076", piIntent, null);
			Toast.makeText(MainActivity.this, "短信发送完成,等待回复短信。",
					Toast.LENGTH_LONG).show();
			progressBar.setVisibility(ProgressBar.VISIBLE);
		}
		return true;
	}

	@Override
	public boolean handleMessage(Message msg) {
		// previewText.setText(msg.getData().getString("content"));
		switch (msg.what) {
		case 10010:
			Bundle bundleFromSmsReceiver = msg.getData();
			mSharedPreferencesProcesser.setDataInfo(bundleFromSmsReceiver);
			setText();
			progressBar.setVisibility(ProgressBar.GONE);
			break;
		default:
			break;
		}

		return false;
	}
}
