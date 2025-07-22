package com.hal.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hal.BuildConfig
import com.hal.MainActivity
import com.hal.R
import com.hal.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    private val bluetoothPermissions: Array<String> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.BLUETOOTH_ADVERTISE
            ) // No location permission needed in Android 13+
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.BLUETOOTH_ADVERTISE,
                android.Manifest.permission.ACCESS_FINE_LOCATION // Required in Android 12
            )
        } else {
            emptyArray() // No runtime permissions needed below Android 12
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkBluetoothPermissions()
        clicks()

        val deviceId =  Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        binding.deviceId.text = "Device ID: $deviceId"

        val versionName = BuildConfig.VERSION_NAME
        binding.footer.version.text = "$versionName"

    }


    private fun clicks() {
        binding.btnLogin.setOnClickListener {
//            val username = binding.etUsername.text.toString().trim()
//            val password = binding.etPassword.text.toString().trim()

//            if (username == "admin" && password == "1234") {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
//            } else {
//                showErrorDialog("Invalid credentials. Please try again.")
//            }
        }
    }

    private fun showErrorDialog(message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Login Failed")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }


    //code to check bluetooth permissions
    private fun checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            if (bluetoothPermissions.any {
                    ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
                }) {
                ActivityCompat.requestPermissions(this, bluetoothPermissions, 1001)
            }
        }
    }
}