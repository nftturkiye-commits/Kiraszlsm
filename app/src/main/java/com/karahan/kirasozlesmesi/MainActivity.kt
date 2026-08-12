package com.karahan.kirasozlesmesi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var ownerName: EditText
    private lateinit var ownerTc: EditText
    private lateinit var ownerAddress: EditText
    private lateinit var ownerPhone: EditText

    private lateinit var tenantName: EditText
    private lateinit var tenantTc: EditText
    private lateinit var tenantAddress: EditText
    private lateinit var tenantWork: EditText
    private lateinit var tenantPhone: EditText

    private lateinit var flat: EditText
    private lateinit var neighborhood: EditText
    private lateinit var street: EditText
    private lateinit var dwelling: EditText
    private lateinit var address: EditText

    private lateinit var monthlyRent: EditText
    private lateinit var annualRent: EditText
    private lateinit var paymentType: EditText
    private lateinit var duration: EditText
    private lateinit var startDate: EditText
    private lateinit var purpose: EditText

    private lateinit var fixturesContainer: LinearLayout
    private lateinit var specialTerms: EditText
    private lateinit var evacuationDate: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(28, 28, 28, 28)

        val scrollView = ScrollView(this)
        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL

        title(content, "KARAHAN EMLAK")
        title(content, "KİRA SÖZLEŞMESİ")

        section(content, "1. KİRAYA VEREN")

        ownerName = field(content, "Ad Soyad")
        ownerTc = field(content, "T.C. Kimlik No")
        ownerAddress = field(content, "İkametgah")
        ownerPhone = field(content, "Telefon")

        section(content, "2. KİRACI")

        tenantName = field(content, "Ad Soyad")
        tenantTc = field(content, "T.C. Kimlik No")
        tenantAddress = field(content, "İkametgah")
        tenantWork = field(content, "İşyeri")
        tenantPhone = field(content, "Telefon")

        section(content, "3. KİRALANAN EV")

        flat = field(content, "Daire")
        neighborhood = field(content, "Mahalle")
        street = field(content, "Sokak / No")
        dwelling = field(content, "Ev / Mesken")
        address = multiLineField(content, "Açık Adres")

        section(content, "4. KİRA BİLGİLERİ")

        monthlyRent = field(content, "Aylık Kira (TL)")

        annualRent = field(
            content,
            "Yıllık Kira (TL)"
        )

        monthlyRent.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                calculateAnnualRent()
            }
        }

        paymentType = field(content, "Ödeme Şekli")
        duration = field(content, "Kira Süresi")
        startDate = field(content, "Başlangıç Tarihi")

        purpose = field(
            content,
            "Kullanım Amacı"
        )

        purpose.setText("Mesken")

        section(content, "5. DEMİRBAŞLAR")

        fixturesContainer = LinearLayout(this)
        fixturesContainer.orientation = LinearLayout.VERTICAL

        addFixture("MUTFAK DOLABI")
        addFixture("VESTİYER")
        addFixture("YÜKLÜK")
        addFixture("KOMBİ")

        content.addView(fixturesContainer)

        val addFixtureButton = Button(this)
        addFixtureButton.text = "+ DEMİRBAŞ EKLE"

        addFixtureButton.setOnClickListener {
            addFixture("")
        }

        content.addView(addFixtureButton)

        section(content, "6. ÖZEL ŞARTLAR")

        specialTerms = multiLineField(
            content,
            "Özel şartları yazın..."
        )

        section(content, "7. TAHLİYE TAAHHÜTNAMESİ")

        evacuationDate = field(
            content,
            "Taahhüt Edilen Tahliye Tarihi"
        )

        val info = TextView(this)
        info.text =
            "Kiracı ve kiraya veren bilgileri tahliye taahhütnamesine otomatik aktarılacaktır."
        info.setPadding(0, 8, 0, 16)

        content.addView(info)

        val createButton = Button(this)
        createButton.text = "EXCEL OLUŞTUR"

        createButton.setOnClickListener {
            createExcel()
        }

        content.addView(createButton)

        scrollView.addView(content)
        root.addView(scrollView)

        setContentView(root)
    }

    private fun title(
        layout: LinearLayout,
        text: String
    ) {
        val view = TextView(this)
        view.text = text
        view.textSize = 24f
        view.setPadding(0, 0, 0, 12)

        layout.addView(view)
    }

    private fun section(
        layout: LinearLayout,
        text: String
    ) {
        val view = TextView(this)
        view.text = text
        view.textSize = 19f
        view.setPadding(0, 24, 0, 12)

        layout.addView(view)
    }

    private fun field(
        layout: LinearLayout,
        hint: String
    ): EditText {

        val editText = EditText(this)
        editText.hint = hint
        editText.textSize = 16f

        layout.addView(editText)

        return editText
    }

    private fun multiLineField(
        layout: LinearLayout,
        hint: String
    ): EditText {

        val editText = EditText(this)

        editText.hint = hint
        editText.textSize = 16f
        editText.minLines = 3
        editText.gravity = android.view.Gravity.TOP

        layout.addView(editText)

        return editText
    }

    private fun addFixture(
        value: String
    ) {

        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL

        val input = EditText(this)
        input.hint = "Demirbaş adı"
        input.setText(value)

        val delete = Button(this)
        delete.text = "SİL"

        delete.setOnClickListener {
            fixturesContainer.removeView(row)
        }

        row.addView(
            input,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        row.addView(
            delete,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        fixturesContainer.addView(row)
    }

    private fun calculateAnnualRent() {

        val monthlyText =
            monthlyRent.text.toString().trim()

        if (monthlyText.isEmpty()) {
            return
        }

        val monthly =
            monthlyText
                .replace(",", ".")
                .toDoubleOrNull()
                ?: return

        val annual = monthly * 12

        annualRent.setText(
            if (annual % 1.0 == 0.0) {
                annual.toLong().toString()
            } else {
                annual.toString()
            }
        )
    }

    private fun createExcel() {

        if (ownerName.text.toString().trim().isEmpty()) {
            showMessage("Kiraya veren adını girin.")
            return
        }

        if (tenantName.text.toString().trim().isEmpty()) {
            showMessage("Kiracı adını girin.")
            return
        }

        if (address.text.toString().trim().isEmpty()) {
            showMessage("Kiralanan evin adresini girin.")
            return
        }

        /*
         * Şablon dosyamız:
         *
         * app/src/main/assets/sablon.xlsx
         *
         * Bir sonraki aşamada burada gerçek Excel
         * hücre eşleştirmelerini yapacağız.
         */

        try {

            assets.open("sablon.xlsx").use {
                // Şablonun uygulama içinde bulunduğunu kontrol ediyoruz.
            }

            showMessage(
                "Bilgiler hazır. Excel şablonu bulundu. " +
                        "Hücre eşleştirme aşamasına geçebiliriz."
            )

        } catch (e: Exception) {

            showMessage(
                "sablon.xlsx bulunamadı."
            )
        }
    }

    private fun showMessage(
        message: String
    ) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}