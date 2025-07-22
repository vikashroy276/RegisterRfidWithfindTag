package com.hal.ui

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.hal.Base.BaseActivity
import com.hal.databinding.ActivitySearchBinding
import com.hal.room.AppDatabase
import com.hal.room.RegistrationItem
import kotlinx.coroutines.launch
import kotlin.math.pow

class SearchActivity : BaseActivity() {
    lateinit var binding: ActivitySearchBinding
    var selectedItem: String? = null
    var items: List<RegistrationItem> = listOf() // Initialized with empty list
    private val detectedTags = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clicks()
        saveDataToLocal()
    }

    override fun onScannedData(data: String?) {

    }

    override fun handleSetText(msg: String?) {

    }

    private fun saveDataToLocal() {


        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            items = db.inventoryDao().getAllItems()
            setupSpinner() // Now that we have data, set up the spinner
        }
    }

    private fun setupSpinner() {
        detectedTags.add(items[0].serialNo)
        val adapter = object : ArrayAdapter<RegistrationItem>(
            this,
            android.R.layout.simple_spinner_item,
            items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as? TextView)?.text = items[position].serialNo
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as? TextView)?.text = items[position].serialNo
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSelectBin.adapter = adapter

        binding.spinnerSelectBin.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    selectedItem = items[position].serialNo

                    handleTagFound(selectedItem.toString())

                    Toast.makeText(
                        this@SearchActivity,
                        "Selected: $selectedItem",
                        Toast.LENGTH_SHORT
                    ).show()

                    when (selectedItem) {
                        "Registration" -> { /* Handle logic */ }
                        "Tag Mapping" -> { /* Handle logic */ }
                        "Search" -> { /* Handle logic */ }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }


    private fun clicks() {
        // Your click listeners
    }

    fun estimateDistance(rssi: Int): Double {
        val txPower = -59
        return 10.0.pow((txPower - rssi) / (10 * 2.0))
    }

    fun handleTagFound(tagNo: String) {
        if (tagNo !in detectedTags) {
            detectedTags.add(tagNo)
            Toast.makeText(this, "Tag found: $tagNo", Toast.LENGTH_SHORT).show()
            playBeepSound()
        }
    }

    fun playBeepSound() {
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGen.startTone(ToneGenerator.TONE_CDMA_PIP, 200)
    }
}
