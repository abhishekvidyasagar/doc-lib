package com.doconline.doconline.alert;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.doconline.doconline.R;
import com.doconline.doconline.app.MyApplication;
import com.doconline.doconline.helper.OnDialogAction;
import com.google.android.material.snackbar.Snackbar;

import static com.doconline.doconline.app.Constants.PLAYSTORE_URL;


public class CustomAlertDialog 
{
	public static final int DIALOG_REQUEST_CODE_LOGOUT = 101;
	public static final int DIALOG_REQUEST_CODE_SET_PASSWORD = 102;
	public static final int DIALOG_REQUEST_CODE_OVERLAY_PERMISSION = 103;
	public static final int DIALOG_REQUEST_CODE_INTERNET = 104;
	public static final int DIALOG_REQUEST_CODE_CONFIRM_PASSWORD = 105;

	public static final int DIALOG_REQUEST_CODE_DELETE_MEDICATION = 400;
	public static final int DIALOG_REQUEST_CODE_DELETE_ALLERGY = 401;
	public static final int DIALOG_REQUEST_CODE_DELETE_FAMILY_MEMBER = 402;
	public static final int DIALOG_REQUEST_CODE_UPDATE_CONSENT = 403;
	public static final int DIALOG_REQUEST_CODE_DELETE_VITAL = 404;
	public static final int DIALOG_REQUEST_CODE_DELETE_EHR = 405;

	public static final int DIALOG_REQUEST_CODE_CHAT_CONFIRMATION = 200;
	public static final int DIALOG_REQUEST_CODE_PROCURE_MEDICINE = 201;
	public static final int DIALOG_REQUEST_CODE_REMOVE_MEDICINE_AND_CONTINUE = 202;

	public static final int DIALOG_REQUEST_CODE_MOBILE_VERIFICATION = 300;
	public static final int DIALOG_REQUEST_CODE_SUBSCRIPTION_REQUIRE = 301;
	public static final int DIALOG_REQUEST_CODE_UPGRADE_SUBSCRIPTION = 302;
	public static final int DIALOG_REQUEST_CODE_EMAIL_VERIFICATION = 303;

	public static final int DIALOG_REQUEST_CODE_SLOT_REQUIRE = 500;
	public static final int DIALOG_REQUEST_CODE_CANCEL_APPOINTMENT = 501;
	public static final int DIALOG_REQUEST_CODE_CANCEL_SUBSCRIPTION = 502;
	public static final int DIALOG_REQUEST_CODE_BOOKING_FAIL = 503;
	public static final int DIALOG_REQUEST_CODE_RETRY_PAYMENT = 504;
	public static final int DIALOG_REQUEST_CODE_FILE_UPLOAD_FAIL = 505;
	public static final int DIALOG_REQUEST_CODE_DELETE_ATTACHMENTS = 506;
	public static final int DIALOG_REQUEST_CODE_DOB_REQUIRE = 507;
	public static final int DIALOG_REQUEST_CODE_DISCARD_FILE = 508;

	public static final int LENGTH_SHORT = Snackbar.LENGTH_SHORT;
	public static final int LENGTH_LONG = Snackbar.LENGTH_LONG;
	public static final int LENGTH_INDEFINITE = Snackbar.LENGTH_INDEFINITE;

	public static final int DIALOG_REQUEST_CODE_DIAGNOSTICS_DELETE_ADDRESS = 700;
	public static final int DIALOG_REQUEST_CODE_DIAGNOSTICS_SAVE_NEW_ADDRESS = 701;
	public static final int DIALOG_REQUEST_CODE_DIAGNOSTICS_UPDATE_ADDRESS = 702;
	public static final int DIALOG_REQUEST_CODE_DIAGNOSTICS_SET_DEFAULT_ADDRESS = 703;

	public static final int DIALOG_REQUEST_CODE_DIAGNOSTICS_DELETE_CART_ITEM = 7000;
	public static final int DIALOG_REQUEST_CODE_DIAGNOSTICS_ADD_CART_ITEM = 7001;
	public static final int DIALOG_REQUEST_CODE_DIAGNOSTICS_BOOK_APPOINTMENT_ERROR = 70002;

	public static final int DIALOG_REQUEST_CODE_DIAGNOSTICS_PAYMENT_ERROR = 70003;

	private Context context;
	private int requestCode;
	private OnDialogAction listener;
	private View view;
	

	public CustomAlertDialog(Context context)
	{
		this.context = context;
	}


	public CustomAlertDialog(Context context, OnDialogAction listener)
	{
		this.listener = listener;
		this.context = context;
	}

	public CustomAlertDialog(Context context, View view) {
		this.context = context;
		this.view = view;
	}

	public CustomAlertDialog(Context context, int requestCode, OnDialogAction listener)
	{
		this.listener = listener;
		this.context = context;
		this.requestCode = requestCode;
	}

	public CustomAlertDialog(Context context, OnDialogAction listener, View view)
	{
		this.listener = listener;
		this.context = context;
		this.view = view;
	}


