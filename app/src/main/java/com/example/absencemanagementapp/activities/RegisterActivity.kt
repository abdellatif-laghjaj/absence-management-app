package com.example.absencemanagementapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.adapters.RegisterTabsAdapter
import com.google.android.material.tabs.TabLayout

class RegisterActivity : AppCompatActivity() {
    private lateinit var tab_layout: TabLayout
    private lateinit var view_pager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //initiate views and adapters for dropdowns
        initViews()
        initTabs()
    }

    private fun initViews() {
        tab_layout = findViewById(R.id.tab_layout)
        view_pager = findViewById(R.id.view_pager)
    }

    private fun initTabs() {
        view_pager.adapter = RegisterTabsAdapter(supportFragmentManager)
        tab_layout.setupWithViewPager(view_pager)
    }
}