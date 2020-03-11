/*package com.example.wimmy

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.marker_layout.*
import java.text.NumberFormat
import java.util.ArrayList

class Main_Map: AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_map)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val marker = LatLng(35.241615, 128.695587)
        mMap.addMarker(MarkerOptions().position(marker).title("Marker LAB"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
    }

    private fun setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(this).inflate(R.layout.marker_layout, null)
        tv_marker = marker_root_view.findViewById(R.id.tv_marker) as TextView
    }

    private val sampleMarkerItems: Unit
        private get() {
            val sampleList: ArrayList<MarkerItem?> = ArrayList<Any?>()
            sampleList.add(MarkerItem(37.538523, 126.96568, 2500000))
            sampleList.add(MarkerItem(37.527523, 126.96568, 100000))
            sampleList.add(MarkerItem(37.549523, 126.96568, 15000))
            sampleList.add(MarkerItem(37.538523, 126.95768, 5000))
            for (markerItem in sampleList) {
                addMarker(markerItem, false)
            }
        }

    private fun addMarker(markerItem:MarkerItem, isSelectedMarker:Boolean): Marker {
        val position = LatLng(markerItem.getLat(), markerItem.getLon())
        val price = markerItem.getPrice()
        val formatted = NumberFormat.getCurrencyInstance().format((price))
        tag_marker.setText(formatted)
        if (isSelectedMarker)
        {
            tag_marker.setBackgroundResource(R.drawable.marker_focus)
            tag_marker.setTextColor(Color.WHITE)
        }
        else
        {
            tag_marker.setBackgroundResource(R.drawable.marker_free)
            tag_marker.setTextColor(Color.BLACK)
        }
        val markerOptions = MarkerOptions()
        markerOptions.title(Integer.toString(price))
        markerOptions.position(position)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_root_view)))
        return mMap.addMarker(markerOptions)
    }
}*/
