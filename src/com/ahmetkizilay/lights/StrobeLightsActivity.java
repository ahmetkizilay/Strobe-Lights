package com.ahmetkizilay.lights;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class StrobeLightsActivity extends Activity {

	final Random rand = new Random();
	private LinearLayout mainLayout;
	private SeekBar speedBar;
	private Thread mainThread;
	private boolean isRunning = false;
	private boolean isOn = false;
	private long flickerInterval = 100;

	private static final int ABOUT_ID = 1000;

	private PowerManager.WakeLock wakeLock;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mainLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.main, null);

		mainLayout.setBackgroundColor(Color.BLACK);

		setContentView(mainLayout);

		speedBar = (SeekBar) findViewById(R.id.seekBar1);
		speedBar.setVisibility(View.INVISIBLE);
		speedBar.setIndeterminate(false);
		speedBar.setProgress(18);
		speedBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {
				seekBar.setVisibility(View.INVISIBLE);

			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				flickerInterval = 10 + progress * 5;
			}
		});

		mainLayout.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(View v) {
				speedBar.setVisibility(View.VISIBLE);
				return true;
			}
		});

		mainLayout.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				speedBar.setVisibility(View.INVISIBLE);
			}

		});

		// Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());

	}

	private void startThread() {
		isRunning = true;
		mainThread = new Thread(new Runnable() {
			public void run() {
				try {
					while (isRunning) {
						mainLayout.post(new Runnable() {
							public void run() {
								int bgColor = (isOn ? Color.WHITE : Color.BLACK);
								mainLayout.setBackgroundColor(bgColor);
								isOn = !isOn;
							}
						});
						Thread.sleep(flickerInterval);
					}
				} catch (Exception exp) {
				}
			}
		});

		mainThread.start();
	}

	@Override
	protected void onStop() {
		isRunning = false;
		super.onStop();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about_page:
			showDialog(ABOUT_ID);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case ABOUT_ID:
			dialog = createAboutDialog();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	private Dialog createAboutDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Strobe Lights\nVersion 1.3\n\nPERISONiC Sound And Media").setCancelable(false).setNeutralButton("OK", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return builder.create();
	}

	@Override
	protected void onResume() {
		
		startThread();
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "com.ahmetkizilay.lights.StrobeLights");
		wakeLock.acquire();
		
		super.onResume();
	}

	@Override
	protected void onPause() {
				
		if (wakeLock.isHeld()) {
			wakeLock.release();
		}
		super.onPause();
	}

	class CustomUncaughtExceptionHandler implements UncaughtExceptionHandler {
		public void uncaughtException(Thread thread, Throwable ex) {
			String message = ex.getMessage();
			System.out.println(message);
		}

	}
}