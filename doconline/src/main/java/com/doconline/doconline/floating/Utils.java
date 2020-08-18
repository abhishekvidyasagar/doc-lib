package com.doconline.doconline.floating;


import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class Utils
{
	static String LogTag = "timer_countdown";
	static String EXTRA_MSG = "extra_msg";

	public static boolean canDrawOverlays(Context context)
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
		{
			return true;
		}

		else
		{
			return Settings.canDrawOverlays(context);
		}
	}
}
