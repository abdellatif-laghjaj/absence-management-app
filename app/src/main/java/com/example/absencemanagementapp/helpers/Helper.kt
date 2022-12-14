package com.example.absencemanagementapp.helpers

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.finishAffinity
import com.airbnb.lottie.LottieAnimationView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.models.Seance
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import java.util.*

class Helper {
    companion object {
        fun checkInternetConnection(
            activity: Activity, appCompatActivity: AppCompatActivity
        ) {
            if (!isConnected(appCompatActivity)) {
                val dialog_no_internet =
                    MaterialDialog.Builder(activity)
                        .setTitle(Resources.getSystem().getString(R.string.no_internet_connection))
                        .setAnimation(R.raw.no_internet)
                        .setMessage(
                            Resources.getSystem().getString(R.string.please_check_your_connection)
                        )
                        .setCancelable(false)

                        .setNegativeButton(
                            Resources.getSystem().getString(R.string.exit)
                        ) { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            activity.finish()
                        }.setPositiveButton(Resources.getSystem().getString(R.string.ok)) { _, _ ->
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

        fun showExitDialog(context: Activity) {
            val dialog = MaterialDialog.Builder(context).setTitle(context.getString(R.string.exit))
                .setMessage(context.getString(R.string.exit_dialog_message))
                .setCancelable(false)
                .setAnimation(R.raw.logout)
                .setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                    finishAffinity(context)
                }.setNegativeButton(context.getString(R.string.no)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }.build()
            dialog.show()

            val animationView: LottieAnimationView = dialog.animationView

            //scale animation
            animationView.scaleX = 0.5f
            animationView.scaleY = 0.5f
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

        //change language
        fun changeLanguage(language: String, activity: AppCompatActivity) {
            when (language) {
                "en" -> activity.resources.configuration.setLocale(Locale("en"))
                "fr" -> activity.resources.configuration.setLocale(Locale("fr"))
                "ar" -> activity.resources.configuration.setLocale(Locale("ar"))
            }

            //save language to shared preferences
            saveLanguage(language, activity)
        }

        //save language to shared preferences
        fun saveLanguage(language: String, activity: AppCompatActivity) {
            val sharedPreferences = activity.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("language", language)
            editor.apply()
        }

        //get language from shared preferences
        fun loadLanguage(activity: AppCompatActivity): String {
            val sharedPreferences = activity.getSharedPreferences("settings", Context.MODE_PRIVATE)
            return sharedPreferences.getString("language", "en").toString()
        }

        //format seance id
        fun formatSeanceId(seance: Seance): String {
            val module = seance.n_module
            val date = seance.date.toString().replace("/", "-")
            val start_time = seance.start_time.toString().replace(":", "-")

            return "$module-$date-$start_time"
        }

        fun formatStudentName(first_name: String, last_name: String): String {
            return first_name.toCharArray()[0].toUpperCase() + first_name.substring(1).toLowerCase() + " " + last_name.toLowerCase()
        }

        fun shorten(string: String, length: Int): String {
            return if (string.length > length) {
                string.substring(0, length - 3) + "..."
            } else {
                string
            }
        }
    }
}
