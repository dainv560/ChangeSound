package com.nvdai.phone;

import com.nvdai.changesound.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class PhoneActivity extends Activity {
	private Context context;
	private ImageButton btCall;
	private EditText etPhoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_layout);
		context = this;
		btCall = (ImageButton) findViewById(R.id.btCall);
		etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);

		MyPhoneListener phoneListener = new MyPhoneListener();
		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(phoneListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		btCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					String uri = "tel:" + etPhoneNumber.getText().toString();
					Intent callIntent = new Intent(Intent.ACTION_CALL, Uri
							.parse(uri));

					startActivity(callIntent);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(),
							"Your call has failed...", Toast.LENGTH_LONG)
							.show();
					e.printStackTrace();
				}
			}

		});

		// dialBtn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// try {
		// String uri = "tel:"+number.getText().toString();
		// Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
		//
		// startActivity(dialIntent);
		// }catch(Exception e) {
		// Toast.makeText(getApplicationContext(),"Your call has failed...",
		// Toast.LENGTH_LONG).show();
		// e.printStackTrace();
		// }
		// }
		// });
	}

	private class MyPhoneListener extends PhoneStateListener {

		private boolean onCall = false;

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				Toast.makeText(context, incomingNumber + " calls you",
						Toast.LENGTH_LONG).show();
				break;

			case TelephonyManager.CALL_STATE_OFFHOOK:
				Toast.makeText(context, "on call...", Toast.LENGTH_LONG).show();
				onCall = true;
				break;

			case TelephonyManager.CALL_STATE_IDLE:
				if (onCall == true) {
					Toast.makeText(context, "restart app after call",
							Toast.LENGTH_LONG).show();

					Intent restart = getBaseContext().getPackageManager()
							.getLaunchIntentForPackage(
									getBaseContext().getPackageName());
					restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(restart);

					onCall = false;
				}
				break;
			default:
				break;
			}

		}
	}

}
