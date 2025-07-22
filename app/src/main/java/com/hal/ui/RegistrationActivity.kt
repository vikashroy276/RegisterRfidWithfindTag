package com.hal.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.hal.Base.BaseActivity
import com.hal.databinding.ActivityRegistrationBinding
import com.hal.room.AppDatabase
import com.hal.room.RegistrationItem
import kotlinx.coroutines.launch

class RegistrationActivity : BaseActivity() {
    lateinit var binding: ActivityRegistrationBinding
    var selectedItem: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        views()
        clicks()
        setupToolBar()
    }

    fun setupToolBar() {
        setSupportActionBar(binding.tool.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            title = "Registration"
            binding.tool.toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun onScannedData(data: String?) {

    }

    override fun handleSetText(msg: String?) {

    }

    fun handleTagData(tagData: String) {
        if (tagData.isNotEmpty()) {
            runOnUiThread {
                binding.etSerialN0.setText(tagData)
                Toast.makeText(this, "TagData->$tagData", Toast.LENGTH_SHORT).show()
                Log.d("mmmmmm", "handleTagData: $tagData")
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "Please scan serial no again", Toast.LENGTH_SHORT).show()
            }
        }
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
            val serialNoInput = binding.etSerialN0.text.toString().trim()
            val existingItem = db.inventoryDao().getItemBySerialNo(serialNoInput)

            if (existingItem != null) {
                showErrorDialog("Tag already registered! ")
            } else {
                val newItem = RegistrationItem(
                    serialNo = serialNoInput, binNo = selectedItem.toString()
                )
                db.inventoryDao().insertItem(newItem)

                val message =
                    "Registration Successful!\n\nSerial No: ${newItem.serialNo}\nBin No: ${newItem.binNo}"
                showRegistrationDialog(message)
            }
        }
    }

    private fun showRegistrationDialog(message: String) {
        AlertDialog.Builder(this).setTitle("Success").setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                binding.etSerialN0.text?.clear()
                binding.spinnerSelectBin.setSelection(0)
                selectedItem = null
            }.setCancelable(false).show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this).setTitle("Error").setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                binding.etSerialN0.text?.clear()
                binding.spinnerSelectBin.setSelection(0)
                selectedItem = null
            }.show()
    }

    private fun validateData(): Boolean {
        if (binding.etSerialN0.text.toString().isEmpty()) {
            Toast.makeText(this, "Please enter serial no", Toast.LENGTH_SHORT).show()
            return false

        } else if (binding.spinnerSelectBin.selectedItem == "Select Option") {
            Toast.makeText(this, "Please enter serial no", Toast.LENGTH_SHORT).show()
            return false

        }
        return true

    }

    private fun views() {
        val items =
            listOf("Select Option", "BIN00001", "BIN00002", "BIN00003", "BIN00004", "BIN00005")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSelectBin.adapter = adapter
        binding.spinnerSelectBin.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    selectedItem = items[position]
                    if (position != 0) { // Skip "Select Option"
                        Toast.makeText(
                            this@RegistrationActivity, "Selected: $selectedItem", Toast.LENGTH_SHORT
                        ).show()
                        when (selectedItem) {
                            "Registration" -> {

                            }

                            "Tag Mapping" -> {

                            }

                            "Search" -> {

                            }
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Optional
                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}