package com.example.encryptedrecords

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.encryptedrecords.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val adapter = RecordAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recordsList.layoutManager = LinearLayoutManager(this)
        binding.recordsList.adapter = adapter

        binding.openDatabaseButton.setOnClickListener {
            val passphrase = binding.passwordInput.text.toString()
            if (passphrase.isBlank()) {
                Toast.makeText(this, getString(R.string.password_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            openDatabase(passphrase)
        }

        binding.manageDatabaseButton.setOnClickListener {
            val passphrase = binding.passwordInput.text.toString()
            if (passphrase.isBlank()) {
                Toast.makeText(this, getString(R.string.password_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, ManagementActivity::class.java).apply {
                putExtra(ManagementActivity.EXTRA_PASSPHRASE, passphrase)
            }
            startActivity(intent)
        }
    }

    private fun openDatabase(passphrase: String) {
        val database = DatabaseProvider.getDatabase(this, passphrase)
        lifecycleScope.launch {
            database.recordDao().getRecordsAlphabetical().collectLatest { records ->
                adapter.submitList(records)
            }
        }
    }
}