	public void showUpdateDialog()
	{
		new MaterialDialog.Builder(context)
				.title(context.getResources().getString(R.string.dialog_title_update_available))
				.content(context.getResources().getString(R.string.dialog_content_update_available))
				.positiveText(context.getResources().getString(R.string.Update))
				.negativeText(context.getResources().getString(R.string.NotNow))
				.icon(context.getResources().getDrawable(R.mipmap.ic_launcher))
				.cancelable(true)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
					{
						context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAYSTORE_URL)));
					}
				})
				.onNegative(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
					{
						dialog.cancel();
					}
				})
				.show();
	}


	public void showNetworkAlertDialog()
	{
		new MaterialDialog.Builder(context)
				.title("Network")
				.content("Internet not connected. Please connect to internet and try again.")
				.positiveText(context.getResources().getString(R.string.Settings))
				.cancelable(false)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
					{
						listener.onPositiveAction(requestCode);

						Intent i = new Intent(Settings.ACTION_SETTINGS);
						context.startActivity(i);
					}
				})
				.show();
	}


	public void showDialogWithActionAndIcon(String title, String content, String pos_action, String neg_action, boolean cancelable, int icon)
	{
		new MaterialDialog.Builder(context)
				.title(title)
				.content(content)
				.positiveText(pos_action)
				.negativeText(neg_action)
				.icon(context.getResources().getDrawable(icon))
				.cancelable(true)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
					{
						listener.onPositiveAction(requestCode);
					}
				})
				.onNegative(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
					{
						listener.onNegativeAction(requestCode);
						dialog.cancel();
					}
				})
				.show();
	}


	public void showPasswordAlertDialog()
	{
		new MaterialDialog.Builder(context)
				.title("Enter Password")
				.inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
				.cancelable(false)
				.positiveText(context.getResources().getString(R.string.Submit))
				.negativeText(context.getResources().getString(R.string.Cancel))
				.input("Password", "", new MaterialDialog.InputCallback() {

					@Override
					public void onInput(@NonNull MaterialDialog dialog, CharSequence input)
					{
						listener.onPositiveAction(requestCode, input.toString());
					}
				}).show();
	}


	public void unauthorized(String title, String content, String pos_action, boolean cancelable)
	{
		new MaterialDialog.Builder(context)
				.title(title)
				.content(content)
				.positiveText(pos_action)
				.cancelable(cancelable)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
					{
						MyApplication.getInstance().getSession().logoutUser();
						dialog.cancel();
					}
				})
				.show();
	}


	public void showDialogWithAction(String title, String content, String pos_action, String neg_action, boolean cancelable)
	{
		new MaterialDialog.Builder(context)
				.title(title)
				.content(content)
				.positiveText(pos_action)
				.negativeText(neg_action)
				.cancelable(cancelable)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
					{
						listener.onPositiveAction(requestCode);
					}
				})
				.onNegative(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
					{
						listener.onNegativeAction(requestCode);
					}
				})
				.show();
	}


	public void showAlertDialogWithPositiveAction(String title, String content, String pos_action, boolean cancelable)
	{
		new MaterialDialog.Builder(context)
				.title(title)
				.content(content)
				.positiveText(pos_action)
				.cancelable(cancelable)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
					{
						listener.onPositiveAction(requestCode);
					}
				})
				.show();
	}

	public void showAlertDialogWithoutTitle(String content, String pos_action, boolean cancelable)
	{
		new MaterialDialog.Builder(context)
				.content(content)
				.positiveText(pos_action)
				.cancelable(cancelable)
				.onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
					{
						listener.onPositiveAction(requestCode);
					}
				})
				.show();
	}

	public void showSnackbarWithAction(final String message, final int duration)
	{
		Snackbar bar = Snackbar.make(view, message, duration)
				.setAction("DISMISS", new View.OnClickListener() {

					@Override
					public void onClick(View v)
					{
						listener.onPositiveAction();
					}
				});

		bar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));

		TextView textView = bar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
		textView.setTextColor(Color.WHITE);

		bar.setActionTextColor(Color.WHITE);

		bar.show();
	}


	public void showSnackbar(final String message, final int duration)
	{
		Snackbar bar = Snackbar.make(view, message, duration);
		bar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));

		TextView textView = bar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
		textView.setTextColor(Color.WHITE);

		bar.show();
	}


	public void snackbarForInternetConnectivity()
	{
		Snackbar bar = Snackbar.make(view, context.getResources().getString(R.string.error_internet_connectivity), Snackbar.LENGTH_LONG)
				.setAction("SETTINGS", new View.OnClickListener() {

					@Override
					public void onClick(View v)
					{
						Intent i = new Intent(Settings.ACTION_SETTINGS);
						context.startActivity(i);
					}
				});

		bar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));

		TextView textView = bar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
		textView.setTextColor(Color.WHITE);

		bar.setActionTextColor(Color.WHITE);

		bar.show();
	}

	public ProgressDialog showLoadingAlertDialog(Context context, int color, String message) {
		ProgressDialog progress=new ProgressDialog(context);
		progress.setMessage(message);
		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progress.setIndeterminate(true);
		progress.setProgress(0);
		return progress;
	}
}