package com.example.calltranslator

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.fromHtml
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import kotlin.collections.binarySearchBy

class CardActivity : AppCompatActivity() {


    private var item:Message = Message(0);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val id = intent.getIntExtra("id", 0)

        val index = adapter.binarySearch(id)
        if (index >= 0) {
            item = adapter.sortedList[index]
            // ... работа с найденным элементом
            toolbar.title = item.header
            //findViewById<ImageView>(R.id.imageView).setImageResource(item.icon_id)

        } else {
            // Элемент не найден
            toolbar.title = " not found " + id.toString()
        }

        cardByItem()
    }

    private fun cardByItem()
    {
        val imageInfo = findViewById<ImageView>(R.id.imageInfo)
        adapter.setIconById(imageInfo, item.icon_id)

        findViewById<TextView>(R.id.textViewHeader).text = item.header
        findViewById<TextView>(R.id.textViewName).text = item.name
        findViewById<TextView>(R.id.textViewText).text = item.text
        findViewById<TextView>(R.id.textViewData).text = item.data
        findViewById<TextView>(R.id.textViewNumber).text = item.number


        //item full = html
        val textView = findViewById<TextView>(R.id.textViewfull)
        if(item.full.isNullOrEmpty())
        {
            textView.text = "no data"
        }
        else
        {
            val spannedText =     Html.fromHtml(item.full, Html.FROM_HTML_MODE_LEGACY)
            textView.text = spannedText
            textView.movementMethod = LinkMovementMethod.getInstance()
        }


    }

    private fun updateCard()
    {

        Thread {
            try {
                val msg = net.get( item.id )
                if(msg != null)
                {
                    runOnUiThread {
                        item = msg
                        cardByItem()
                        adapter.updateItem(item)
                    }
                }
                else
                {
                    runOnUiThread(Toast.makeText(this, "Ошибка post", Toast.LENGTH_SHORT)::show)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    //binding.progressBar.visibility  = View.INVISIBLE
                }
            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        updateCard()
    }



}