package com.doconline.doconline.floating;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.doconline.doconline.R;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.appointment.AppointmentSummeryActivity;

import static com.doconline.doconline.app.Constants.KEY_APPOINTMENT_ID;
import static com.doconline.doconline.app.Constants.KEY_REMAINING_TIME;
import static com.doconline.doconline.app.MyApplication.prefs;


public class TimerService extends Service
{
	private WindowManager windowManager;
	private FrameLayout timerView, removeView;
	private TextView tvTimer;
	private ImageView removeImg;

	private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
	private Point szWindow = new Point();


	@SuppressWarnings("deprecation")
	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.i(Utils.LogTag, "TimerService.onCreate()");

		ReverseCountDownTimer(prefs.getInt(KEY_REMAINING_TIME, 300));
	}


	public void ReverseCountDownTimer(long Seconds)
	{
		new CountDownTimer(Seconds * 1000 + 1000, 1000)
		{
			public void onTick(long millisUntilFinished)
			{
				try
				{
					prefs.edit().putInt(KEY_REMAINING_TIME, prefs.getInt(KEY_REMAINING_TIME, 300)-1).apply();

					int seconds = (int) (millisUntilFinished / 1000);
					int minutes = seconds / 60;
					seconds = seconds % 60;
					tvTimer.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
				}

				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			public void onFinish()
			{
				try
				{
					prefs.edit().putInt(KEY_REMAINING_TIME, 300).apply();
					tvTimer.setText("00:00");

					if(MyApplication.getInstance().isServiceRunning())
					{
						stopService(new Intent(MyApplication.getInstance(), TimerService.class));
					}
				}

				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void handleStart()
	{
		try
		{
			windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

			LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

			removeView = (FrameLayout) inflater.inflate(R.layout.timer_remove_view, null);
			WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
					PixelFormat.TRANSLUCENT);
			paramRemove.gravity = Gravity.TOP | Gravity.LEFT;

			removeView.setVisibility(View.GONE);
			removeImg = removeView.findViewById(R.id.remove_img);
			windowManager.addView(removeView, paramRemove);


			timerView = (FrameLayout) inflater.inflate(R.layout.timer_view, null);
			tvTimer = timerView.findViewById(R.id.tv_timer);


			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			{
				windowManager.getDefaultDisplay().getSize(szWindow);
			}

			else
			{
				int w = windowManager.getDefaultDisplay().getWidth();
				int h = windowManager.getDefaultDisplay().getHeight();
				szWindow.set(w, h);
			}

			WindowManager.LayoutParams params = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
					PixelFormat.TRANSLUCENT);
			params.gravity = Gravity.TOP | Gravity.LEFT;
			params.x = 0;
			params.y = 100;
			windowManager.addView(timerView, params);

			timerView.setOnTouchListener(new View.OnTouchListener() {

				long time_start = 0, time_end = 0;
				boolean isLongClick = false, inBounded = false;
				int remove_img_width = 0, remove_img_height = 0;

				Handler handler_longClick = new Handler();
				Runnable runnable_longClick = new Runnable() {

					@Override
					public void run()
					{
						Log.d(Utils.LogTag, "Into runnable_longClick");

						isLongClick = true;
						removeView.setVisibility(View.VISIBLE);
						timer_long_click();
					}
				};


				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) timerView.getLayoutParams();

					int x_cord = (int) event.getRawX();
					int y_cord = (int) event.getRawY();
					int x_cord_Destination, y_cord_Destination;

					switch (event.getAction())
					{

						case MotionEvent.ACTION_DOWN:

							time_start = System.currentTimeMillis();
							handler_longClick.postDelayed(runnable_longClick, 600);

							remove_img_width = removeImg.getLayoutParams().width;
							remove_img_height = removeImg.getLayoutParams().height;

							x_init_cord = x_cord;
							y_init_cord = y_cord;

							x_init_margin = layoutParams.x;
							y_init_margin = layoutParams.y;

							break;

						case MotionEvent.ACTION_MOVE:

							int x_diff_move = x_cord - x_init_cord;
							int y_diff_move = y_cord - y_init_cord;

							x_cord_Destination = x_init_margin + x_diff_move;
							y_cord_Destination = y_init_margin + y_diff_move;

							if(isLongClick)
							{
								int x_bound_left = szWindow.x / 2 - (int)(remove_img_width * 1.5);
								int x_bound_right = szWindow.x / 2 +  (int)(remove_img_width * 1.5);
								int y_bound_top = szWindow.y - (int)(remove_img_height * 1.5);

								if((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top)
								{
									inBounded = true;

									int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
									int y_cord_remove = (int) (szWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight() ));

									if(removeImg.getLayoutParams().height == remove_img_height)
									{
										removeImg.getLayoutParams().height = (int) (remove_img_height * 1.5);
										removeImg.getLayoutParams().width = (int) (remove_img_width * 1.5);

										WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
										param_remove.x = x_cord_remove;
										param_remove.y = y_cord_remove;

										windowManager.updateViewLayout(removeView, param_remove);
									}

									layoutParams.x = x_cord_remove + (Math.abs(removeView.getWidth() - timerView.getWidth())) / 2;
									layoutParams.y = y_cord_remove + (Math.abs(removeView.getHeight() - timerView.getHeight())) / 2 ;

									windowManager.updateViewLayout(timerView, layoutParams);
									break;
								}

								else
								{
									inBounded = false;
									removeImg.getLayoutParams().height = remove_img_height;
									removeImg.getLayoutParams().width = remove_img_width;

									WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
									int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
									int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight() );

									param_remove.x = x_cord_remove;
									param_remove.y = y_cord_remove;

									windowManager.updateViewLayout(removeView, param_remove);
								}
							}

							layoutParams.x = x_cord_Destination;
							layoutParams.y = y_cord_Destination;

							windowManager.updateViewLayout(timerView, layoutParams);
							break;

						case MotionEvent.ACTION_UP:

							isLongClick = false;
							removeView.setVisibility(View.GONE);
							removeImg.getLayoutParams().height = remove_img_height;
							removeImg.getLayoutParams().width = remove_img_width;
							handler_longClick.removeCallbacks(runnable_longClick);

							if(inBounded)
							{
								stopService(new Intent(TimerService.this, TimerService.class));
								inBounded = false;
								break;
							}


							int x_diff = x_cord - x_init_cord;
							int y_diff = y_cord - y_init_cord;

							if(Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5)
							{
								time_end = System.currentTimeMillis();

								if((time_end - time_start) < 300)
								{
									timer_click();
								}
							}

							y_cord_Destination = y_init_margin + y_diff;

							int BarHeight =  getStatusBarHeight();

							if (y_cord_Destination < 0)
							{
								y_cord_Destination = 0;
							}

							else if (y_cord_Destination + (timerView.getHeight() + BarHeight) > szWindow.y)
							{
								y_cord_Destination = szWindow.y - (timerView.getHeight() + BarHeight );
							}

							layoutParams.y = y_cord_Destination;

							inBounded = false;
							resetPosition(x_cord);
							break;

						default:

							Log.d(Utils.LogTag, "timerView.setOnTouchListener  -> event.getAction() : default");
							break;
					}

					return true;
				}
			});
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);

		try
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			{
				windowManager.getDefaultDisplay().getSize(szWindow);
			}

			else
			{
				int w = windowManager.getDefaultDisplay().getWidth();
				int h = windowManager.getDefaultDisplay().getHeight();
				szWindow.set(w, h);
			}

			WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) timerView.getLayoutParams();

			if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
			{
				Log.d(Utils.LogTag, "TimerService.onConfigurationChanged -> landscape");

				if(layoutParams.y + (timerView.getHeight() + getStatusBarHeight()) > szWindow.y)
				{
					layoutParams.y = szWindow.y- (timerView.getHeight() + getStatusBarHeight());
					windowManager.updateViewLayout(timerView, layoutParams);
				}

				if(layoutParams.x != 0 && layoutParams.x < szWindow.x)
				{
					resetPosition(szWindow.x);
				}
			}

			else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
			{
				Log.d(Utils.LogTag, "TimerService.onConfigurationChanged -> portrait");

				if(layoutParams.x > szWindow.x)
				{
					resetPosition(szWindow.x);
				}
			}
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	private void resetPosition(int x_cord_now)
	{
		try
		{
			if(x_cord_now <= szWindow.x / 2)
			{
				moveToLeft(x_cord_now);
			}

			else
			{
				moveToRight(x_cord_now);
			}
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}

	}


	private void moveToLeft(final int x_cord_now)
	{
		try
		{
			final int x = szWindow.x - x_cord_now;

			new CountDownTimer(500, 5)
			{
				WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) timerView.getLayoutParams();
				public void onTick(long t)
				{
					long step = (500 - t)/5;
					mParams.x = 0 - (int)(double)bounceValue(step, x );
					windowManager.updateViewLayout(timerView, mParams);
				}

				public void onFinish()
				{
					mParams.x = 0;
					windowManager.updateViewLayout(timerView, mParams);
				}
			}.start();
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	private  void moveToRight(final int x_cord_now)
	{
		try
		{
			new CountDownTimer(500, 5)
			{
				WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) timerView.getLayoutParams();

				public void onTick(long t)
				{
					long step = (500 - t)/5;
					mParams.x = szWindow.x + (int)(double)bounceValue(step, x_cord_now) - timerView.getWidth();
					windowManager.updateViewLayout(timerView, mParams);
				}

				public void onFinish()
				{
					mParams.x = szWindow.x - timerView.getWidth();
					windowManager.updateViewLayout(timerView, mParams);
				}
			}.start();
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	private double bounceValue(long step, long scale)
	{
		double value = scale * Math.exp(-0.055 * step) * Math.cos(0.08 * step);
		return value;
	}


	private int getStatusBarHeight()
	{
		int statusBarHeight = (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
		return statusBarHeight;
	}


	private void timer_click()
	{
		try
		{
			Intent intent = new Intent(MyApplication.getInstance(), AppointmentSummeryActivity.class);
			intent.putExtra("ID", prefs.getInt(KEY_APPOINTMENT_ID, 0));
			startActivity(intent);
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}

		Log.wtf("TIMER_CLICK", "timer_click");
	}


	private void timer_long_click()
	{
		Log.d(Utils.LogTag, "Into TimerService.timer_long_click() ");

		try
		{
			WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeView.getLayoutParams();
			int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
			int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight() );

			param_remove.x = x_cord_remove;
			param_remove.y = y_cord_remove;

			windowManager.updateViewLayout(removeView, param_remove);
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i(Utils.LogTag, "TimerService.onStartCommand() -> startId=" + startId);

		try
		{
			if(intent != null)
			{
				Bundle bd = intent.getExtras();

				if(bd != null)
				{
					//bd.getString(Utils.EXTRA_MSG);
				}
			}

			/*if(startId == Service.START_STICKY)
			{
				handleStart();
				return super.onStartCommand(intent, flags, startId);
			}

			else
			{
				return START_NOT_STICKY;
			}*/

			handleStart();
			return START_STICKY;
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}

		return START_STICKY;
	}


	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.i(Utils.LogTag, "TimerService.onDestroy()");

		try
		{
			if(timerView != null)
			{
				windowManager.removeView(timerView);
			}

			if(removeView != null)
			{
				windowManager.removeView(removeView);
			}
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	@Override
	public IBinder onBind(Intent intent)
	{
		Log.i(Utils.LogTag, "TimerService.onBind()");
		return null;
	}


	@Override
	public void onTaskRemoved(Intent rootIntent)
	{
		Log.i(Utils.LogTag, "TimerService.onTaskRemoved()");
		super.onTaskRemoved(rootIntent);
	}
}