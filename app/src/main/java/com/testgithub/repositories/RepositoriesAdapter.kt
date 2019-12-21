package com.testgithub.repositories

import android.text.SpannableString
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.testgithub.R
import com.testgithub.common.TextUtils
import com.testgithub.extention.getColorCompat
import com.testgithub.repositories.model.Repository
import kotlinx.android.synthetic.main.item_repository.view.*
import timber.log.Timber


class RepositoriesAdapter : ListAdapter<Repository, RepositoryViewHolder>(asyncDifferConfig) {
    var itemClickListener: ((repository: Repository) -> Unit)? = null
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val repositoryViewHolder = RepositoryViewHolder(parent)
        repositoryViewHolder.itemClickListener = { media -> itemClickListener?.invoke(media) }
        return repositoryViewHolder
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        if (position == itemCount - 1) {
            onBottomReachedListener?.onBottomReached(position)
        }
        val item = getItem(position)
        holder.bind(item, highligtedText)
    }

    override fun onViewRecycled(holder: RepositoryViewHolder) {
        super.onViewRecycled(holder)
        holder.onRecycled()
    }
}

class RepositoryViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_repository, parent, false)
    ) {

    var itemClickListener: ((repository: Repository) -> Unit)? = null
    val nameTextView: TextView = itemView.nameTextView
    fun bind(item: Repository, highligtedText: String) {

        val spannable = SpannableString("${item.owner.login}/${item.name}")
        TextUtils.highlightText(
            spannable,
            "${item.owner.login}/${item.name}",
            highligtedText,
            nameTextView.context.getColorCompat(R.color.colorAccent)
        )
        nameTextView.text = spannable
    }

    fun onRecycled() {
        nameTextView.text = ""
        Timber.tag("TestGitHub").d("RepositoryViewHolder recycled")
    }
}

interface OnBottomReachedListener {
    fun onBottomReached(position: Int)
}