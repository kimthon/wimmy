package com.example.wimmy

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.wimmy.Main_PhotoView.Companion.photoList
import com.example.wimmy.db.LatLngData
import com.example.wimmy.db.MediaStore_Dao
import com.example.wimmy.db.PhotoData
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import kotlinx.android.synthetic.main.main_map.*
import java.text.SimpleDateFormat


class Main_Map: AppCompatActivity(), OnMapReadyCallback {

    lateinit var marker_view:  View
    lateinit var tag_marker: TextView
    private var testphotoList = arrayListOf<PhotoData>()
    private var mLastClickTime: Long = 0
    private lateinit var mClusterManager: ClusterManager<LatLngData>
    private val ZoomLevel: Int = 12

    companion object {
        var latlngdata = arrayListOf<LatLngData>()
    }
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_map)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        if(latlngdata.size != 0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngdata[0].latlng, 12F))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12F))
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mClusterManager = ClusterManager<LatLngData>(this, mMap)
        mClusterManager.setRenderer( MarkerClusterRenderer(this, mMap, mClusterManager))
        mMap.setOnCameraChangeListener (mClusterManager)
        mMap.setOnMarkerClickListener(mClusterManager)

        if(latlngdata.size != 0) {
            val builder: LatLngBounds.Builder =
                LatLngBounds.builder() // Bounds 모든 데이터를 맵 안으로 보여주게 하기 위함
            val bounds: LatLngBounds = addItems(builder) // 클러스터 Marker 추가
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, ZoomLevel))
            val zoom: Float = mMap.getCameraPosition().zoom - 0.5f
            mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom))
        }
        mMap.setOnInfoWindowClickListener(object : GoogleMap.OnInfoWindowClickListener {
            override fun onInfoWindowClick(marker: Marker?) {}
        })
        mMap.setOnMapClickListener(object : GoogleMap.OnMapClickListener {              // 맵 클릭 리스너
            override fun onMapClick(p0: LatLng?) {
                card_view.visibility = View.GONE
            }

        })
        mClusterManager.setOnClusterItemClickListener ( object :                // // 클러스터 아이템 클릭 리스너
            ClusterManager.OnClusterItemClickListener<LatLngData> {
            @SuppressLint("SimpleDateFormat")
            override fun onClusterItemClick(p0: LatLngData?): Boolean {
                card_view.visibility = View.VISIBLE
                val center: CameraUpdate = CameraUpdateFactory.newLatLng(p0?.getPosition())
                mMap!!.animateCamera(center)

                val formatter = SimpleDateFormat("yyyy년 MM월 dd일 (E) / HH:mm:ss")
                val date_string = (formatter).format(photoList[p0!!.index].date_info)
                var bitmap =
                    BitmapFactory.decodeFile(photoList[p0!!.index].file_path + '/' + photoList[p0!!.index].name)
                bitmap = MediaStore_Dao.modifyOrientaionById(
                    this@Main_Map,
                    photoList[p0!!.index].photo_id,
                    bitmap
                )

                map_image.setImageBitmap(bitmap)
                map_name.setText(photoList[p0!!.index].name)
                map_date.setText(date_string)
                map_location.setText(photoList[p0!!.index].location_info)
                if (photoList[p0!!.index].favorite == true)
                    map_favorite.setImageResource(R.drawable.ic_favorite_checked)
                else
                    map_favorite.setImageResource(R.drawable.ic_favorite)

                card_view.setOnClickListener {
                    if (SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                        val intent = Intent(this@Main_Map, PhotoViewPager::class.java)
                        intent.putExtra("photo_num", p0!!.index)
                        startActivityForResult(intent, 900)
                    }
                    mLastClickTime = SystemClock.elapsedRealtime()
                }
                return true



                //map_name.setText(photoList[p0!!.index].name)


                    /*if(selectedMarker != p0) {
                        if (selectedMarker != null) {
                            tag_marker.setTextColor(Color.BLACK)
                            tag_marker.setBackgroundResource(R.drawable.ic_marker_phone)
                            selectedMarker!!.setIcon(
                                BitmapDescriptorFactory.fromBitmap(
                                    createDrawableFromView(context, marker_view)
                                )
                            )
                        }

                        if (marker != null) {
                            tag_marker.setTextColor(Color.WHITE)
                            tag_marker.setBackgroundResource(R.drawable.ic_marker_phone_blue)
                            p0.setIcon(
                                BitmapDescriptorFactory.fromBitmap(
                                    createDrawableFromView(
                                        context,
                                        marker_view
                                    )
                                )
                            )
                            selectedMarker = marker
                        }
                    }*/

            }
        })

         mClusterManager.setOnClusterClickListener(object :             // 클러스터 클릭 리스너
            ClusterManager.OnClusterClickListener<LatLngData?> {
            override fun onClusterClick(cluster: Cluster<LatLngData?>): Boolean {
                val builder_c: LatLngBounds.Builder = LatLngBounds.builder()
                for (item in cluster.getItems()) {
                    if (item != null) {
                        builder_c.include(item.getPosition())
                    }
                }
                val bounds_c: LatLngBounds = builder_c.build()
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds_c, ZoomLevel))
                val zoom: Float = mMap.getCameraPosition().zoom - 0.5f
                mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom))
                return true
            }
        })

    }

    private fun addItems(builder: LatLngBounds.Builder): LatLngBounds {
        for (i in latlngdata.indices) {
            mClusterManager.addItem(latlngdata[i])
            builder.include(latlngdata[i].latlng)
        }
        return builder.build()
    }
    private fun createDrawableFromView(context: Context?, view: View): Bitmap {
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
/*fun getExtra(view: View){
    val getname: String?
    val title: TextView = findViewById(R.id.title_location_name)
    val vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

    if (intent.hasExtra("location_name")) {
        getname = intent.getStringExtra("location_name")
        vm.setOpenLocationDir(getname)

        title.text = getname
    }


}*/

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
    var mLastClickTime: Long = 0
    var selectedMarker: Marker? = null
    private var iconGenerator: IconGenerator? = null
    private var markerImageView: ImageView? = null
    private var context = context
    private var mMap = map

    fun MarkerClusterRenderer(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<LatLngData?>?) {

    }

    protected override fun onBeforeClusterItemRendered(item: LatLngData, markerOptions: MarkerOptions) { // 5

        val marker_view = LayoutInflater.from(context).inflate(R.layout.marker_layout, null)
        val tag_marker = marker_view.findViewById(R.id.tag_marker) as TextView
        tag_marker.setText(photoList[item.index].name)


        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context, marker_view))
        )

    }

    private fun createDrawableFromView(context: Context?, view: View): Bitmap {
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

    companion object {
        // 1
        private const val MARKER_DIMENSION = 48 // 2
    }
}