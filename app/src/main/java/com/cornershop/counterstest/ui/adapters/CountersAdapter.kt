package com.cornershop.counterstest.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.R
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

    inner class CounterViewHolder(val binding: CounterItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnLongClickListener {
        fun bind(item: Counter) {
            binding.container.setOnLongClickListener(this)
            binding.item = item
            binding.executePendingBindings()
        }

        override fun onLongClick(v: View?): Boolean {
            binding.item?.let {
                if (binding.item is Counter) {
                    if (binding.item?.deleteModeVisible!!.not()) {
//                        binding.item.deleteModeVisible = true
                        binding.llCount.visibility = View.GONE
                        binding.checkDelete.visibility = View.VISIBLE
                        binding.container.setBackgroundResource(R.drawable.button_primary)
                    } else {
                        binding.item?.deleteModeVisible = false
                        binding.llCount.visibility = View.GONE
                        binding.checkDelete.visibility = View.VISIBLE
                        binding.container.setBackgroundResource(R.color.black)

                    }
                }
            }
            return false
        }
    }
}