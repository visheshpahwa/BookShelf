package com.example.bookshelf.activity

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookshelf.R
import com.example.bookshelf.databse.BookDatabase
import com.example.bookshelf.databse.BookEntity
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.lang.Exception

class DescriptionActivity : AppCompatActivity() {
    lateinit var txtBookName:TextView
    lateinit var txtBookAuthor:TextView
    lateinit var txtBookPrice:TextView
    lateinit var txtBookRating:TextView
    lateinit var txtBookDesc:TextView
    lateinit var imgBookImage:ImageView
    lateinit var btnAddToFav:Button
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout




    var bookId:String?="10"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName=findViewById(R.id.txtBookName)
        txtBookAuthor=findViewById(R.id.txtBookAuthor)
        txtBookPrice=findViewById(R.id.txtBookPrice)
        txtBookRating=findViewById(R.id.txtBookRating)
        txtBookDesc=findViewById(R.id.txtBookDesc)
        imgBookImage=findViewById(R.id.imgBookImage)
        btnAddToFav=findViewById(R.id.btnAddToFav)
        progressBar=findViewById(R.id.progressBar)
        progressBar.visibility=View.VISIBLE
        progressLayout=findViewById(R.id.progressLayout)
        progressLayout.visibility=View.VISIBLE




        if(intent!=null){
            bookId= intent.getStringExtra("book_id")
        }else if(bookId=="10"){
            finish()
            Toast.makeText(this@DescriptionActivity,"Some unexpected error occurred",Toast.LENGTH_SHORT).show()
        }else{
            finish()
            Toast.makeText(this@DescriptionActivity,"Some unexpected error occurred",Toast.LENGTH_SHORT).show()
        }

        val queue=Volley.newRequestQueue(this@DescriptionActivity)
        val url="http://13.235.250.119/v1/book/get_book/"

        val jsonParams=JSONObject()
        jsonParams.put("book_id",bookId)

        val jsonRequest=object: JsonObjectRequest(Request.Method.POST,url,jsonParams,
            Response.Listener {
            try{
                val success=it.getBoolean("success")
                if(success){
                    val bookJsonObject=it.getJSONObject("book_data")
                    progressLayout.visibility=View.GONE

                    val bookImageUrl=bookJsonObject.getString("image")
                    Picasso.get().load(bookJsonObject.getString("image")).error(R.drawable.default_book_cover).into(imgBookImage)
                    txtBookName.text=bookJsonObject.getString("name")
                    txtBookAuthor.text=bookJsonObject.getString("author")
                    txtBookPrice.text=bookJsonObject.getString("price")
                    txtBookRating.text=bookJsonObject.getString("rating")
                    txtBookDesc.text=bookJsonObject.getString("description")

                    val bookEntity=BookEntity(
                        bookId?.toInt() as Int,
                        txtBookName.text.toString(),
                        txtBookAuthor.text.toString(),
                        txtBookPrice.text.toString(),
                        txtBookRating.text.toString(),
                        txtBookDesc.text.toString(),
                        bookImageUrl
                    )

                    val checkFav=DBAsyncTask(applicationContext,bookEntity,1).execute()
                    val isFav=checkFav.get()

                    if(isFav){
                        btnAddToFav.text="Remove from Favourites"
                        val favColor=ContextCompat.getColor(applicationContext,R.color.colorFav)
                        btnAddToFav.setBackgroundColor(favColor)
                    }else{
                        btnAddToFav.text="Add to Favourites"
                        val noFavColor=ContextCompat.getColor(applicationContext,R.color.bookShelfDark)
                        btnAddToFav.setBackgroundColor(noFavColor)
                    }

                    btnAddToFav.setOnClickListener{
                        if(!DBAsyncTask(applicationContext,bookEntity,1).execute().get()){
                            val async=DBAsyncTask(applicationContext,bookEntity,2).execute()
                            val result=async.get()
                            if(result){
                                Toast.makeText(this@DescriptionActivity,"Book added to favourites",Toast.LENGTH_SHORT).show()
                                btnAddToFav.text="Remove from Favourites"
                                val favColor=ContextCompat.getColor(applicationContext,R.color.colorFav)
                                btnAddToFav.setBackgroundColor(favColor)
                            }else{
                                Toast.makeText(this@DescriptionActivity,"Some error occurred",Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            val async=DBAsyncTask(applicationContext,bookEntity,3).execute()
                            val result=async.get()
                            if(result){
                                Toast.makeText(this@DescriptionActivity,"Book removed from favourites",Toast.LENGTH_SHORT).show()
                                btnAddToFav.text="Add to Favourites"
                                val noFavColor=ContextCompat.getColor(applicationContext,R.color.bookShelfDark)
                                btnAddToFav.setBackgroundColor(noFavColor)
                            }else{
                                Toast.makeText(this@DescriptionActivity,"Some Error occurred",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }else{
                    Toast.makeText(this@DescriptionActivity,"Some Error Occurred!",Toast.LENGTH_SHORT).show()
                }
            }catch (e: Exception){
                Toast.makeText(this@DescriptionActivity,"Some Error Occurred!",Toast.LENGTH_SHORT).show()
            }
        },Response.ErrorListener{
                Toast.makeText(this@DescriptionActivity,"Some Error Occurred! $it",Toast.LENGTH_SHORT).show()
        }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers=HashMap<String,String>()
                headers["Content-type"]="application/json"
                headers["token"]="d765f26d912196"
                return headers
            }
        }
        queue.add(jsonRequest)
    }

    class DBAsyncTask(val context: Context,val bookEntity: BookEntity,val mode:Int):AsyncTask<Void,Void,Boolean>(){

        val db= Room.databaseBuilder(context,BookDatabase::class.java,"books-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            when(mode){
                1->{
//                    Check DB if books in favourites or not
                    val book:BookEntity?=db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book!=null
                }
                2->{
//                    Save books into DB as Favourites
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }
                3->{
//                    Remove the favourite Book
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }

            return false
        }

    }
}