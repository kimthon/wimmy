package com.example.wimmy.fragment


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.wimmy.MainActivity
import com.example.wimmy.Main_PhotoView
import com.example.wimmy.R

/**
 * A simple [Fragment] subclass.
 */
class LocationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_location, null)
        val test = view?.findViewById<Button>(R.id.test_button)
        test!!.setOnClickListener ( object :View.OnClickListener {
            override fun onClick(v: View?){
            val intent = Intent(context, Main_PhotoView::class.java)
            startActivity(intent)
        }
    })

        // Inflate the layout for this fragment
        return view
    }


}
