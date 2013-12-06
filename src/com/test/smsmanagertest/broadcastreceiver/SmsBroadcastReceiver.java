package com.test.smsmanagertest.broadcastreceiver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.test.smsmanagertest.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SmsBroadcastReceiver extends BroadcastReceiver {

	Context mContext;
	Handler mHandler;

	private boolean firstSmsReceived = false;
	private boolean secondSmsReceived = false;
	Bundle bundleSendToMaina;;

	final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	public SmsBroadcastReceiver(Context context, Handler handler) {
		this.mContext = context;
		this.mHandler = handler;
		bundleSendToMaina = new Bundle();
		Log.i("receiver constructed", "==================");
	}

	private void sendBundle() {
		Message msg = new Message();
		msg.what = 10010;
		msg.setData(bundleSendToMaina);
		mHandler.sendMessage(msg);
		firstSmsReceived = false;
		secondSmsReceived = false;

	}

	public SmsBroadcastReceiver setFirstSmsReceived(boolean firstSmsReceived) {
		this.firstSmsReceived = firstSmsReceived;
		return this;
	}

	public SmsBroadcastReceiver setSecondSmsReceived(boolean secondSmsReceived) {
		this.secondSmsReceived = secondSmsReceived;
		return this;
	}

	private void sendToast(String toastMassage) {
		if ((firstSmsReceived || secondSmsReceived)
				&& !(firstSmsReceived && secondSmsReceived)) {
			Toast.makeText(mContext,
					"收到第一条短信(共有两条)：\n\n" + toastMassage + "\n\n正在等待第二条短信。",
					Toast.LENGTH_LONG).show();
		} else if (firstSmsReceived && secondSmsReceived) {
			Toast.makeText(mContext, "信息接受完毕：\n" + toastMassage,
					Toast.LENGTH_LONG).show();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(SMS_RECEIVED)) {
			// abortBroadcast();

			StringBuilder sb = new StringBuilder();
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] smsMessage = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					smsMessage[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}
				if (smsMessage.length != 0) {
					if (smsMessage[0].getDisplayOriginatingAddress().equals(
							"10010")) {
						abortBroadcast();
						// TODO 处理联通的短信
						for (SmsMessage currMsg : smsMessage) {
							if (currMsg.getOriginatingAddress().toString()
									.equals("10010")) {
								sb.append(currMsg.getDisplayMessageBody());
								// Log.e("Sms received", "===================");
							}
						}

						String msgContent = sb.toString();
						Pattern pattern = Pattern.compile("用(.*)MB.*量(.*)MB");
						Matcher matcher = pattern.matcher(msgContent);
						if (matcher.find()) {
							float inusedTmp = Float
									.parseFloat(matcher.group(1));
							float remainTmp = Float
									.parseFloat(matcher.group(2));
							bundleSendToMaina.putString(MainActivity.IN_USED,
									"" + inusedTmp);
							bundleSendToMaina.putString(MainActivity.REMAIN, ""
									+ remainTmp);
							bundleSendToMaina
									.putInt(MainActivity.DURATION,
											(int) ((inusedTmp / (inusedTmp + remainTmp)) * 100));
							firstSmsReceived = true;
							sendToast(sb.toString());
							if (secondSmsReceived == true) {
								sendBundle();
								firstSmsReceived = false;
								secondSmsReceived = false;
							}
						}

						pattern = Pattern.compile("）(.*)GB。根据");
						matcher = pattern.matcher(msgContent);
						if (matcher.find()) {
							bundleSendToMaina.putString(
									MainActivity.ALL_USED,
									""
											+ (int) (Float.parseFloat(matcher
													.group(1)) * 1000));
							secondSmsReceived = true;
							sendToast(sb.toString());
							if (firstSmsReceived == true) {
								sendBundle();
								firstSmsReceived = false;
								secondSmsReceived = false;
							}
						}

					} else if (smsMessage[0].getDisplayOriginatingAddress()
							.equals("10086")) {
						abortBroadcast();
						// TODO 处理移动的短信
					}
				}

			}
		}
	}
}
