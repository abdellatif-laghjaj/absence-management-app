package com.example.absencemanagementapp.helpers

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import dev.shreyaspatil.MaterialDialog.MaterialDialog

class Helper {
    companion object {
        fun checkInternetConnection(
            activity: Activity,
            appCompatActivity: AppCompatActivity
        ) {
            if (!isConnected(appCompatActivity)) {
                MaterialDialog.Builder(activity)
                    .setTitle("No Internet Connection")
                    .setMessage("Please check your internet connection and try again")
                    .setCancelable(false)

                    .setNegativeButton("Exit") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        activity.finish()
                    }
                    .setPositiveButton("Ok") { _, _ ->
                        checkInternetConnection(activity, appCompatActivity)
                    }
                    .build().show()
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
    }
}