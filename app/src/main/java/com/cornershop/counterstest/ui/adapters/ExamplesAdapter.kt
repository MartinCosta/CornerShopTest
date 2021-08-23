package com.cornershop.counterstest.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cornershop.counterstest.R
import kotlinx.android.synthetic.main.example_item.view.*

class ExamplesAdapter(private val list: Array<String>, private val onClick: (String) -> Unit): RecyclerView.Adapter<ExamplesAdapter.ExampleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder =
        ExampleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.example_item, parent, false))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) = holder.bind(list[position])

    inner class ExampleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.exampleTxt
        fun bind(item: String) {
            title.text = item
            title.setOnClickListener {
                onClick(list[adapterPosition])
            }
        }
    }
}