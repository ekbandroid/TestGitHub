package com.testgithub.repositories.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.testgithub.R
import com.testgithub.repositories.favorites.RepositoryViewHolder
import com.testgithub.repositories.model.Repository
import com.testgithub.repositories.search.paging.LoadRepositoriesError
import com.testgithub.repositories.search.paging.NetworkState
import com.testgithub.repositories.search.paging.Status
import kotlinx.android.synthetic.main.item_network_state.view.*

private const val VIEW_TYPE_ITEM = 0
private const val VIEW_TYPE_LOADING = 1

class SearchedRepositoriesAdapter(private val retryCallback: () -> Unit) :
    PagedListAdapter<Repository, RecyclerView.ViewHolder>(asyncDifferConfig) {
    private var networkState: NetworkState? = null
    var itemClickListener: ((repository: Repository) -> Unit)? = null
    var favoriteClickListener: ((repository: Repository) -> Unit)? = null
    var searchText = ""

    companion object {
        val asyncDifferConfig = object : DiffUtil.ItemCallback<Repository>() {
            override fun areItemsTheSame(
                oldItem: Repository,
                newItem: Repository
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Repository,
                newItem: Repository
            ): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == VIEW_TYPE_ITEM) {
            val repositoryViewHolder =
                RepositoryViewHolder(parent)
            repositoryViewHolder.favoriteClickListener =
                { media -> favoriteClickListener?.invoke(media) }
            repositoryViewHolder.itemClickListener =
                { media -> itemClickListener?.invoke(media) }
            repositoryViewHolder
        } else {
            NetworkStateItemViewHolder(
                parent,
                retryCallback
            )
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RepositoryViewHolder -> {
                getItem(position)?.let {
                    holder.bind(it, searchText)
                }
            }
            is NetworkStateItemViewHolder -> {
                holder.bind(networkState)
            }
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun addItems(pagedList: PagedList<Repository>?, searchText:String) {
        super.submitList(pagedList)
        this.searchText = searchText
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }
}

class NetworkStateItemViewHolder(parent: ViewGroup, private val retryCallback: () -> Unit) :
    RecyclerView.ViewHolder(
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_network_state, parent, false)
    ) {
    private val progressBar = itemView.progress_bar
    private val retry = itemView.retry_button
    private val errorMsg = itemView.error_msg

    init {
        retry.setOnClickListener {
            retryCallback.invoke()
        }
    }

    fun bind(networkState: NetworkState?) {
        progressBar.isVisible = networkState?.status == Status.RUNNING
        retry.isVisible = networkState?.status == Status.FAILED
        errorMsg.isVisible= networkState?.error != null
        networkState?.error?.let { error ->
            if (error is LoadRepositoriesError) {
                errorMsg.setText(R.string.load_repositories_error)
            } else {
                errorMsg.setText(R.string.unknown_error)
            }
        }
    }
}