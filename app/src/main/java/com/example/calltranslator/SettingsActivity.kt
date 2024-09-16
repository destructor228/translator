package com.example.calltranslator

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val button = findViewById<ImageButton>(R.id.buttonSave)
        button.setOnClickListener {
            onSave()
        }

        val fab = findViewById<FloatingActionButton>(R.id.floatingActionButtonSave)
        fab.setOnClickListener {
            onSave()
        }
    }

    override fun onResume() {
        val sharedPrefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "")
        val password = sharedPrefs.getString("password", "")
        val server = sharedPrefs.getString("server", "")

        if(!username.isNullOrEmpty()){
            findViewById<EditText>(R.id.editTextLogin).setText(username)
        }
        if(!password.isNullOrEmpty()){
            findViewById<EditText>(R.id.editTextPass).setText(password)
        }
        if(!server.isNullOrEmpty()){
            findViewById<EditText>(R.id.editTextServer).setText(server)
        }

        super.onResume()
    }



    private fun onSave()
    {
        // 1. Get data from UI
        val username = findViewById<EditText>(R.id.editTextLogin).text.toString()
        val password = findViewById<EditText>(R.id.editTextPass).text.toString()
        val server   = findViewById<EditText>(R.id.editTextServer).text.toString()

        // 2. Validate data (example - check if name is not empty)
        if (username.isBlank()) { Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show(); return }
        if (password.isBlank()) { Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();  return }
        if (server.isBlank()) { Toast.makeText(this, "Please enter a server address", Toast.LENGTH_SHORT).show(); return }

        // 3. Store data (example - using SharedPreferences)
        val sharedPrefs = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putString("server", server)
        editor.apply()

        // 4. Provide feedback
        Toast.makeText(this, "Data saved!", Toast.LENGTH_SHORT).show()

        finish()
    }

}