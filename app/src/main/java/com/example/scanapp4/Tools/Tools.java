package com.example.scanapp4.Tools;

import static android.view.View.INVISIBLE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.scanapp4.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.tapadoo.alerter.Alerter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Tools {
    public static boolean containsOnlyDigits(String s) {
        return s.matches("\\d+");
    }
    public static void showAlertDialog (Context context,String title, String message, String positiveButtonText, DialogInterface.OnClickListener positiveListener, String negativeButtonText, DialogInterface.OnClickListener negativeListener){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(positiveButtonText, positiveListener)

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(negativeButtonText, negativeListener)
                .setIcon(R.drawable.ic_warning_alert)
                .show();
    }

    public static void warningDialog(Context context,String message){
        new CustomDialog(context,message, R.drawable.ic_warning_alert).show();
    }
    public static void successDialog(Context context,String message){
        new CustomDialog(context,message, R.drawable.ic_done).show();
    }
    public static void  back(Fragment fragment){
        NavController navController = NavHostFragment.findNavController(fragment);
        navController.popBackStack();
    }
    public static void  clearInputLayoutError(TextInputLayout ... inputLayouts){
        for (TextInputLayout inputLayout:inputLayouts) {
            inputLayout.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    inputLayout.setError(null);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    inputLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    inputLayout.setError(null);
                }
            });
        }
    }
    public static void  attachButtonsToListener(View.OnClickListener listener, MaterialButton... materialButtons){
        for (MaterialButton button:materialButtons){
            button.setOnClickListener(listener);
        }
    }

    public static String getTextFromInputLayout(TextInputLayout textInputLayout) {
        return textInputLayout.getEditText().getText().toString().trim();
    }
    public static void hideKeyboard(Activity activity) {
        if (activity!=null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager)   activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
    public static void activateItem(View itemView) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.9f,1.0f);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(50);//duration in millisecond
        itemView.startAnimation(alphaAnimation);
    }

    public static void deactivateItem(View itemView) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.9f,0.4f);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(50);//duration in millisecond
        itemView.startAnimation(alphaAnimation);
    }

    public static long getRemainingTime(String expectedSignOut) {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d = null;
        try {
            d = sdf.parse(expectedSignOut);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return d.getTime() - currentDate.getTime();
    }

    public static void startRemainingTimeTimer(long remainingTime, TextView remainingTimeTv){
        if (remainingTime>0) {
            new CountDownTimer(remainingTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    remainingTimeTv.setText(convertToTimeFormat(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    remainingTimeTv.setText("Operation Finished");
                }
            }.start();
        } else
            remainingTimeTv.setText("Operation Finished");
    }

    public static String convertToTimeFormat(long millisUntilFinished) {
        DateFormat formatter = new SimpleDateFormat("hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millisUntilFinished);
        return formatter.format(millisUntilFinished);
    }
    public static String getEditTextText(TextInputLayout editText){
        return editText.getEditText().getText().toString().trim();
    }

    public static void showSuccessAlerter(String message,Activity activity){
       Alerter.create(activity).setText(message)
                .setIcon(R.drawable.ic_done)
                .setBackgroundColorInt(activity.getResources().getColor(R.color.alerter_success_color))
                .setDuration(1000)
                .setTextAppearance(R.style.alerter_text_appearance)
                .setEnterAnimation(com.tapadoo.alerter.R.anim.alerter_slide_in_from_top)
                .setExitAnimation(com.tapadoo.alerter.R.anim.alerter_slide_out_to_top)
                .show();
    }


    public static Context getContext (Application application){
        return application.getApplicationContext();
    }
    void showLogOutButton(Activity activity){
        MaterialButton logOutButton = activity.findViewById(R.id.logout);
                logOutButton.setVisibility(View.VISIBLE);
    }
    void hideLogOutButton( Activity activity){
        MaterialButton logOutButton = activity.findViewById(R.id.logout);
        logOutButton.setVisibility(INVISIBLE);
    }

    public static void showBackButton(Activity activity){
        MaterialButton back = activity.findViewById(R.id.back_arrow);
                back.setVisibility(View.VISIBLE);
    }
    public static void hideBackButton( Activity activity){
        MaterialButton back = activity.findViewById(R.id.back_arrow);
                back.setVisibility(INVISIBLE);
    }

    public static void changeFragmentTitle(String title ,  Activity activity){
        TextView titleTextView = activity.findViewById(R.id.title);
                titleTextView.setText(title);
    }
    public static String getTodayDate (){
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh.mm.ss");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(new Date(calendar.getTimeInMillis()));
//        String day,monthText;
//        int month = calendar.get(Calendar.MONTH)+1;
//        if (calendar.get(Calendar.DAY_OF_MONTH)<10)
//            day = "0"+calendar.get(Calendar.DAY_OF_MONTH);
//         else day = ""+calendar.get(Calendar.DAY_OF_MONTH);
//        if (calendar.get(Calendar.MONTH)+1<10)
//            monthText = "0"+month;
//        else monthText = ""+month;
//        return day+"-"+monthText+"-"+calendar.get(Calendar.YEAR);
    }

    public static String formatDate (String date){
        return date.substring(0,4)+""+date.substring(5,7)+""+date.substring(8,10);
    }
}
