package barlovinto.ricky.myslambook

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import barlovinto.ricky.myslambook.adapter.GenericFavoritesAdapter
import barlovinto.ricky.myslambook.databinding.ActivityForm2Binding
import barlovinto.ricky.myslambook.model.FavoriteItem
import barlovinto.ricky.myslambook.model.SlamBook

class Form2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityForm2Binding
    private lateinit var slamBook: SlamBook
    private val favoritesList = ArrayList<FavoriteItem>()
    private lateinit var favoritesAdapter: GenericFavoritesAdapter

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForm2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Your Favorites"

        // Retrieve SlamBook object from intent
        slamBook = intent.getParcelableExtra("slamBook", SlamBook::class.java) ?: SlamBook()

        setupCategorySpinner()
        setupRecyclerView()

        // Button listeners
        binding.btnAddItem.setOnClickListener { addItem() }
        binding.btnBack.setOnClickListener { onSupportNavigateUp() }
        binding.btnNext.setOnClickListener { onNextClicked() }
    }

    private fun setupCategorySpinner() {
        val categories = resources.getStringArray(R.array.category_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        binding.spnCategory.adapter = adapter
    }

    private fun setupRecyclerView() {
        favoritesAdapter = GenericFavoritesAdapter(favoritesList, ::onEditItem, ::onDeleteItem)
        binding.rvFavorites.layoutManager = LinearLayoutManager(this)
        binding.rvFavorites.adapter = favoritesAdapter
    }

    private fun addItem() {
        val itemName = binding.etFavoriteItem.text.toString().trim()
        if (itemName.isEmpty()) {
            binding.etFavoriteItem.error = "Item name cannot be empty"
            return
        }
        val category = binding.spnCategory.selectedItem.toString()
        favoritesList.add(FavoriteItem(category, itemName))
        favoritesAdapter.notifyItemInserted(favoritesList.size - 1)
        binding.etFavoriteItem.text?.clear()
        Toast.makeText(this, "$itemName added to $category", Toast.LENGTH_SHORT).show()
    }

    private fun onEditItem(position: Int) {
        val item = favoritesList[position]
        val editText = EditText(this).apply {
            setText(item.name)
        }

        AlertDialog.Builder(this)
            .setTitle("Edit ${item.category}")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    favoritesList[position] = item.copy(name = newName)
                    favoritesAdapter.notifyItemChanged(position)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun onDeleteItem(position: Int) {
        favoritesList.removeAt(position)
        favoritesAdapter.notifyItemRemoved(position)
        Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show()
    }

    private fun onNextClicked() {
        // Save favorites to SlamBook object
        slamBook.favorites = favoritesList

        val intent = Intent(this, Form3Activity::class.java)
        intent.putExtra("slamBook", slamBook)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onSupportNavigateUp()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, Form1Activity::class.java)
        intent.putExtra("slamBook", slamBook)
        startActivity(intent)
        finish()
        return true
    }
}