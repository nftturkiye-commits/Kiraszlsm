package com.karahan.kirasozlesmesi

import android.app.Activity
import android.os.Bundle
import android.widget.*
import android.content.Intent
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : Activity() {

    private lateinit var rentMode: Spinner
    private lateinit var monthlyRent: EditText
    private lateinit var annualRent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        rentMode = findViewById(R.id.rentMode)
        monthlyRent = findViewById(R.id.monthlyRent)
        annualRent = findViewById(R.id.annualRent)

        setupRentMode()
        setupRentCalculation()

        findViewById<Button>(R.id.createExcel).setOnClickListener {
            createExcel()
        }
    }

    private fun setupRentMode() {

        val modes = arrayOf(
            "Aylık ödeme",
            "Yıllık ödeme"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            modes
        )

        rentMode.adapter = adapter

        rentMode.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {

                    if (position == 0) {

                        // Aylık ödeme
                        monthlyRent.isEnabled = true
                        annualRent.isEnabled = false

                        annualRent.setText("")

                    } else {

                        // Yıllık ödeme
                        monthlyRent.isEnabled = false
                        annualRent.isEnabled = true

                        monthlyRent.setText("")
                    }
                }
            }
    }

    private fun setupRentCalculation() {

        monthlyRent.setOnFocusChangeListener { _, hasFocus ->

            if (!hasFocus &&
                rentMode.selectedItemPosition == 0
            ) {

                calculateAnnualRent()
            }
        }
    }

    private fun calculateAnnualRent() {

        val monthly =
            monthlyRent.text.toString()
                .replace(",", ".")
                .toDoubleOrNull()

        if (monthly != null) {

            val annual = monthly * 12

            annualRent.setText(
                if (annual % 1.0 == 0.0)
                    annual.toLong().toString()
                else
                    annual.toString()
            )
        }
    }

    private fun createExcel() {

        if (rentMode.selectedItemPosition == 0) {

            calculateAnnualRent()

        } else {

            if (annualRent.text.toString().trim().isEmpty()) {

                Toast.makeText(
                    this,
                    "Yıllık kira toplamını girin.",
                    Toast.LENGTH_SHORT
                ).show()

                return
            }
        }

        /*
         * Şimdilik form kontrolü burada tamamlanıyor.
         *
         * Bir sonraki aşamada bu bölüm:
         *
         * Form → Excel şablonu → 3 sayfa → XLS çıktı
         *
         * motoruna bağlanacak.
         */

        Toast.makeText(
            this,
            "Form hazır. Excel motoruna bağlanacak.",
            Toast.LENGTH_LONG
        ).show()
    }
}