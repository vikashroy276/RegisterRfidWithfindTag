package com.hal.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.hal.Base.BaseActivity
import com.hal.databinding.ActivityIssuanceBinding
import com.hal.room.AppDatabase
import com.hal.room.IssuanceItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IssuanceActivity : BaseActivity() {
    lateinit var binding: ActivityIssuanceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIssuanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        views()
        clicks()
        setupToolBar()
    }

    override fun onScannedData(data: String?) {

    }

    override fun handleSetText(msg: String?) {

    }

    fun setupToolBar() {
        setSupportActionBar(binding.tool.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            title = "Issuance"
            binding.tool.toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    fun handleTagData(tagData: String) {
        if (tagData.isNotEmpty()) {
            binding.etSerialN0.setText(tagData)
            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(applicationContext)
                val item = db.inventoryDao().getItemBySerialNo(tagData)

                withContext(Dispatchers.Main) {
                    if (item != null && item.binNo.isNotEmpty()) {
                        binding.binDetails.setText(item.binNo)
                    } else {
                        binding.binDetails.setText("")
                    }
                }
            }

            Log.d("TAG", "handleTagData: $tagData")
        } else {
            Toast.makeText(this, "Please scan serial no again", Toast.LENGTH_SHORT).show()
        }
    }


    private fun views() {

    }

    private fun clicks() {
        binding.btnSubmit.setOnClickListener {
            if (validateData()) {
                saveDataToLocal()

            }
        }

    }

    private fun saveDataToLocal() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val newItem = IssuanceItem(
                serialNo = binding.etSerialN0.text.toString(),
                userName = binding.etUserName.text.toString()
            )

            db.inventoryDao().insertIssuanceUser(newItem)

            // Show success message on UI thread
            withContext(Dispatchers.Main) {
                showRegistrationDialog("Data saved successfully!")
            }
        }
    }

    private fun showRegistrationDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Success")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                binding.etSerialN0.text?.clear()
                binding.etUserName.text?.clear()
                binding.binDetails.text?.clear()
            }
            .setCancelable(false)
            .show()
    }


    private fun validateData(): Boolean {
        if (binding.etSerialN0.text.toString().isEmpty()) {
            Toast.makeText(this, "Please enter serial no", Toast.LENGTH_SHORT).show()
            return false

        } else if (binding.etUserName.text.toString().isEmpty()) {
            Toast.makeText(this, "Please enter user name", Toast.LENGTH_SHORT).show()
            return false
        }
        return true

    }
}