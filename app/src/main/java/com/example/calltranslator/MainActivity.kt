package com.example.calltranslator

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calltranslator.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView( binding.root )

        binding.progressBar.visibility = View.INVISIBLE
        binding.recyclerView1.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView1.adapter = adapter

        listener = object : EndlessRecyclerViewScrollListener(binding.recyclerView1.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                // Загрузите следующую страницу данных
                getMessages(page)
                //listener!!.resetState()
            }
        }

        binding.recyclerView1.addOnScrollListener(listener as EndlessRecyclerViewScrollListener)

        binding.floatingActionAddTest1.setOnClickListener {
            getToken()
        }
        binding.floatingActionAddTest2.setOnClickListener {
            getMessages()
        }
        binding.floatingActionAddTest3.setOnClickListener {
            Toast.makeText(this, "no implemented", Toast.LENGTH_SHORT).show()
        }
        binding.floatingActionAddTest4.setOnClickListener {
            Toast.makeText(this, "no implemented", Toast.LENGTH_SHORT).show()
        }

        getToken()

        //getMessages()


    }


    override fun onResume() {
        super.onResume()
    }

    private fun getMessages(id:Int = 0) : Boolean
    {
        if(token == "") { Toast.makeText(this, "no token", Toast.LENGTH_SHORT).show(); return false;}
        val sharedPrefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val server = sharedPrefs.getString("server", "")
        if(server!!.isBlank()) {Toast.makeText(this, "server address is not set", Toast.LENGTH_SHORT).show(); return false;}

        binding.progressBar.visibility  = View.VISIBLE
        Thread {
            try {
                val arr = net.list(id)
                if(arr.size > 0)
                {
                    runOnUiThread {
//                        val textView = findViewById<TextView>(R.id.textView)
//                        textView.text = arr.toString()
                        for( i in 0 until arr.size )
                        {
                            adapter.addItem(arr[i])
                        }
                        binding.progressBar.visibility  = View.INVISIBLE
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Ошибка post " + e.message, Toast.LENGTH_SHORT).show() // Показываем Toast
                    listener!!.resetState()
                    binding.progressBar.visibility  = View.INVISIBLE
                }
            }



        }.start()

        return true
    }

    @SuppressLint("SetTextI18n")
    private fun getToken() : Boolean
    {
        if(token != "") return true

        val sharedPrefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "")
        val password = sharedPrefs.getString("password", "")
        val server = sharedPrefs.getString("server", "")

        if(username!!.isBlank()) {Toast.makeText(this, "login is not set", Toast.LENGTH_SHORT).show(); return false;}
        if(password!!.isBlank()) {Toast.makeText(this, "password  is not set", Toast.LENGTH_SHORT).show(); return false;}
        if(server!!.isBlank()) {Toast.makeText(this, "server address is not set", Toast.LENGTH_SHORT).show(); return false;}

        binding.progressBar.visibility  = View.VISIBLE

        Thread {
            try {
                token = net.auth(server, username, password)
                runOnUiThread {
                    if (token != "") {
                        // Успешная аутентификация, обновите UI с токеном
                        val textView = findViewById<TextView>(R.id.textView)
                        textView.text = "Токен: $token"
                        getMessages()
                    } else {
                        Toast.makeText(this, "Ошибка аутентификации", Toast.LENGTH_SHORT).show() // Показываем Toast
                    }
                    binding.progressBar.visibility  = View.INVISIBLE
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Ошибка post " + e.message , Toast.LENGTH_SHORT).show() // Показываем Toast
                    binding.progressBar.visibility  = View.INVISIBLE
                }
            }
        }.start()

        return token != ""
    }


    fun onSettingsClick(item: MenuItem) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }
    fun onInfoClick(item: MenuItem)  {
        startActivity(Intent(this, InfoActivity::class.java))
    }

    fun onClickCard(view: View) {
        val intent = Intent(this, CardActivity::class.java)
        intent.putExtra("id", view.tag as Int)
        startActivity(intent)
    }

}

