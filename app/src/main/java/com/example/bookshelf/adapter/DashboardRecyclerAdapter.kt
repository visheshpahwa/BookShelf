package com.example.bookshelf.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.bookshelf.activity.DescriptionActivity
import com.example.bookshelf.model.Book
import com.example.bookshelf.R
import com.example.bookshelf.adapter.DashboardRecyclerAdapter.*
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context:Context, private val itemList: ArrayList<Book>): RecyclerView.Adapter<DashboardViewHolder>() {
    class DashboardViewHolder(view:View): RecyclerView.ViewHolder(view){
        val txtBookName:TextView = view.findViewById(R.id.txtRecyclerRowItem)
        val txtBookAuthor:TextView = view.findViewById(R.id.txtSubRecyclerRowItem)
        val txtBookPrice:TextView = view.findViewById(R.id.txtPriceRecyclerRowItem)
        val txtBookRating:TextView = view.findViewById(R.id.txtRatingRecyclerRowItem)
        val imgBookImage:ImageView=view.findViewById(R.id.imgRecyclerRowItem)
        val llContent: RelativeLayout =view.findViewById(R.id.llContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row,parent,false)

        return DashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book=itemList[position]
        holder.txtBookName.text=book.bookName
        holder.txtBookAuthor.text=book.bookAuthor
        holder.txtBookPrice.text=book.bookPrice
        holder.txtBookRating.text=book.bookRating
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.imgBookImage)
        holder.llContent.setOnClickListener{
            val intent=Intent(context, DescriptionActivity::class.java)
            intent.putExtra("book_id",book.bookId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}