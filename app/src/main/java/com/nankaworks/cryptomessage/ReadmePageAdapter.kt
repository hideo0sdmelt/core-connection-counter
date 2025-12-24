package com.nankaworks.cryptomessage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Readmeにスワイプでページを切り替えられるようにする機能
 */
class ReadmePageAdapter(private val pages: List<ReadmeActivity.Page>) :
    RecyclerView.Adapter<ReadmePageAdapter.PageViewHolder>() {

    class PageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.titleText)
        val bodyText: TextView = view.findViewById(R.id.bodyText)
        val image: ImageView = view.findViewById(R.id.titleImage)
    }

    // 新しいページのViewを作成
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.page_item, parent, false)
        return PageViewHolder(view)
    }

    // 各ページの内容を設定
    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val page = pages[position]
        holder.titleText.text = page.title
        holder.bodyText.text = page.body
        holder.image.setImageResource(page.imageResId)
    }

    override fun getItemCount() = pages.size
}
