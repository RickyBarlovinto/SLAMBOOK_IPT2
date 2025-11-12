package barlovinto.ricky.myslambook

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import barlovinto.ricky.myslambook.databinding.ActivityListEntriesBinding

class ListEntriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListEntriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListEntriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadEntries()
    }

    private fun loadEntries() {
        val sharedPref = getSharedPreferences("SlamBookEntries", Context.MODE_PRIVATE)
        val entries = sharedPref.getStringSet("entries", emptySet())?.toList() ?: emptyList()

        if (entries.isEmpty()) {
            // Optional: Show a message if there are no entries
            val emptyMessage = listOf("No entries found.")
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emptyMessage)
            binding.listViewEntries.adapter = adapter
        } else {
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, entries)
            binding.listViewEntries.adapter = adapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish() // Go back to the previous activity
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}