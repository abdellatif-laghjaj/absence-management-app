package com.example.absencemanagementapp.helpers

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.airbnb.lottie.LottieAnimationView
import com.example.absencemanagementapp.R
import dev.shreyaspatil.MaterialDialog.MaterialDialog

class Helper {
    companion object {
        fun checkInternetConnection(
            activity: Activity, appCompatActivity: AppCompatActivity
        ) {
            if (!isConnected(appCompatActivity)) {
                val dialog_no_internet =
                    MaterialDialog.Builder(activity).setTitle("No Internet Connection")
                        .setAnimation(R.raw.no_internet)
                        .setMessage("Please check your internet connection and try again")
                        .setCancelable(false)

                        .setNegativeButton("Exit") { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            activity.finish()
                        }.setPositiveButton("Ok") { _, _ ->
                            checkInternetConnection(activity, appCompatActivity)
                        }.build()
                dialog_no_internet.show()

                val animationView: LottieAnimationView = dialog_no_internet.getAnimationView()

                //scale animation view
                animationView.scaleX = 0.5f
                animationView.scaleY = 0.5f
            } else {
                //close the dialog if the user is connected to the internet
                activity.finish()
                appCompatActivity.startActivity(activity.intent)
            }
        }

        fun isConnected(activity: AppCompatActivity): Boolean {
            val connectivityManager =
                activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

        //change theme
        fun changeTheme(theme: String, activity: AppCompatActivity) {
            when (theme) {
                "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }

            //save theme to shared preferences
            saveTheme(theme, activity)
        }


        //save theme to shared preferences
        fun saveTheme(theme: String, activity: AppCompatActivity) {
            val sharedPreferences = activity.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("theme", theme)
            editor.apply()
        }

        //get theme from shared preferences
        fun lodaTheme(activity: AppCompatActivity): String {
            val sharedPreferences = activity.getSharedPreferences("settings", Context.MODE_PRIVATE)
            return sharedPreferences.getString("theme", "system").toString()
        }
    }
}