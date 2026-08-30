package com.karahan.kirasozlesmesi

import android.net.Uri
import android.os.Bundle
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
        tenantAddress = field(content, "İkametgah")
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

        specialTerms = multiField(
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
        e.gravity = android.view.Gravity.TOP
        layout.addView(e)
        return e
    }

    private fun addFixture(value: String) {

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

        row.addView(delete)

        fixturesContainer.addView(row)
    }

    private fun calculateAnnual() {

        val monthly =
            monthlyRent.text.toString()
                .replace(",", ".")
                .trim()
                .toDoubleOrNull()

        if (monthly != null) {

            val annual = monthly * 12

            annualRent.setText(
                if (annual % 1.0 == 0.0) {
                    annual.toLong().toString()
                } else {
                    String.format(
                        Locale.US,
                        "%.2f",
                        annual
                    )
                }
            )
        }
    }

    private fun prepareExcel() {

        if (ownerName.text.toString().trim().isEmpty()) {
            message("Kiraya veren adını girin.")
            return
        }

        if (tenantName.text.toString().trim().isEmpty()) {
            message("Kiracı adını girin.")
            return
        }

        if (address.text.toString().trim().isEmpty()) {
            message("Açık adresi girin.")
            return
        }

        createDocument.launch(
            "Kira_Sozlesmesi_" +
                    safeFileName(
                        tenantName.text.toString()
                    ) +
                    ".xlsx"
        )
    }

    private fun writeExcel(uri: Uri) {

        try {

            val input =
                assets.open("sablon.xlsx")

            val workbook =
                XSSFWorkbook(input)

            input.close()

            val sayfa1 =
                workbook.getSheet("Sayfa1")

            val sayfa2 =
                workbook.getSheet("Sayfa2")

            val sayfa3 =
                workbook.getSheet("Sayfa3")

            if (sayfa1 == null ||
                sayfa2 == null ||
                sayfa3 == null
            ) {
                workbook.close()
                message("Excel sayfaları bulunamadı.")
                return
            }

            // ------------------------------------------------
            // SAYFA 1 - KİRA SÖZLEŞMESİ
            // ------------------------------------------------

            setValue(
                sayfa1,
                "E7",
                flat.text.toString()
            )

            setValue(
                sayfa1,
                "E8",
                neighborhood.text.toString()
            )

            setValue(
                sayfa1,
                "E9",
                street.text.toString()
            )

            setValue(
                sayfa1,
                "E10",
                dwelling.text.toString()
            )

            setValue(
                sayfa1,
                "E11",
                ownerName.text.toString()
            )

            setValue(
                sayfa1,
                "E12",
                ownerTc.text.toString()
            )

            setValue(
                sayfa1,
                "E13",
                ownerAddress.text.toString()
            )

            setValue(
                sayfa1,
                "E15",
                tenantName.text.toString()
            )

            setValue(
                sayfa1,
                "E16",
                tenantTc.text.toString()
            )

            /*
             * Şablonda E17 ve E18 zaten:
             *
             * =E8
             * =E9
             *
             * formüllerine bağlı.
             *
             * Bu yüzden onları değiştirmiyoruz.
             */

            setValue(
                sayfa1,
                "E19",
                tenantWork.text.toString()
            )

            val monthly =
                monthlyRent.text.toString()
                    .replace(",", ".")
                    .trim()
                    .toDoubleOrNull()

            val annual =
                annualRent.text.toString()
                    .replace(",", ".")
                    .trim()
                    .toDoubleOrNull()

            if (monthly != null) {

                setNumber(
                    sayfa1,
                    "E21",
                    monthly
                )
            }

            /*
             * Özel yıllık kira mantığı:
             *
             * Yıllık = aylık x 12 ise mevcut Excel
             * formülünü koruyoruz.
             *
             * Kullanıcı yıllık tutarı farklı girdiyse,
             * formülü kaldırıp kullanıcının verdiği
             * yıllık tutarı yazıyoruz.
             */

            if (monthly != null &&
                annual != null &&
                kotlin.math.abs(
                    annual - monthly * 12.0
                ) < 0.01
            ) {

                setFormula(
                    sayfa1,
                    "E22",
                    "12*E21"
                )

            } else if (annual != null) {

                setNumber(
                    sayfa1,
                    "E22",
                    annual
                )
            }

            setValue(
                sayfa1,
                "E23",
                paymentType.text.toString()
            )

            setValue(
                sayfa1,
                "E24",
                duration.text.toString()
            )

            setValue(
                sayfa1,
                "E25",
                startDate.text.toString()
            )

            setValue(
                sayfa1,
                "E27",
                purpose.text.toString()
            )

            // ------------------------------------------------
            // DEMİRBAŞLAR
            // ------------------------------------------------

            val fixtures =
                ArrayList<String>()

            for (i in 0 until fixturesContainer.childCount) {

                val row =
                    fixturesContainer.getChildAt(i)
                        as? LinearLayout ?: continue

                if (row.childCount == 0) {
                    continue
                }

                val input =
                    row.getChildAt(0)
                        as? EditText ?: continue

                val text =
                    input.text.toString().trim()

                if (text.isNotEmpty()) {
                    fixtures.add(text)
                }
            }

            setValue(
                sayfa1,
                "B31",
                fixtures.joinToString("-")
            )

            // ------------------------------------------------
            // ÖZEL ŞARTLAR
            // B50:K51 BİRLEŞİK ALAN
            // ------------------------------------------------

            val terms =
                specialTerms.text.toString().trim()

            if (terms.isNotEmpty()) {

                setWrappedMergedText(
                    sayfa2,
                    "B50:K51",
                    terms
                )
            }

            // ------------------------------------------------
            // TELEFONLAR
            // ------------------------------------------------

            setValue(
                sayfa2,
                "B60",
                "TELEFON :" +
                        ownerPhone.text.toString()
            )

            setValue(
                sayfa2,
                "F60",
                "TELEFON :" +
                        tenantPhone.text.toString()
            )

            // ------------------------------------------------
            // TAHLİYE TAAHHÜTNAMESİ
            // ------------------------------------------------

            /*
             * F8/F9/F13/F14/F35/F36 gibi hücrelerde
             * zaten Sayfa1'e bağlı formüller bulunuyor.
             *
             * Bu formüllere dokunmuyoruz.
             */

            setValue(
                sayfa3,
                "F23",
                evacuationDate.text.toString()
            )

            // ------------------------------------------------
            // DOSYAYI KAYDET
            // ------------------------------------------------

            val output =
                contentResolver.openOutputStream(uri)

            if (output == null) {

                workbook.close()

                message(
                    "Dosya oluşturulamadı."
                )

                return
            }

            output.use {
                workbook.write(it)
            }

            workbook.close()

            message(
                "Kira sözleşmesi Excel olarak oluşturuldu."
            )

        } catch (e: Exception) {

            e.printStackTrace()

            message(
                "Excel oluşturulurken hata oluştu:\n" +
                        e.message
            )
        }
    }

    // --------------------------------------------------------
    // B50:K51 UZUN METİN YAZMA
    // --------------------------------------------------------

    private fun setWrappedMergedText(
        sheet: org.apache.poi.ss.usermodel.Sheet,
        rangeAddress: String,
        text: String
    ) {

        val targetRange =
            CellRangeAddress.valueOf(rangeAddress)

        /*
         * B50:K51 ile çakışan mevcut birleşimleri
         * kaldırıyoruz.
         */

        val overlappingRegions =
            sheet.mergedRegions
                .mapIndexed { index, region ->
                    index to region
                }
                .filter { (_, region) ->

                    region.firstRow <= targetRange.lastRow &&
                    region.lastRow >= targetRange.firstRow &&
                    region.firstColumn <= targetRange.lastColumn &&
                    region.lastColumn >= targetRange.firstColumn
                }
                .map { it.first }
                .sortedDescending()

        for (index in overlappingRegions) {
            sheet.removeMergedRegion(index)
        }

        /*
         * B50:K51 alanını birleştir.
         */

        sheet.addMergedRegion(targetRange)

        /*
         * B50 hücresini al.
         */

        val row =
            sheet.getRow(targetRange.firstRow)
                ?: sheet.createRow(
                    targetRange.firstRow
                )

        val cell =
            row.getCell(targetRange.firstColumn)
                ?: row.createCell(
                    targetRange.firstColumn
                )

        /*
         * Metni yaz.
         */

        cell.setCellValue(text)

        /*
         * Mevcut hücre stilini koruyarak
         * sadece metin kaydırmayı açıyoruz.
         */

        val style =
            cell.cellStyle

        style.wrapText = true

        style.verticalAlignment =
            VerticalAlignment.TOP

        cell.cellStyle = style

        /*
         * Uzunluğa göre yaklaşık satır yüksekliği.
         *
         * B:K oldukça geniş olduğu için
         * yaklaşık 105 karakter / satır
         * kabul ediyoruz.
         */

        val charactersPerLine = 105

        val lineCount =
            ceil(
                text.length.toDouble() /
                        charactersPerLine
            )
                .toInt()
                .coerceAtLeast(1)

        /*
         * B50:K51 iki satırdan oluşuyor.
         * Gerekirse iki satırın yüksekliğini artırıyoruz.
         */

        val linesPerRow = 2

        val rowsNeeded =
            ceil(
                lineCount.toDouble() /
                        linesPerRow
            )
                .toInt()
                .coerceAtLeast(1)

        val heightPerRow =
            (rowsNeeded * 18f)
                .coerceAtLeast(30f)

        val row50 =
            sheet.getRow(49)
                ?: sheet.createRow(49)

        val row51 =
            sheet.getRow(50)
                ?: sheet.createRow(50)

        row50.heightInPoints =
            heightPerRow

        row51.heightInPoints =
            heightPerRow
    }

    // --------------------------------------------------------
    // HÜCRE YAZMA
    // --------------------------------------------------------

    private fun setValue(
        sheet: org.apache.poi.ss.usermodel.Sheet,
        address: String,
        value: String
    ) {

        val cell =
            getCell(
                sheet,
                address
            )

        cell.setCellValue(value)
    }

    private fun setNumber(
        sheet: org.apache.poi.ss.usermodel.Sheet,
        address: String,
        value: Double
    ) {

        val cell =
            getCell(
                sheet,
                address
            )

        cell.setCellValue(value)
    }

    private fun setFormula(
        sheet: org.apache.poi.ss.usermodel.Sheet,
        address: String,
        formula: String
    ) {

        val cell =
            getCell(
                sheet,
                address
            )

        cell.cellFormula =
            formula
    }

    private fun getCell(
        sheet: org.apache.poi.ss.usermodel.Sheet,
        address: String
    ): org.apache.poi.ss.usermodel.Cell {

        val parts =
            address.split(
                Regex(
                    "(?<=\\D)(?=\\d)"
                )
            )

        val columnName =
            parts[0]

        val rowNumber =
            parts[1].toInt() - 1

        val row =
            sheet.getRow(rowNumber)
                ?: sheet.createRow(rowNumber)

        val column =
            columnNumber(columnName)

        return row.getCell(column)
            ?: row.createCell(column)
    }

    private fun columnNumber(
        column: String
    ): Int {

        var result = 0

        for (char in column.uppercase()) {

            result =
                result * 26 +
                        (
                            char.code -
                                    'A'.code +
                                    1
                        )
        }

        return result - 1
    }

    private fun safeFileName(
        name: String
    ): String {

        return name
            .trim()
            .replace(
                Regex(
                    "[^a-zA-Z0-9ğĞüÜşŞıİöÖçÇ ]"
                ),
                ""
            )
            .replace(
                " ",
                "_"
            )
            .ifEmpty {
                "Kira_Sozlesmesi"
            }
    }

    private fun message(
        text: String
    ) {

        Toast.makeText(
            this,
            text,
            Toast.LENGTH_LONG
        ).show()
    }
}