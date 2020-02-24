package com.example.wimmy

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.example.wimmy.db.PhotoData
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.TagData
import com.example.wimmy.db.thumbnailData
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val bnv = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        bnv.setOnNavigationItemSelectedListener(this)
        val tb: Toolbar = findViewById(R.id.main_toolbar)
        tb.bringToFront()
        setSupportActionBar(tb)

        SetHeader()
        init()

        InsertDummy()
        /*val go_intent = findViewById(R.id.search) as SearchView
        go_intent.setOnClickListener {
            val intent = Intent(this, com.example.wimmy.SearchView::class.java)
            startActivity(intent)
        }*/


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

    /*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            //R.id.favorate_menu =>
            //
        }
        return super .onOptionsItemSelected(item)
    }
     */


    /*override fun onContextItemSelected(item: MenuItem): Boolean {
        val bottomNavigationView = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.menu_tag)
        return true
    }*/
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        val tb: Toolbar = findViewById(R.id.main_toolbar)
        tb.visibility = View.VISIBLE
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        when(p0.itemId){
            R.id.menu_name ->{
                val fragmentA = NameFragment()
                transaction.replace(R.id.frame_layout,fragmentA, "name")
            }
            R.id.menu_tag -> {
                val fragmentB = TagFragment()
                transaction.replace(R.id.frame_layout,fragmentB, "tag")
            }
            R.id.menu_cal -> {
                val fragmentC = CalFragment()
                transaction.replace(R.id.frame_layout,fragmentC, "cal")
                tb.visibility = View.GONE
            }
            R.id.menu_location -> {
                val fragmentD = LocationFragment()
                transaction.replace(R.id.frame_layout,fragmentD, "location")
            }
            R.id.menu_map -> {
                val fragmentE = MapFragment()
                transaction.replace(R.id.frame_layout,fragmentE, "map")
                tb.visibility = View.GONE
            }
        }
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val bnv = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        updateBottomMenu(bnv)
    }

    fun updateBottomMenu(navigation: BottomNavigationView) {
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag("name")
        val tag2: Fragment? = supportFragmentManager.findFragmentByTag("tag")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("cal")
        val tag4: Fragment? = supportFragmentManager.findFragmentByTag("location")
        val tag5: Fragment? = supportFragmentManager.findFragmentByTag("map")

        if(tag1 != null && tag1.isVisible()) navigation.getMenu().findItem(R.id.menu_name).setChecked(true)
        if(tag2 != null && tag2.isVisible()) navigation.getMenu().findItem(R.id.menu_tag).setChecked(true)
        if(tag3 != null && tag3.isVisible()) navigation.getMenu().findItem(R.id.menu_cal).setChecked(true)
        if(tag4 != null && tag4.isVisible()) navigation.getMenu().findItem(R.id.menu_location).setChecked(true)
        if(tag5 != null && tag5.isVisible()) navigation.getMenu().findItem(R.id.menu_map).setChecked(true)


    }

    fun init(): Boolean{
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        val fragmentA = NameFragment()
        transaction.replace(R.id.frame_layout,fragmentA, "name")
        transaction.commit()
        return true
    }

    private fun getDate(year : Int, month : Int, day : Int) : Date {
        val date = Calendar.getInstance()
        date.set(year, month - 1, day)
        return date.time
    }

    private fun InsertDummy() {
        val vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        //더미는 한번만 생성
        if(vm.getSize() != 0) return
        var id1 = vm.Insert(PhotoData(0, "dump0", "dump0", "dump", "dump", getDate(2020, 2, 23), false))
        var id2 = vm.Insert(PhotoData(0, "dump", "dump1", "dump", "dump", getDate(2020, 2, 24), false))
        var id3 = vm.Insert(PhotoData(0, "dump", "dump1", "dump", "dump", getDate(2020, 2, 25), false))
        var id4 = vm.Insert(PhotoData(0, "dump", "dump2", "dump", "dump", getDate(2020, 2, 25), false))
        var id5 = vm.Insert(PhotoData(0, "dump", "dump2", "dump", "dump", getDate(2020, 2, 26), false))
        var id6 = vm.Insert(PhotoData(0, "dump", "dump2", "dump", "dump", getDate(2020, 2, 27), false))
        var id7 = vm.Insert(PhotoData(0, "dump", "dump3", "dump", "dump", getDate(2020, 2, 27), false))
        var id8 = vm.Insert(PhotoData(0, "dump", "dump3", "dump", "dump", getDate(2020, 2, 28), false))
        var id9 = vm.Insert(PhotoData(0, "dump", "dump4", "dump", "dump", getDate(2020, 2, 28), false))
        var id10 = vm.Insert(PhotoData(0, "dump", "dump5", "dump", "dump", getDate(2020, 2, 29), false))
        var id11 = vm.Insert(PhotoData(0, "dump", "dump5", "dump", "dump", getDate(2020, 3, 1), false))
        var id12 = vm.Insert(PhotoData(0, "dump", "dump5", "dump", "dump", getDate(2020, 3, 1), false))
        var id13 = vm.Insert(PhotoData(0, "dump", "dump6", "dump", "dump", getDate(2020, 3, 2), false))
        var id14 = vm.Insert(PhotoData(0, "dump", "dump6", "dump", "dump", getDate(2020, 3, 3), false))
        var id15 =  vm.Insert(PhotoData(0, "dump", "dump6", "dump", "dump", getDate(2020, 3, 3), false))
        var id16 = vm.Insert(PhotoData(0, "dump", "dump7", "dump", "dump", getDate(2020, 3, 4), false))
        var id17 = vm.Insert(PhotoData(0, "dump", "dump7", "dump", "dump", getDate(2020, 3, 5), false))
        var id18 = vm.Insert(PhotoData(0, "dump", "dump7", "dump", "dump", getDate(2020, 3, 5), false))
        var id19 = vm.Insert(PhotoData(0, "dump", "dump9", "dump", "dump", getDate(2020, 4, 5), false))
        var id20 = vm.Insert(PhotoData(0, "dump", "dump10", "dump", "dump",getDate(2020, 5, 10), false))

        vm.Insert(TagData(id3, "코로나", "auto"))
        vm.Insert(TagData(id3, "상남", "auto"))

        vm.Insert(TagData(id4, "코로나", "auto"))
        vm.Insert(TagData(id4, "상남", "auto"))


        vm.Insert(TagData(id5, "코로나", "auto"))
    }
}


