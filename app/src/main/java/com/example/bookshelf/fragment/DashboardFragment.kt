package com.example.bookshelf.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Request.Method.GET
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.bookshelf.model.Book
import com.example.bookshelf.R
import com.example.bookshelf.adapter.DashboardRecyclerAdapter
import com.example.bookshelf.util.ConnectionManager
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import javax.xml.transform.ErrorListener
import android.provider.Settings.ACTION_WIRELESS_SETTINGS
import android.widget.ProgressBar
import android.widget.RelativeLayout
import org.json.JSONException


class DashboardFragment : Fragment() {

    private lateinit var recyclerDashboard:RecyclerView
    private lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var progressBar:ProgressBar
    lateinit var progressLayout:RelativeLayout
    private val bookInfoList = arrayListOf<Book>(

    )
    private lateinit var recyclerAdapter:DashboardRecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view=inflater.inflate(R.layout.fragment_dashboard, container, false )

        recyclerDashboard=view.findViewById(R.id.recyclerDashboard)

        layoutManager=LinearLayoutManager(activity)
        progressBar=view.findViewById(R.id.progressBar)
        progressLayout=view.findViewById(R.id.progressLayout)

        progressLayout.visibility=View.VISIBLE


        val queue=Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v1/book/fetch_books/"
        if (ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest=object:JsonObjectRequest(
                GET,url,null,
                Response.Listener {
                try{
                    progressLayout.visibility=View.GONE
                    val success=it.getBoolean("success")

                    if(success){
                        val data=it.getJSONArray("data")
                        for(i in 0 until data.length()){
                            val bookJsonObject=data.getJSONObject(i)
                            val bookObject=Book(
                                bookJsonObject.getString("book_id"),
                                bookJsonObject.getString("name"),
                                bookJsonObject.getString("author"),
                                bookJsonObject.getString("rating"),
                                bookJsonObject.getString("price"),
                                bookJsonObject.getString("image")
                            )

                            bookInfoList.add(bookObject)
                            recyclerAdapter= DashboardRecyclerAdapter(activity as Context,bookInfoList)
                            recyclerDashboard.adapter=recyclerAdapter
                            recyclerDashboard.layoutManager=layoutManager

                        }
                    }else{
                        Toast.makeText(activity as Context,"Some error occurred!!!",Toast.LENGTH_SHORT).show()
                    }
                }catch(e: JSONException){
                    Toast.makeText(activity as Context,"Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
                }


                },Response.ErrorListener{
                    Toast.makeText(activity as Context,"Volley Error Occurred", Toast.LENGTH_SHORT).show()
                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String,String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="d765f26d912196"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)

        }else{

            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")
            dialog.setPositiveButton("Open Settings"){ text,listner->
                val settingIntent=Intent(ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){text,listner->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }


    }
