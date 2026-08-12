package com.karahan.kirasozlesmesi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var kirayaVeren: EditText
    private lateinit var kirayaVerenTc: EditText
    private lateinit var kiraci: EditText
    private lateinit var kiraciTc: EditText
    private lateinit var adres: EditText
    private lateinit var aylikKira: EditText
    private lateinit var kiraBaslangic: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(32, 32, 32, 32)

        val title = TextView(this)
        title.text = "KARAHAN EMLAK\nKİRA SÖZLEŞMESİ"
        title.textSize = 24f

        layout.addView(title)

        kirayaVeren = alanEkle(layout, "Kiraya Veren Adı Soyadı")
        kirayaVerenTc = alanEkle(layout, "Kiraya Veren T.C. Kimlik No")
        kiraci = alanEkle(layout, "Kiracının Adı Soyadı")
        kiraciTc = alanEkle(layout, "Kiracının T.C. Kimlik No")
        adres = alanEkle(layout, "Kiralanan Taşınmazın Adresi")
        aylikKira = alanEkle(layout, "Aylık Kira Bedeli")
        kiraBaslangic = alanEkle(layout, "Kira Başlangıç Tarihi")

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

        // Bir sonraki aşamada burada
        // sablon.xlsx dosyasını açıp
        // bilgileri Excel'in ilgili hücrelerine yazacağız.

        android.widget.Toast.makeText(
            this,
            "Bilgiler hazır. Excel oluşturma bölümü sıradaki adım.",
            android.widget.Toast.LENGTH_LONG
        ).show()
    }
}