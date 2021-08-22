package com.cornershop.counterstest.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.databinding.CounterItemBinding
import com.cornershop.counterstest.model.data.Counter
import com.cornershop.counterstest.viewModel.MainViewModel

class CountersAdapter(private val viewModel: MainViewModel): ListAdapter<Counter, CountersAdapter.CounterViewHolder>(CounterDiff()) {

    class CounterDiff : DiffUtil.ItemCallback<Counter>() {
        override fun areItemsTheSame(firstItem: Counter, secondItem: Counter) =
            firstItem.id == secondItem.id

        override fun areContentsTheSame(firstItem: Counter, secondItem: Counter) =
            firstItem.count == secondItem.count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CounterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CounterItemBinding.inflate(inflater, parent, false)
        binding.viewmodel = viewModel
        return CounterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CounterViewHolder, position: Int) = holder.bind(getItem(position))

    inner class CounterViewHolder(val binding: CounterItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Counter) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}