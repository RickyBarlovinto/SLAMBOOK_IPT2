package barlovinto.ricky.myslambook

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import barlovinto.ricky.myslambook.adapter.GenericFavoritesAdapter
import barlovinto.ricky.myslambook.databinding.FragmentFormPageTwoBinding
import barlovinto.ricky.myslambook.model.FavoriteItem
import barlovinto.ricky.myslambook.model.SlamBook

class FormPageTwoFragment : Fragment() {

    private lateinit var binding: FragmentFormPageTwoBinding
    private lateinit var slamBook: SlamBook
    private val favoritesList = ArrayList<FavoriteItem>()
    private lateinit var favoritesAdapter: GenericFavoritesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFormPageTwoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        slamBook = arguments?.getParcelable("slamBook") ?: SlamBook()

        setupCategorySpinner()
        setupRecyclerView()

        binding.btnAddItem.setOnClickListener { addItem() }
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnNext.setOnClickListener { onNextClicked() }
    }

    private fun setupCategorySpinner() {
        val categories = resources.getStringArray(R.array.category_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        binding.spnCategory.adapter = adapter
    }

    private fun setupRecyclerView() {
        favoritesAdapter = GenericFavoritesAdapter(favoritesList, ::onEditItem, ::onDeleteItem)
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = favoritesAdapter
    }

    private fun addItem() {
        val itemName = binding.etFavoriteItem.text.toString().trim()
        val category = binding.spnCategory.selectedItem.toString()

        if (itemName.isEmpty()) {
            binding.etFavoriteItem.error = "Item name cannot be empty"
            return
        }

        // Check for duplicates (case-insensitive)
        val isDuplicate = favoritesList.any { it.name.equals(itemName, ignoreCase = true) && it.category == category }
        if (isDuplicate) {
            binding.etFavoriteItem.error = "This item already exists in this category"
            Toast.makeText(requireContext(), "'${itemName}' already exists.", Toast.LENGTH_SHORT).show()
            return
        }

        favoritesList.add(FavoriteItem(category, itemName))
        favoritesAdapter.notifyItemInserted(favoritesList.size - 1)
        binding.etFavoriteItem.text?.clear()
        Toast.makeText(requireContext(), "$itemName added to $category", Toast.LENGTH_SHORT).show()
    }

    private fun onEditItem(position: Int) {
        val item = favoritesList[position]
        val editText = EditText(requireContext()).apply { setText(item.name) }

        AlertDialog.Builder(requireContext())
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
    }

    private fun onNextClicked() {
        if (favoritesList.isEmpty()) {
            Toast.makeText(requireContext(), "Please add at least one favorite item.", Toast.LENGTH_SHORT).show()
            return
        }

        slamBook.favorites = favoritesList

        val bundle = Bundle().apply {
            putParcelable("slamBook", slamBook)
        }
        findNavController().navigate(R.id.action_formPageTwoFragment_to_formPageThreeFragment, bundle)
    }
}