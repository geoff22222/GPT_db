package com.example.encryptedrecords

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.encryptedrecords.databinding.ActivityManagementBinding
import kotlinx.coroutines.launch

class ManagementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagementBinding
    private lateinit var passphrase: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        passphrase = intent.getStringExtra(EXTRA_PASSPHRASE).orEmpty()
        if (passphrase.isBlank()) {
            Toast.makeText(this, getString(R.string.password_required), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val database = DatabaseProvider.getDatabase(this, passphrase)
        val dao = database.recordDao()

        binding.addRecordButton.setOnClickListener {
            val name = binding.recordNameInput.text.toString().trim()
            if (name.isBlank()) {
                Toast.makeText(this, getString(R.string.record_name), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                dao.insert(Record(name = name))
                binding.recordNameInput.text?.clear()
            }
        }

        binding.exportDatabaseButton.setOnClickListener {
            val exported = DatabaseExporter.exportEncryptedDatabase(this)
            if (exported) {
                Toast.makeText(this, getString(R.string.export_success), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.export_failure), Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val EXTRA_PASSPHRASE = "extra_passphrase"
    }
}
