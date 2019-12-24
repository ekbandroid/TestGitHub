package com.testgithub.repositories

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.testgithub.R
import com.testgithub.extention.getColorCompat
import com.testgithub.extention.setSpannableText
import com.testgithub.repositories.model.Repository
import kotlinx.android.synthetic.main.item_repository.view.*


private const val VIEW_TYPE_ITEM = 0
private const val VIEW_TYPE_LOADING = 1

class RepositoriesAdapter : ListAdapter<Repository, RecyclerView.ViewHolder>(asyncDifferConfig) {
    var itemClickListener: ((repository: Repository) -> Unit)? = null
    var favoriteClickListener: ((repository: Repository) -> Unit)? = null
    var highligtedText = ""
    var onBottomReachedListener: OnBottomReachedListener? = null

    companion object {
        val asyncDifferConfig = object : DiffUtil.ItemCallback<Repository>() {
            override fun areItemsTheSame(oldItem: Repository, newItem: Repository): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Repository, newItem: Repository): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == VIEW_TYPE_ITEM) {
            val repositoryViewHolder = RepositoryViewHolder(parent)
            repositoryViewHolder.favoriteClickListener =
                { media -> favoriteClickListener?.invoke(media) }
            repositoryViewHolder.itemClickListener =
                { media -> itemClickListener?.invoke(media) }
            repositoryViewHolder
        } else {
            LoadingViewHolder(parent)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RepositoryViewHolder -> {
                if (position == itemCount - 1) {
                    onBottomReachedListener?.onBottomReached(position)
                }
                val item = getItem(position)
                holder.bind(item, highligtedText)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }
}

class RepositoryViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_repository, parent, false)
    ) {

    var favoriteClickListener: ((repository: Repository) -> Unit)? = null
    var itemClickListener: ((repository: Repository) -> Unit)? = null
    private val nameTextView: TextView = itemView.nameTextView
    private val favoriteImageView: ImageView = itemView.favoriteImageView
    private val descriptionTextView: TextView = itemView.descriptionTextView

    fun bind(item: Repository, highligtedText: String) {
        if (item.isFavorite) {
            favoriteImageView.setImageResource(android.R.drawable.star_big_on)
        } else {
            favoriteImageView.setImageResource(android.R.drawable.star_big_off)
        }

        nameTextView.setSpannableText(
            "${item.owner.login}/${item.name}",
            highligtedText,
            nameTextView.context.getColorCompat(
                R.color.colorAccent
            )
        )

        descriptionTextView.setSpannableText(
            item.description ?: "",
            highligtedText,
            descriptionTextView.context.getColorCompat(
                R.color.colorAccent
            )
        )

        favoriteImageView.setOnClickListener { favoriteClickListener?.invoke(item) }
        itemView.setOnClickListener { itemClickListener?.invoke(item) }
    }
}

class LoadingViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_loading, parent, false)
    )

interface OnBottomReachedListener {
    fun onBottomReached(position: Int)
}