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
import com.hal.databinding.ActivityBiningBinding
import com.hal.room.AppDatabase
import com.hal.room.BinItem
import kotlinx.coroutines.launch

class BinningActivity : BaseActivity() {
    private lateinit var binding: ActivityBiningBinding
    var selectedItem: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBiningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolBar()
        views()
        clicks()
    }

    override fun onScannedData(data: String?) {

    }

    fun setupToolBar() {
        setSupportActionBar(binding.tool.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            title = "Binning"
            binding.tool.toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun views() {
        val items = listOf("Select Option", "QRCode", "Barcode", "RFID", )
        val adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, items)
        adapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
        binding.spinnerSelectBin.adapter = adapter
        binding.spinnerSelectBin.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    selectedItem = items[position]
                    if (position != 0) { // Skip "Select Option"
                        Toast.makeText(
                            this@BinningActivity, "Selected: $selectedItem", Toast.LENGTH_SHORT
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

    override fun handleSetText(msg: String?) {

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

            val newItem = BinItem(
                serialNo = serialNoInput,
                binItem = selectedItem.toString()
            )

            db.inventoryDao().insertBinData(newItem)

            val message =
                "Mapping Successful!\n\nSerial No: ${newItem.serialNo}"
            showRegistrationDialog(message)
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
}