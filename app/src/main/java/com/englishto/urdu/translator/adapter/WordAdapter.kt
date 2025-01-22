package com.englishto.urdu.translator.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.englishto.urdu.translator.Data
import com.englishto.urdu.translator.R

class WordAdapter(
    private val dataList: MutableList<Data>,
    private val onDeleteClick: (Data) -> Unit
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {
    var isAscending = false

    inner class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val englishTextView: TextView = view.findViewById(R.id.tvEnglish)
        val urduTextView: TextView = view.findViewById(R.id.tvUrdu)
        val deleteButton: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = dataList[position]
        holder.englishTextView.text = word.english
        holder.urduTextView.text = word.urdu
        holder.deleteButton.setOnClickListener { onDeleteClick(word) }
    }

    override fun getItemCount() = dataList.size

    fun updateDataList(newDataList: List<Data>) {
        dataList.clear()
        dataList.addAll(newDataList)
        notifyDataSetChanged()
    }

    fun sortDataList() {
        if (isAscending) {
            dataList.sortBy { it.id }
        } else {
            dataList.sortByDescending { it.id }
        }
        isAscending = !isAscending
        notifyDataSetChanged()
    }
}
