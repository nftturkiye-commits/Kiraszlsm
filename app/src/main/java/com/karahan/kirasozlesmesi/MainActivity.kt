package com.karahan.kirasozlesmesi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class MainActivity : AppCompatActivity() {

    private lateinit var kirayaVeren: EditText
    private lateinit var kirayaVerenTc: EditText
    private lateinit var kiraci: EditText
    private lateinit var kiraciTc: EditText
    private lateinit var adres: EditText
    private lateinit var aylikKira: EditText
    private lateinit var kiraBaslangic: EditText

    private val dosyaOlusturucu =
        registerForActivityResult(
            ActivityResultContracts.CreateDocument(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
        ) { uri ->

            if (uri != null) {
                excelOlustur(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(32, 32, 32, 32)

        val title = TextView(this)
        title.text = "KARAHAN EMLAK\nKİRA SÖZLEŞMESİ"
        title.textSize = 24f

        layout.addView(title)

        kirayaVeren =
            alanEkle(layout, "Kiraya Veren Adı Soyadı")

        kirayaVerenTc =
            alanEkle(layout, "Kiraya Veren T.C. Kimlik No")

        kiraci =
            alanEkle(layout, "Kiracının Adı Soyadı")

        kiraciTc =
            alanEkle(layout, "Kiracının T.C. Kimlik No")

        adres =
            alanEkle(layout, "Kiralanan Taşınmazın Adresi")

        aylikKira =
            alanEkle(layout, "Aylık Kira Bedeli")

        kiraBaslangic =
            alanEkle(layout, "Kira Başlangıç Tarihi")

        val kaydet = Button(this)
        kaydet.text = "SÖZLEŞMEYİ OLUŞTUR"

        kaydet.setOnClickListener {
            olustur()
        }

        layout.addView(kaydet)

        val scrollView = ScrollView(this)
        scrollView.addView(layout)

        setContentView(scrollView)
    }

    private fun alanEkle(
        layout: LinearLayout,
        baslik: String
    ): EditText {

        val textView = TextView(this)
        textView.text = baslik
        textView.textSize = 16f

        layout.addView(textView)

        val editText = EditText(this)
        editText.hint = baslik
        editText.textSize = 16f

        layout.addView(editText)

        return editText
    }

    private fun olustur() {

        val kirayaVerenAd =
            kirayaVeren.text.toString().trim()

        val kirayaVerenTcNo =
            kirayaVerenTc.text.toString().trim()

        val kiraciAd =
            kiraci.text.toString().trim()

        val kiraciTcNo =
            kiraciTc.text.toString().trim()

        val tasinmazAdres =
            adres.text.toString().trim()

        val kira =
            aylikKira.text.toString().trim()

        val baslangic =
            kiraBaslangic.text.toString().trim()

        if (kirayaVerenAd.isEmpty() ||
            kiraciAd.isEmpty() ||
            tasinmazAdres.isEmpty()
        ) {

            Toast.makeText(
                this,
                "Lütfen zorunlu bilgileri doldurun.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        /*
         * Dosya adını kullanıcıya soracağız.
         * Örnek:
         * Kira_Sozlesmesi_Ahmet_Yilmaz.xlsx
         */

        dosyaOlusturucu.launch(
            "Kira_Sozlesmesi_${kiraciAd.replace(" ", "_")}.xlsx"
        )
    }

    private fun excelOlustur(uri: Uri) {

        try {

            /*
             * GitHub'daki:
             *
             * app/src/main/assets/sablon.xlsx
             *
             * dosyasını uygulamanın assets klasöründen açıyoruz.
             */

            val inputStream =
                assets.open("sablon.xlsx")

            val workbook =
                XSSFWorkbook(inputStream)

            inputStream.close()

            /*
             * Excel'in ilk sayfasını kullanıyoruz.
             */

            val sheet =
                workbook.getSheetAt(0)

            /*
             * ŞİMDİLİK hücre eşleştirmeleri:
             *
             * B2 = Kiraya veren
             * B3 = Kiraya veren TC
             * B4 = Kiracı
             * B5 = Kiracı TC
             * B6 = Adres
             * B7 = Aylık kira
             * B8 = Başlangıç tarihi
             *
             * Excel formundaki gerçek hücreler farklıysa
             * sadece bu bölüm değiştirilecek.
             */

            sheet.getRow(1)
                .getCellOrCreate(1)
                .setCellValue(kirayaVeren.text.toString())

            sheet.getRow(2)
                .getCellOrCreate(1)
                .setCellValue(kirayaVerenTc.text.toString())

            sheet.getRow(3)
                .getCellOrCreate(1)
                .setCellValue(kiraci.text.toString())

            sheet.getRow(4)
                .getCellOrCreate(1)
                .setCellValue(kiraciTc.text.toString())

            sheet.getRow(5)
                .getCellOrCreate(1)
                .setCellValue(adres.text.toString())

            sheet.getRow(6)
                .getCellOrCreate(1)
                .setCellValue(aylikKira.text.toString())

            sheet.getRow(7)
                .getCellOrCreate(1)
                .setCellValue(kiraBaslangic.text.toString())

            /*
             * Kullanıcının seçtiği yere Excel'i kaydet.
             */

            contentResolver.openOutputStream(uri).use { outputStream ->

                if (outputStream == null) {
                    throw Exception("Dosya oluşturulamadı.")
                }

                workbook.write(outputStream)
            }

            workbook.close()

            Toast.makeText(
                this,
                "Kira sözleşmesi Excel olarak oluşturuldu.",
                Toast.LENGTH_LONG
            ).show()

            /*
             * Oluşturulan Excel'i açmayı deniyoruz.
             */

            try {

                val intent = Intent(
                    Intent.ACTION_VIEW,
                    uri
                )

                intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                startActivity(intent)

            } catch (e: Exception) {

                Toast.makeText(
                    this,
                    "Excel dosyası kaydedildi. Açmak için dosyalar bölümünden seçebilirsiniz.",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                this,
                "Excel oluşturulurken hata oluştu:\n${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /*
     * POI'de satır veya hücre yoksa oluşturan yardımcı fonksiyonlar.
     */

    private fun org.apache.poi.ss.usermodel.Row.getCellOrCreate(
        index: Int
    ): org.apache.poi.ss.usermodel.Cell {

        return getCell(index)
            ?: createCell(index)
    }
}