package com.example.wimmy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*

import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import com.example.wimmy.db.PhotoDB
import com.example.wimmy.db.PhotoData
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.photo_view.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private var photoList = arrayListOf<PhotoData>(
        PhotoData(0, "dump", "dump1", "dump", "dump1", 0, false),
        PhotoData(0, "dump", "dump1", "dump", "dump1", 0, false),
        PhotoData(0, "dump", "dump1", "dump", "dump1", 0, false),
        PhotoData(0, "dump", "dump2", "dump", "dump2", 1, false),
        PhotoData(0, "dump", "dump2", "dump", "dump2", 1, false),
        PhotoData(0, "dump", "dump2", "dump", "dump2", 1, false),
        PhotoData(0, "dump", "dump2", "dump", "dump2", 1, false),
        PhotoData(0, "dump", "dump3", "dump", "dump3", 2, false),
        PhotoData(0, "dump", "dump3", "dump", "dump3", 2, false),
        PhotoData(0, "dump", "dump3", "dump", "dump3", 2, false),
        PhotoData(0, "dump", "dump4", "dump", "dump3", 2, false)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavigationView = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        val tb: Toolbar = findViewById(R.id.main_toolbar)
        tb.bringToFront()


     /*   val go_intent = findViewById(R.id.activity_main) as View
        go_intent.setOnClickListener {
            val intent = Intent(this@MainActivity, MapActivity::class.java)
            startActivity(intent)
        }*/
      
        SetHeader()
    }

    private fun SetHeader() {
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(null)
    }
  

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            //R.id.favorate_menu =>
            //
        }
        return super .onOptionsItemSelected(item)
    }


    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        val tb: Toolbar = findViewById(R.id.main_toolbar)
        tb.visibility = View.VISIBLE
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        when(p0.itemId){
            R.id.menu_name ->{
                val fragmentA = NameFragment()
                transaction.replace(R.id.frame_layout,fragmentA)
            }
            R.id.menu_tag -> {
                val fragmentB = TagFragment()
                transaction.replace(R.id.frame_layout,fragmentB)
            }
            R.id.menu_cal -> {
                val fragmentC = CalFragment()
                transaction.replace(R.id.frame_layout,fragmentC)
            }
            R.id.menu_location -> {
                val fragmentD = LocationFragment()
                transaction.replace(R.id.frame_layout,fragmentD)
            }
            R.id.menu_map -> {
                val fragmentE = MapFragment()
                transaction.replace(R.id.frame_layout,fragmentE)
                tb.visibility = View.GONE

            }
        }
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)


        transaction.commit()
        return true
    }



}

