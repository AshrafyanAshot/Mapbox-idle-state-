package com.example.mapboxidlestate

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.mapboxidlestate.databinding.ActivityMainBinding
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val TAG = "mapbox_tag"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mapboxMap: MapboxMap
    private lateinit var manager: PointAnnotationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initMapBox()
    }

    private fun initMapBox() {
        mapboxMap = binding.mapView.getMapboxMap().apply {
            addOnMapIdleListener {
                Log.d(TAG, "addOnMapIdleListener: $it")
            }
        }
        manager = binding.mapView.annotations.createPointAnnotationManager()

        CoroutineScope(Dispatchers.Main).launch {
            repeat(Int.MAX_VALUE) {
                addAndRemovePoint()
            }
        }
    }

    private suspend fun addAndRemovePoint() {
        val point: Point = Point.fromLngLat(44.5152, 40.1872)
        val pointAnnotation = addPoint(getDotBitmap() ?: return, point)
        delay(1500L)
        removePoint(pointAnnotation)
        delay(1500L)
    }

    private fun addPoint(bitmap: Bitmap, point: Point): PointAnnotation = PointAnnotationOptions().apply {
        withPoint(point)
        withIconImage(bitmap)
    }.run { manager.create(this) }

    private fun removePoint(point: PointAnnotation) {
        try {
            manager.delete(point)
        } catch (_: Exception) {
        }
    }

    private fun getDotBitmap(): Bitmap? = getBitmapFromShape(R.drawable.dot)

    private fun getBitmapFromShape(drawableRes: Int): Bitmap? {
        var bitmap: Bitmap? = null
        ResourcesCompat.getDrawable(resources, drawableRes, null)?.run {
            val canvas = Canvas()
            bitmap = Bitmap.createBitmap(
                intrinsicWidth,
                intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            canvas.setBitmap(bitmap)
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            draw(canvas)
        }
        return bitmap
    }

}