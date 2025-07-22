package com.hal

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.hal.Base.BaseActivity
import com.hal.databinding.ActivityMainBinding
import com.hal.ui.BinningActivity
import com.hal.ui.IssuanceActivity
import com.hal.ui.LocatorActivity
import com.hal.ui.RegistrationActivity


class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        connectRfid()
        views()
        clicks()
        setupToolBar()

        val versionName = BuildConfig.VERSION_NAME
        binding.footer.version.text = "$versionName"
    }

    fun setupToolBar() {
        setSupportActionBar(binding.tool.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            title = "Dashboard"
            binding.tool.toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun onScannedData(data: String?) {

    }

    override fun handleSetText(msg: String?) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            binding.rfidStatusTv.text = msg
        } else {
            Handler(Looper.getMainLooper()).post {
                binding.rfidStatusTv.text = msg
            }
        }
    }

    private fun clicks() {
        binding.btnRegistration.apply {
            setBackgroundColor(Color.parseColor("#f1401c"))
            setOnClickListener {
                startActivity(Intent(context, RegistrationActivity::class.java))
            }
        }

        binding.btnIssuance.apply {
            setBackgroundColor(Color.parseColor("#84ea2d"))
            setOnClickListener{
                startActivity(Intent(this@MainActivity, IssuanceActivity::class.java))
            }
        }

        binding.btnSearch.apply {
            setBackgroundColor(Color.parseColor("#479ef5"))
            setOnClickListener{
                startActivity(Intent(this@MainActivity, LocatorActivity::class.java))
            }
        }

        binding.btnBinning.apply {
            setBackgroundColor(Color.parseColor("#DB8B2E"))
            setOnClickListener {
                startActivity(Intent(this@MainActivity, BinningActivity::class.java))
            }
        }
    }

    private fun views() {}

    override fun onBackPressed() {
        super.onBackPressed()
        disconnectRfid()
    }

}