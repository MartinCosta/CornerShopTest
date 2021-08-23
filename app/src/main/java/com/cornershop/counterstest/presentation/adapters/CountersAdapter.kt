package com.cornershop.counterstest.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.R
import com.cornershop.counterstest.databinding.CounterItemBinding
import com.cornershop.counterstest.helpers.ScreenStates
import com.cornershop.counterstest.model.data.Counter
import com.cornershop.counterstest.viewModel.MainViewModel

class CountersAdapter(private val viewModel: MainViewModel): ListAdapter<Counter, CountersAdapter.CounterViewHolder>(CounterDiff()) {

    class CounterDiff : DiffUtil.ItemCallback<Counter>() {
        override fun areItemsTheSame(firstItem: Counter, secondItem: Counter) =
            firstItem.id == secondItem.id

        override fun areContentsTheSame(firstItem: Counter, secondItem: Counter) =
            firstItem.count == secondItem.count
    }
    private val viewHolders: MutableList<CounterViewHolder> = mutableListOf()

    override fun onViewAttachedToWindow(holder: CounterViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: CounterViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CounterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CounterItemBinding.inflate(inflater, parent, false)
        val viewHolder = CounterViewHolder(binding)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewHolder
        viewHolder.markCreated()
        viewHolders.add(viewHolder)

        return viewHolder
    }

    override fun onBindViewHolder(holder: CounterViewHolder, position: Int) = holder.bind(getItem(position))

    inner class CounterViewHolder(val binding: CounterItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnLongClickListener, LifecycleOwner {
        private val lifecycleRegistry = LifecycleRegistry(this)
        private var wasPaused: Boolean = false

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }
        fun markCreated() {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }

        fun markAttach() {
            if (wasPaused) {
                lifecycleRegistry.currentState = Lifecycle.State.RESUMED
                wasPaused = false
            } else {
                lifecycleRegistry.currentState = Lifecycle.State.STARTED
            }
        }

        fun markDetach() {
            wasPaused = true
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }


        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }

        fun bind(item: Counter) {
            binding.container.setOnLongClickListener(this)
            binding.item = item
            binding.executePendingBindings()

            binding.container.setOnClickListener {
                setDeleteItem()
            }
        }

        override fun onLongClick(v: View?): Boolean {
            binding.item?.let {
                if (viewModel.screenState.value!! == ScreenStates.MainScreen)
                    viewModel.setScreenState(ScreenStates.Editing)
                else viewModel.exitEditingState()
            }
            return false
        }

        private fun setDeleteItem() {
            if (viewModel.screenState.value!! == ScreenStates.Editing) {
                if (getItem(adapterPosition).isSelectedForDelete.not()) {
                    binding.container.setBackgroundResource(R.drawable.background_editing_item)
                    binding.checkDelete.visibility = View.VISIBLE
                    getItem(adapterPosition).isSelectedForDelete = true
                    viewModel.addItemToDelete(getItem(adapterPosition))
                } else {
                    binding.container.setBackgroundResource(R.color.welcome_background)
                    binding.checkDelete.visibility = View.GONE
                    getItem(adapterPosition).isSelectedForDelete = false
                    viewModel.removeItemToDelete(getItem(adapterPosition))
                    if(viewModel.listToDelete.isEmpty()) viewModel.exitEditingState()
                }
            }
        }
    }
}