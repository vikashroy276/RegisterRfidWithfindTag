package com.hal.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.hal.Base.BaseActivity
import com.hal.Base.RFIDHandler
import com.hal.databinding.ActivityLocatorBinding
import com.hal.room.AppDatabase
import com.hal.room.RegistrationItem
import com.hal.utils.RangeGraph
import com.zebra.rfid.api3.InvalidUsageException
import com.zebra.rfid.api3.OperationFailureException
import com.zebra.rfid.api3.TagData
import kotlinx.coroutines.launch

class LocatorActivity : BaseActivity(), RFIDHandler.ResponseHandlerInterface {
    private lateinit var binding: ActivityLocatorBinding
    private var ctx: Context? = null
    private var tagEPC: String? = null
    private var locationBar: RangeGraph? = null
    var selectedItem: String? = null
    var items: List<RegistrationItem> = listOf()
    private val detectedTags = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ctx = this
        setupToolBar()
        saveDataToLocal()
        tagEPC = intent.getStringExtra("epc")
        println("epc Id : $tagEPC")

        locationBar = binding.locationBar
        epc = tagEPC
        //binding.tagEPC.text = tagEPC

    }


    override fun onResume() {
        super.onResume()
        rfidHandler = RFIDHandler.getInstance(30, this) // 30-second timeout
        rfidHandler.setPower(rfidHandler.initialPower * 10)
        if (rfidHandler != null) {
            rfidHandler.setHandler(this)
        } else {
            Log.e(TAG, "RFIDHandler instance is null!")
            handleSetText("RFID Initialization Failed")
        }
    }


    fun setupToolBar() {
        setSupportActionBar(binding.tool.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            title = "Find Tag"
            binding.tool.toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun handleTagData(tagData: Array<TagData>) {
        for (tag in tagData) {
            val percent = tag.LocationInfo.relativeDistance.toInt()
            runOnUiThread {
                locationBar?.value = percent
                locationBar?.invalidate()
            }
        }
    }

    override fun handleTriggerPress(pressed: Boolean) {
        if (pressed) {
            rfidHandler?.locate(epc) //reads and shows inventory
        } else {
            rfidHandler?.stopInventory() //on release stops showing any new inventory
        }
    }

    override fun handleSetText(msg: String) {

    }

    override fun showStatus(): Boolean {
        return false
    }

    override fun onScannedData(data: String?) {
    }

    private fun saveDataToLocal() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            items = db.inventoryDao().getAllItems()
            setupSpinner()
        }
    }


    private fun setupSpinner() {
        if (items.isEmpty()) {
            return
        }

        // Add a default unselectable item at the top
        val modifiedItems = listOf(
            RegistrationItem(serialNo = "Select Tag", binNo = "")
        ) + items

        val adapter = object : ArrayAdapter<RegistrationItem>(
            this,
            android.R.layout.simple_spinner_item,
            modifiedItems
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0 // Disable first item
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as? TextView
                textView?.text = modifiedItems[position].serialNo
                textView?.setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                return view
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as? TextView)?.text = modifiedItems[position].serialNo
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSelectBin.adapter = adapter

        // Set default selection to the first item ("Select Tag")
        binding.spinnerSelectBin.setSelection(0)

        binding.spinnerSelectBin.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    if (position != 0) {
                        selectedItem = modifiedItems[position].serialNo
                        epc = selectedItem
                        handleTagFound(selectedItem!!)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }


    fun handleTagFound(tagNo: String) {
        if (tagNo !in detectedTags) {
            detectedTags.add(tagNo)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (rfidHandler != null) {
            try {
                rfidHandler.stopInventory()
                rfidHandler.stopLocate()
                rfidHandler.setHandler(null)
            } catch (e: OperationFailureException) {
                Log.e(TAG, "Error during RFIDHandler cleanup: " + e.message, e)
            } catch (e: InvalidUsageException) {
                Log.e(TAG, "Error during RFIDHandler cleanup: " + e.message, e)
            }
        }
    }

    companion object {
        const val TAG: String = "LocatorActivity"
        var epc: String? = null
        var name: String? = null
    }
}
