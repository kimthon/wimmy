package com.example.wimmy

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.wimmy.db.LatLngData
import com.example.wimmy.db.PhotoData
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import java.util.*


class Main_Map: AppCompatActivity(), OnMapReadyCallback {

    var selectedMarker: Marker? = null
    lateinit var marker_view:  View
    lateinit var tag_marker: TextView
    private var testphotoList = arrayListOf<PhotoData>()
    private var mLastClickTime: Long = 0

    companion object {
        var latlngdata = arrayListOf<LatLngData>()
    }
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_map)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //testphotoList.add(PhotoData(0,"태그1","Dsa","대한민국 부산광역시 수영구 광안1동 비치그린아파트", Date(System.currentTimeMillis()),true))
        //testphotoList.add(PhotoData(2,"태그2","Dsa","대한민국 창원시 의창구 창이대로483 40", Date(System.currentTimeMillis()),true))
        //testphotoList.add(PhotoData(3,"태그3","Dsa","대한민국 창원시 의창구 사림동", Date(System.currentTimeMillis()),true))
        //latlngdata.add(LatLngData(LatLng(35.162339, 129.108509)))
        //latlngdata.add(LatLngData(LatLng(35.224836, 129.088285)))
        //latlngdata.add(LatLngData(LatLng(35.080117, 129.048376)))


        mMap = googleMap

        mMap.setOnMarkerClickListener(object: GoogleMap.OnMarkerClickListener {

            override fun onMarkerClick(marker: Marker): Boolean {
                val center: CameraUpdate = CameraUpdateFactory.newLatLng(marker.getPosition())
                mMap.animateCamera(center)
                changeSelectedMarker(marker)
                return true
            }
        })
        mMap.setOnMapClickListener (object: GoogleMap.OnMapClickListener{
            override fun onMapClick(p0: LatLng?) {
                changeSelectedMarker(null);
            }
        })

        val mClusterManager: ClusterManager<LatLngData> = ClusterManager<LatLngData>(this, mMap)
        mMap.setOnCameraChangeListener (mClusterManager)

        setCustomMarkerView()

        for (i in latlngdata.indices) {
            //var marker = addMarker(testphotoList[i], latlngdata[i], false)
            //marker.tag = i
            mClusterManager.addItem(latlngdata[i])
        }
    }



    private fun setCustomMarkerView() {
        marker_view = LayoutInflater.from(this).inflate(R.layout.marker_layout, null)
        tag_marker = marker_view.findViewById(R.id.tag_marker) as TextView
    }
/*
    private val sampleMarkerItems: Unit
        /*private get() {
            val sampleList: ArrayList<MarkerItem?> = ArrayList<Any?>()
            sampleList.add(MarkerItem(37.538523, 126.96568, 2500000))
            sampleList.add(MarkerItem(37.527523, 126.96568, 100000))
            sampleList.add(MarkerItem(37.549523, 126.96568, 15000))
            sampleList.add(MarkerItem(37.538523, 126.95768, 5000))
            for (markerItem in sampleList) {
                addMarker(markerItem, false)
            }*/
        }
*/
fun getExtra(view: View){
    val getname: String?
    val title: TextView = findViewById(R.id.title_location_name)
    /*val vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

    if (intent.hasExtra("location_name")) {
        getname = intent.getStringExtra("location_name")
        vm.setOpenLocationDir(recyclerAdapter!!, getname)

        title.text = getname
    }*/


}

    private fun addMarker(photodata: PhotoData, latlngdata: LatLngData, isSelectedMarker:Boolean): Marker{
        var geocoder: Geocoder = Geocoder(this)
        var list: List<Address>? = null
        var lat: Double
        var lon: Double
        var position: LatLng
        var markerOptions = MarkerOptions()


            if (isSelectedMarker) {
                tag_marker.setBackgroundResource(R.drawable.ic_marker_phone_blue)
                tag_marker.setTextColor(Color.WHITE)
            } else {
                tag_marker.setBackgroundResource(R.drawable.ic_marker_phone)
                tag_marker.setTextColor(Color.BLACK)
            }

            markerOptions.position(latlngdata.latlng)
            tag_marker.setText(photodata.file_path)
            markerOptions.icon(
                        BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_view
                    )
                )
            )
            return mMap.addMarker(markerOptions)

    }

    private fun createDrawableFromView(context: Context, view: View): Bitmap {
        val displayMetrics = DisplayMetrics()
        (context as Activity).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
        view.setLayoutParams(
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.buildDrawingCache()
        val bitmap: Bitmap = Bitmap.createBitmap(
            view.getMeasuredWidth(),
            view.getMeasuredHeight(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun changeSelectedMarker(marker: Marker?) {
        // 선택했던 마커 되돌리기
        if(selectedMarker != marker) {
            if (selectedMarker != null) {
                val markertag = addMarker(selectedMarker!!, false)
                markertag.tag = selectedMarker?.tag
                selectedMarker!!.remove()
            }

            // 선택한 마커 표시
            if (marker != null) {
                selectedMarker = addMarker(marker, true)
                selectedMarker!!.tag = marker.tag
                marker.remove()
            }
            else
                selectedMarker = null
        }
    }
    private fun addMarker(marker: Marker, isSelectedMarker: Boolean): Marker {
        val temp = testphotoList[marker.tag as Int]
        return addMarker(temp, latlngdata[marker.tag as Int], isSelectedMarker)
    }
}
