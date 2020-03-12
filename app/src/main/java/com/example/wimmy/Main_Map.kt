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
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.wimmy.db.LatLngData
import com.example.wimmy.db.PhotoData
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import java.util.*


class Main_Map: AppCompatActivity(), OnMapReadyCallback {

    var selectedMarker: Marker? = null
    lateinit var marker_view:  View
    lateinit var tag_marker: TextView
    private var testphotoList = arrayListOf<PhotoData>()
    private var mLastClickTime: Long = 0
    private lateinit var mClusterManager: ClusterManager<LatLngData>

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

        /*mMap.setOnMarkerClickListener(object: GoogleMap.OnMarkerClickListener {

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
        })*/

        mClusterManager = ClusterManager<LatLngData>(this, mMap)
        mClusterManager.setRenderer( MarkerClusterRenderer(this, mMap, mClusterManager))
        mMap.setOnCameraChangeListener (mClusterManager)

        //setCustomMarkerView()

        for (i in latlngdata.indices) {
            //var marker = addMarker(testphotoList[i], latlngdata[i], false)
            //marker.tag = i
            mClusterManager.addItem(latlngdata[i])
            mClusterManager.cluster()
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

    /*private fun addMarker(photodata: PhotoData, latlngdata: LatLngData, isSelectedMarker:Boolean): Marker{
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

    }*/



}

class MarkerClusterRenderer(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<LatLngData>?
) : DefaultClusterRenderer<LatLngData>(context, map, clusterManager) {
    private var iconGenerator: IconGenerator? = null
    private var markerImageView: ImageView? = null
    private var context = context
    fun MarkerClusterRenderer(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<LatLngData?>?) {

    }

    protected override fun onBeforeClusterItemRendered(item: LatLngData, markerOptions: MarkerOptions) { // 5
        iconGenerator = IconGenerator(context) // 3
        markerImageView = ImageView(context)

        markerImageView!!.setLayoutParams(
            ViewGroup.LayoutParams(
                MARKER_DIMENSION,
                MARKER_DIMENSION
            )
        )
        iconGenerator!!.setContentView(markerImageView) // 4
        markerImageView?.setImageResource(R.drawable.ic_marker_phone) // 6
        val icon: Bitmap = iconGenerator!!.makeIcon()
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)) // 8
        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(icon)
        )
        //markerOptions.title(item.getTitle())
    }

    /*private fun createDrawableFromView(context: Context, view: View): Bitmap {
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
    }*/

    companion object {
        // 1
        private const val MARKER_DIMENSION = 48 // 2
    }
}