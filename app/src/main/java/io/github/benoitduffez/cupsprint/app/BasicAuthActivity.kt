package io.github.benoitduffez.cupsprint.app

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

import io.github.benoitduffez.cupsprint.R

/**
 * Ask for the HTTP basic auth credentials
 */
class BasicAuthActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basic_auth)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val userName = findViewById<View>(R.id.basic_auth_login) as EditText
        val password = findViewById<View>(R.id.basic_auth_password) as EditText

        val printersUrl = intent.getStringExtra(KEY_BASIC_AUTH_PRINTERS_URL)

        val prefs = getSharedPreferences(CREDENTIALS_FILE, Context.MODE_PRIVATE)

        val numCredentials = prefs.getInt(KEY_BASIC_AUTH_NUMBER, 0)
        val foundId = findSavedCredentialsId(printersUrl, prefs)
        val targetId: Int
        if (foundId >= 0) {
            targetId = foundId
            userName.setText(prefs.getString(KEY_BASIC_AUTH_LOGIN + foundId, ""))
            password.setText(prefs.getString(KEY_BASIC_AUTH_PASSWORD + foundId, ""))
        } else {
            targetId = numCredentials
        }

        findViewById<View>(R.id.basic_auth_button).setOnClickListener {
            val editPrefs = getSharedPreferences(CREDENTIALS_FILE, Context.MODE_PRIVATE).edit()
            editPrefs.putString(KEY_BASIC_AUTH_LOGIN + targetId, userName.text.toString())
            editPrefs.putString(KEY_BASIC_AUTH_PASSWORD + targetId, password.text.toString())
            editPrefs.putString(KEY_BASIC_AUTH_PRINTERS_URL + targetId, printersUrl)
            editPrefs.putInt(KEY_BASIC_AUTH_NUMBER, numCredentials + 1)
            editPrefs.apply()

            finish()
        }
    }

    companion object {
        val CREDENTIALS_FILE = "basic_auth"

        val KEY_BASIC_AUTH_PRINTERS_URL = BasicAuthActivity::class.java!!.getName() + ".PrinterUrl"

        val KEY_BASIC_AUTH_LOGIN = BasicAuthActivity::class.java!!.getName() + ".Login"

        val KEY_BASIC_AUTH_PASSWORD = BasicAuthActivity::class.java!!.getName() + ".Password"

        internal val KEY_BASIC_AUTH_NUMBER = BasicAuthActivity::class.java!!.getName() + ".Number"

        /**
         * See if we have already saved credentials for this server
         *
         * @param fullUrl Server URL (may include the printer name)
         * @param prefs   Shared preferences to search credentials from
         * @return The credentials position in the preferences files, or -1 if it wasn't found
         */
        fun findSavedCredentialsId(fullUrl: String, prefs: SharedPreferences): Int {
            for (i in 0 until prefs.getInt(KEY_BASIC_AUTH_NUMBER, 0)) {
                val url = prefs.getString(KEY_BASIC_AUTH_PRINTERS_URL + i, null)
                if (url != null && fullUrl.startsWith(url)) {
                    return i
                }
            }
            return -1
        }
    }
}