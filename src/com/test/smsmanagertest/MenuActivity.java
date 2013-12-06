package com.test.smsmanagertest;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MenuActivity extends Activity {
	EditText inusedEditText, remainEditText, allusedEditText;
	Button saveBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		inusedEditText = (EditText) this
				.findViewById(R.id.menu_textview_in_used);
		remainEditText = (EditText) this
				.findViewById(R.id.menu_textview_remain);
		allusedEditText = (EditText) this
				.findViewById(R.id.menu_textview_all_used);
		saveBtn = (Button) this.findViewById(R.id.menu_button_save);
		// saveBtn.setOnClickListener(new savaBtnOnClickListener());
		Bundle bundle = MainActivity.mSharedPreferencesProcesser.getDataInfo();
		inusedEditText.setText(bundle.getString(MainActivity.IN_USED));
		remainEditText.setText(bundle.getString(MainActivity.REMAIN));
		allusedEditText.setText(bundle.getString(MainActivity.ALL_USED));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		String inusedTmp, remainTmp, allusedTmp = "";
		inusedTmp = inusedEditText.getText().toString();
		remainTmp = remainEditText.getText().toString();
		allusedTmp = allusedEditText.getText().toString();
		if (!(inusedTmp.equals("") || remainTmp.equals("") || allusedTmp
				.equals(""))) {
			float inusedFloat, remainFloat;
			inusedFloat = Float.parseFloat(inusedTmp);
			remainFloat = Float.parseFloat(remainTmp);
			Bundle bundle = new Bundle();
			bundle.putString(MainActivity.IN_USED, inusedTmp);
			bundle.putString(MainActivity.REMAIN, remainTmp);
			bundle.putString(MainActivity.ALL_USED, allusedTmp);
			bundle.putInt(MainActivity.DURATION,
					(int) ((inusedFloat / (inusedFloat + remainFloat)) * 100));
			Toast.makeText(MenuActivity.this, "手动纠正完成！", Toast.LENGTH_SHORT)
					.show();
			MainActivity.mSharedPreferencesProcesser.setDataInfo(bundle);
			setResult(MainActivity.SUC_MODIFIED);
			finish();
		} else {
			Toast.makeText(MenuActivity.this, "请完整填写信息！", Toast.LENGTH_SHORT)
					.show();
		}

		return super.onOptionsItemSelected(item);
	}

	// class savaBtnOnClickListener implements OnClickListener {
	//
	// @Override
	// public void onClick(View v) {
	// String inusedTmp, remainTmp, allusedTmp = "";
	// inusedTmp = inusedEditText.getText().toString();
	// remainTmp = remainEditText.getText().toString();
	// allusedTmp = allusedEditText.getText().toString();
	// if (!(inusedTmp.equals("") || remainTmp.equals("") || allusedTmp
	// .equals(""))) {
	// float inusedFloat, remainFloat;
	// inusedFloat = Float.parseFloat(inusedTmp);
	// remainFloat = Float.parseFloat(remainTmp);
	// Bundle bundle = new Bundle();
	// bundle.putString(MainActivity.IN_USED, inusedTmp);
	// bundle.putString(MainActivity.REMAIN, remainTmp);
	// bundle.putString(MainActivity.ALL_USED, allusedTmp);
	// bundle.putInt(
	// MainActivity.DURATION,
	// (int) ((inusedFloat / (inusedFloat + remainFloat)) * 100));
	// Toast.makeText(MenuActivity.this, "手动纠正完成！", Toast.LENGTH_SHORT)
	// .show();
	// MainActivity.mSharedPreferencesProcesser.setDataInfo(bundle);
	// setResult(MainActivity.SUC_MODIFIED);
	// finish();
	// } else {
	// Toast.makeText(MenuActivity.this, "请完整填写信息！",
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// }
}
