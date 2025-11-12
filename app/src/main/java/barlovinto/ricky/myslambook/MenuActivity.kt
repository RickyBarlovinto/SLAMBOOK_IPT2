package barlovinto.ricky.myslambook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import barlovinto.ricky.myslambook.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreate.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            startActivity(intent)
        }

        binding.btnView.setOnClickListener { 
            val intent = Intent(this, ListEntriesActivity::class.java)
            startActivity(intent)
        }
    }
}