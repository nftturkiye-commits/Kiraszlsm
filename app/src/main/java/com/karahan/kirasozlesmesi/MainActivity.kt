package com.karahan.kirasozlesmesi

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var ownerName: EditText
    private lateinit var ownerTc: EditText
    private lateinit var ownerAddress: EditText
    private lateinit var ownerPhone: EditText
    private lateinit var tenantName: EditText
    private lateinit var tenantTc: EditText
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
    private lateinit var currentCondition: EditText
    private lateinit var purpose: EditText
    private lateinit var specialTerms: EditText
    private lateinit var evacuationDate: EditText
    private lateinit var fixturesContainer: LinearLayout
    private var annualManual = false
    private lateinit var annualModeButton: Button

    private val createDocument = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ) { uri -> if (uri != null) writeExcel(uri) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Scrollable form + fixed action bar at the bottom.
        val root = FrameLayout(this)
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 28, 28, 140)
        }

        title(content, "KARAHAN EMLAK")
        TextView(this).apply {
            text = "KİRA SÖZLEŞMESİ"
            textSize = 18f
            setPadding(0, 0, 0, 12)
        }.also { content.addView(it) }

        section(content, "1. KİRAYA VEREN")
        ownerName = field(content, "Ad Soyad")
        ownerTc = field(content, "T.C. Kimlik No")
        ownerAddress = multiField(content, "İkametgah")
        ownerPhone = field(content, "Telefon")

        section(content, "2. KİRACI")
        tenantName = field(content, "Ad Soyad")
        tenantTc = field(content, "T.C. Kimlik No")
        tenantWork = field(content, "İşyeri Adresi")
        tenantPhone = field(content, "Telefon")

        section(content, "3. KİRALANAN TAŞINMAZ")
        flat = field(content, "Daire")
        neighborhood = field(content, "Mahalle")
        street = field(content, "Sokak / No")
        dwelling = field(content, "Ev / Mesken")
        address = multiField(content, "Açık Adres (isteğe bağlı)")
        TextView(this).apply {
            text = "Tahliye taahhütnamesindeki adres, kiralanan taşınmazın Mahalle ve Sokak / No bilgilerinden otomatik alınır."
            textSize = 13f
            setPadding(0, 0, 0, 4)
        }.also { content.addView(it) }

        section(content, "4. KİRA BİLGİLERİ")
        monthlyRent = field(content, "Aylık Kira (TL)")
        annualRent = field(content, "Yıllık Kira (TL)")
        annualRent.isEnabled = false
        annualModeButton = Button(this).apply { text = "YILLIK KİRA: OTOMATİK" }
        annualModeButton.setOnClickListener { toggleAnnualMode() }
        content.addView(annualModeButton)
        monthlyRent.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) calculateAnnual() }

        paymentType = field(content, "Ödeme Şekli")
        duration = field(content, "Kira Süresi")
        startDate = field(content, "Kira Başlangıç Tarihi")
        currentCondition = field(content, "Kiralanan Şeyin Şimdiki Durumu")
        currentCondition.setText("BOŞ")
        purpose = field(content, "Kullanım Amacı")
        purpose.setText("EV - MESKEN")

        section(content, "5. DEMİRBAŞLAR")
        fixturesContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        addFixture("MUTFAK DOLABI")
        addFixture("VESTİYER")
        addFixture("YÜKLÜK")
        addFixture("KOMBİ")
        content.addView(fixturesContainer)
        Button(this).apply {
            text = "+ DEMİRBAŞ EKLE"
            setOnClickListener { addFixture("") }
        }.also { content.addView(it) }

        section(content, "6. HUSUSİ ŞARTLAR")
        specialTerms = multiField(content, "Ek özel şart (isteğe bağlı)")

        section(content, "7. TAHLİYE TAAHHÜTNAMESİ")
        TextView(this).apply {
            text = "Adres otomatik olarak kiralanan taşınmaz adresinden alınacaktır."
            textSize = 13f
            setPadding(0, 0, 0, 4)
        }.also { content.addView(it) }
        evacuationDate = field(content, "Taahhüt Edilen Tahliye Tarihi")

        scroll.addView(content)
        root.addView(scroll, FrameLayout.LayoutParams(-1, -1))

        // Fixed button: always visible while the form scrolls.
        val actionBar = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 10, 20, 16)
            setBackgroundColor(0xFFFFFFFF.toInt())
        }
        Button(this).apply {
            text = "EXCEL'E DÖNÜŞTÜR"
            textSize = 17f
            setOnClickListener { prepareExcel() }
        }.also { actionBar.addView(it, LinearLayout.LayoutParams(-1, 56)) }
        TextView(this).apply {
            text = "Formüller korunur • PC'de Excel elle düzenlenebilir"
            gravity = Gravity.CENTER
            textSize = 12f
        }.also { actionBar.addView(it) }

        val actionParams = FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM)
        root.addView(actionBar, actionParams)
        setContentView(root)
        calculateAnnual()
    }

    private fun title(layout: LinearLayout, text: String) {
        TextView(this).apply {
            this.text = text
            textSize = 26f
            setPadding(0, 0, 0, 4)
        }.also { layout.addView(it) }
    }

    private fun section(layout: LinearLayout, text: String) {
        TextView(this).apply {
            this.text = text
            textSize = 19f
            setPadding(0, 24, 0, 10)
        }.also { layout.addView(it) }
    }

    private fun field(layout: LinearLayout, hint: String): EditText = EditText(this).apply {
        this.hint = hint
        textSize = 16f
        layout.addView(this)
    }

    private fun multiField(layout: LinearLayout, hint: String): EditText = EditText(this).apply {
        this.hint = hint
        textSize = 16f
        minLines = 3
        gravity = Gravity.TOP
        layout.addView(this)
    }

    private fun addFixture(value: String) {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val input = EditText(this).apply {
            hint = "Demirbaş adı"
            setText(value)
        }
        val delete = Button(this).apply {
            text = "SİL"
            setOnClickListener { fixturesContainer.removeView(row) }
        }
        row.addView(input, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(delete)
        fixturesContainer.addView(row)
    }

    private fun toggleAnnualMode() {
        annualManual = !annualManual
        annualRent.isEnabled = annualManual
        annualModeButton.text = if (annualManual) "YILLIK KİRA: MANUEL" else "YILLIK KİRA: OTOMATİK"
        if (!annualManual) calculateAnnual()
    }

    private fun calculateAnnual() {
        if (annualManual) return
        val monthly = parseMoney(monthlyRent.text.toString()) ?: run {
            annualRent.setText("")
            return
        }
        annualRent.setText(formatMoney(monthly * 12.0))
    }

    private fun parseMoney(value: String): Double? {
        val cleaned = value.trim().replace("₺", "").replace(" ", "")
        if (cleaned.isEmpty()) return null
        return if (cleaned.contains(",") && cleaned.contains(".")) {
            if (cleaned.lastIndexOf(',') > cleaned.lastIndexOf('.'))
                cleaned.replace(".", "").replace(",", ".").toDoubleOrNull()
            else cleaned.replace(",", "").toDoubleOrNull()
        } else if (cleaned.contains(",")) {
            cleaned.replace(",", ".").toDoubleOrNull()
        } else cleaned.toDoubleOrNull()
    }

    private fun formatMoney(value: Double): String {
        val f = DecimalFormat("0.##")
        return f.format(value).replace(',', '.')
    }

    private fun prepareExcel() {
        if (ownerName.text.toString().trim().isEmpty() || tenantName.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Kiraya veren ve kiracı adını doldurun.", Toast.LENGTH_LONG).show()
            return
        }
        if (!annualManual) calculateAnnual()
        createDocument.launch("Karahan_Emlak_Kira_Sozlesmesi.xlsx")
    }

    private fun writeExcel(uri: Uri) {
        try {
            assets.open("KARAHAN EMLAK ORİJİNAL FORMÜLLÜ.EN SON... - Kopya (2).xlsx").use { input ->
                val workbook = WorkbookFactory.create(input) as XSSFWorkbook
                val s1 = workbook.getSheet("Sayfa1")
                val s2 = workbook.getSheet("Sayfa2")
                val s3 = workbook.getSheet("Sayfa3")

                setValue(s1, "E7", flat.text.toString())
                setValue(s1, "E8", neighborhood.text.toString())
                setValue(s1, "E9", street.text.toString())
                setValue(s1, "E10", dwelling.text.toString().ifBlank { "EV - MESKEN" })
                setValue(s1, "E11", ownerName.text.toString())
                setValue(s1, "E12", ownerTc.text.toString())
                setValue(s1, "E13", ownerAddress.text.toString())
                setValue(s1, "E15", tenantName.text.toString())
                setValue(s1, "E16", tenantTc.text.toString())
                setValue(s1, "E19", tenantWork.text.toString())
                setValue(s1, "E21", parseMoney(monthlyRent.text.toString()) ?: 0.0)

                if (annualManual) {
                    setValue(s1, "E22", parseMoney(annualRent.text.toString()) ?: 0.0)
                } else {
                    s1.getRow(21).getCell(4).cellFormula = "12*E21"
                }

                setValue(s1, "E23", paymentType.text.toString())
                setValue(s1, "E24", duration.text.toString())
                setValue(s1, "E25", startDate.text.toString())
                setValue(s1, "E26", currentCondition.text.toString().ifBlank { "BOŞ" })
                setValue(s1, "E27", purpose.text.toString().ifBlank { "EV - MESKEN" })
                setValue(s1, "E31", fixtureText())

                // Original Excel links are preserved.
                s1.getRow(16).getCell(4).cellFormula = "E8"
                s1.getRow(17).getCell(4).cellFormula = "E9"
                s2.getRow(57).getCell(1).cellFormula = "Sayfa1!E11"
                s2.getRow(57).getCell(5).cellFormula = "Sayfa1!E15"
                s2.getRow(58).getCell(1).cellFormula = "Sayfa1!E12"
                s2.getRow(58).getCell(5).cellFormula = "Sayfa1!E16"
                s3.getRow(7).getCell(5).cellFormula = "Sayfa1!E15"
                s3.getRow(8).getCell(5).cellFormula = "Sayfa1!E16"
                s3.getRow(12).getCell(5).cellFormula = "Sayfa1!E11"
                s3.getRow(13).getCell(5).cellFormula = "Sayfa1!E12"
                s3.getRow(18).getCell(1).cellFormula = "Sayfa1!E8"
                s3.getRow(19).getCell(1).cellFormula = "Sayfa1!E9"
                s3.getRow(34).getCell(5).cellFormula = "Sayfa1!E15"
                s3.getRow(35).getCell(5).cellFormula = "Sayfa1!E16"

                setValue(s2, "B60", "TELEFON :" + ownerPhone.text.toString())
                setValue(s2, "F60", "TELEFON :" + tenantPhone.text.toString())

                if (specialTerms.text.toString().trim().isNotEmpty()) {
                    setValue(s2, "B50", specialTerms.text.toString().trim())
                }

                setValue(s3, "B24", evacuationDate.text.toString())

                workbook.setForceFormulaRecalculation(true)
                contentResolver.openOutputStream(uri).use { output ->
                    requireNotNull(output)
                    workbook.write(output)
                }
                workbook.close()
                Toast.makeText(this, "Excel oluşturuldu. Formüller korundu.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Excel oluşturulamadı: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setValue(sheet: org.apache.poi.ss.usermodel.Sheet, cellRef: String, value: String) {
        val m = Regex("([A-Z]+)([0-9]+)").matchEntire(cellRef) ?: return
        val colLetters = m.groupValues[1]
        val rowIndex = m.groupValues[2].toInt() - 1
        var col = 0
        for (ch in colLetters) col = col * 26 + (ch - 'A' + 1)
        col -= 1
        val row = sheet.getRow(rowIndex) ?: sheet.createRow(rowIndex)
        val cell = row.getCell(col) ?: row.createCell(col)
        cell.setCellValue(value)
    }

    private fun setValue(sheet: org.apache.poi.ss.usermodel.Sheet, cellRef: String, value: Double) {
        val m = Regex("([A-Z]+)([0-9]+)").matchEntire(cellRef) ?: return
        val colLetters = m.groupValues[1]
        val rowIndex = m.groupValues[2].toInt() - 1
        var col = 0
        for (ch in colLetters) col = col * 26 + (ch - 'A' + 1)
        col -= 1
        val row = sheet.getRow(rowIndex) ?: sheet.createRow(rowIndex)
        val cell = row.getCell(col) ?: row.createCell(col)
        cell.setCellValue(value)
    }

    private fun fixtureText(): String {
        val values = mutableListOf<String>()
        for (i in 0 until fixturesContainer.childCount) {
            val row = fixturesContainer.getChildAt(i) as? LinearLayout ?: continue
            val input = row.getChildAt(0) as? EditText ?: continue
            val text = input.text.toString().trim()
            if (text.isNotEmpty()) values.add(text)
        }
        return values.joinToString("-")
    }
}
