package barlovinto.ricky.myslambook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import barlovinto.ricky.myslambook.R
import barlovinto.ricky.myslambook.model.FavoriteItem

class GenericFavoritesAdapter(
    private val favoritesList: List<FavoriteItem>,
    private val onEdit: (position: Int) -> Unit,
    private val onDelete: (position: Int) -> Unit
) : RecyclerView.Adapter<GenericFavoritesAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_entry, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val item = favoritesList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = favoritesList.size

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(item: FavoriteItem) {
            tvCategory.text = item.category
            tvName.text = item.name
            btnEdit.setOnClickListener { onEdit(adapterPosition) }
            btnDelete.setOnClickListener { onDelete(adapterPosition) }
        }
    }
}