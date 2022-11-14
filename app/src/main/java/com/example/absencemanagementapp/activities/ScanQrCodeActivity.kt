package com.example.absencemanagementapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.budiyev.android.codescanner.CodeScannerView
import com.example.absencemanagementapp.R

class ScanQrCodeActivity : AppCompatActivity() {
    private lateinit var code_scanner_view: CodeScannerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr_code_acticty)
    }
}