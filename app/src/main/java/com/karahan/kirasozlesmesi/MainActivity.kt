package com.karahan.kirasozlesmesi

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.apache.poi.ss.usermodel.CellRangeAddress
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.util.Locale
import kotlin.math.ceil

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

    private val createDocument =
        registerForActivityResult(
            ActivityResultContracts.CreateDocument(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
        ) { uri ->
            if (uri != null) {
                writeExcel(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(28, 28, 28, 28)

        val scroll = ScrollView(this)

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
        tenantAddress = multiField(content, "İkametgah")
        tenantWork = field(content, "İşyeri")
        tenantPhone = field(content, "Telefon")

        section(content, "3. KİRALANAN EV")

        flat = field(content, "Daire")
        neighborhood = field(content, "Mahalle")
        street = field(content, "Sokak / No")
        dwelling = field(content, "Ev / Mesken")
        address = multiField(content, "Açık Adres")

        section(content, "4. KİRA BİLGİLERİ")

        monthlyRent = field(content, "Aylık Kira (TL)")
        annualRent = field(content, "Yıllık Kira (TL)")

        monthlyRent.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                calculateAnnual()
            }
        }

        paymentType = field(content, "Ödeme Şekli")
        duration = field(content, "Kira Süresi")
        startDate = field(content, "Kira Başlangıç Tarihi")
        purpose = field(content, "Kullanım Amacı")

        purpose.setText("EV - MESKEN")

        section(content, "5. DEMİRBAŞLAR")

        fixturesContainer = LinearLayout(this)
        fixturesContainer.orientation = LinearLayout.VERTICAL

        addFixture("MUTFAK DOLABI")
        addFixture("VESTİYER")
        addFixture("YÜKLÜK")
        addFixture("KOMBİ")

        content.addView(fixturesContainer)

        val addButton = Button(this)
        addButton.text = "+ DEMİRBAŞ EKLE"

        addButton.setOnClickListener {
            addFixture("")
        }

        content.addView(addButton)

        section(content, "6. ÖZEL ŞARTLAR")

        specialTerms =
            multiField(
                content,
                "Özel şartları yazın..."
            )

        section(content, "7. TAHLİYE TAAHHÜTNAMESİ")

        evacuationDate =
            field(
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
            prepareExcel()
        }

        content.addView(createButton)

        scroll.addView(content)
        root.addView(scroll)

        setContentView(root)
    }

    private fun title(
        layout: LinearLayout,
        text: String
    ) {
        val t = TextView(this)

        t.text = text
        t.textSize = 24f
        t.setPadding(0, 0, 0, 10)

        layout.addView(t)
    }

    private fun section(
        layout: LinearLayout,
        text: String
    ) {
        val t = TextView(this)

        t.text = text
        t.textSize = 19f
        t.setPadding(0, 24, 0, 10)

        layout.addView(t)
    }

    private fun field(
        layout: LinearLayout,
        hint: String
    ): EditText {

        val e = EditText(this)

        e.hint = hint
        e.textSize = 16f

        layout.addView(e)

        return e
    }

    private fun multiField(
        layout: LinearLayout,
        hint: String
    ): EditText {

        val e = EditText(this)

        e.hint = hint
        e.textSize = 16f
        e.minLines = 3
        e.gravity = Gravity.TOP

        layout.addView(e)

        return e
    }

    private fun addFixture(
        value: String
    ) {

        val row = LinearLayout(this)

        row.orientation =
            LinearLayout.HORIZONTAL

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

        row.addView(delete)

        fixturesContainer.addView(row)
    }

    private fun calculateAnnual() {

        val monthly =
            monthlyRent.text
                .toString()
                .replace(",", ".")
                .trim()
                .toDoubleOrNull()

        if (monthly != null) {

            val annual =
                monthly * 12