package com.example.calltranslator

import android.annotation.SuppressLint
import android.content.Context
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar


class InfoActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            //Обработка клика на стрелку "назад"
            finish()
        }
    }

    override fun onResume() {

        val sharedPrefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        //val username = sharedPrefs.getString("username", "")
        //val password = sharedPrefs.getString("password", "")
        val server = sharedPrefs.getString("server", "")

        val myWebView: WebView = findViewById(R.id.ww1)
        //if(!username.isNullOrEmpty()) findViewById<EditText>(R.id.editTextText).setText(username)
        //if(!password.isNullOrEmpty()) findViewById<EditText>(R.id.editTextText2).setText(password)
        if(!server.isNullOrEmpty())
        {
            myWebView.webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                )
                {
                    if (error != null) {
                        Log.e("MainActivity", "Это Ошибка " + error.description )
                    }
                    super.onReceivedError(view, request, error)
                }
                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    //handler?.proceed()
                    super.onReceivedSslError(view, handler, error)
                }
            }

            // Включите JavaScript
            myWebView.settings.javaScriptEnabled = true

            // Включите DOM Storage
            myWebView.settings.domStorageEnabled = true

            // Разрешите загрузку изображений
            myWebView.settings.loadsImagesAutomatically = true

            // Загрузите URL
            myWebView.loadUrl(server)
        }
        else
        {
            val htmlData = "<html><body><p>Ошибка</p><p>в настройках необходимо прописать сервер и применить</p><p>логин и пароль покатят: test/test</p></body></html>"
            myWebView.settings.javaScriptEnabled = true
            myWebView.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null)
        }


        super.onResume()
    }



}