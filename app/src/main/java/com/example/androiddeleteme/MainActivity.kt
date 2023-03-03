package com.example.androiddeleteme

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

//    var path: File? = null
private var file: File? = null
    private var mFileDescriptor: ParcelFileDescriptor? = null
    private var mPdfRenderer: PdfRenderer? = null
    private var mCurrentPage: PdfRenderer.Page? = null
    private val mPageIndex = 1
    private var leftAnimation: Animation? = null
    private var rightAnimation: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.iv_activity_read_quraan)
        addImageFling()
        leftAnimation = AnimationUtils.loadAnimation(this,
            R.anim.slide_in_right )
        rightAnimation = AnimationUtils.loadAnimation(this,
            R.anim.slide_in_left)


        file = fileFromAsset("sample2.pdf")
        Log.i("filename","${file!!.name}")
        renderPDF()
        showPdf(mPageIndex, Direction.None)
        imageView.startAnimation(leftAnimation)

    }

    private fun fileFromAsset(name: String) : File =
        File("$cacheDir/$name").apply { writeBytes(assets.open(name).readBytes()) }

    private fun renderPDF() {
        mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        if (mFileDescriptor != null) {
            mPdfRenderer = PdfRenderer(mFileDescriptor!!)
        }
    }

    private fun showPdf(index: Int, direction: Direction) {
        if ((mPdfRenderer?.pageCount ?: 0) <= index) {
            return
        }

        if (null != mCurrentPage) {
            mCurrentPage?.close()
        }


        mCurrentPage = mPdfRenderer?.openPage(index)

        val bitmap = mCurrentPage?.width?.let {
            mCurrentPage?.height?.let { it1 ->
                Bitmap.createBitmap(
                    it, it1,
                    Bitmap.Config.ARGB_8888
                )
            }
        }


        bitmap?.let {
            mCurrentPage?.render(
                it,
                null,
                Matrix().apply { postRotate(360F) },
                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
            )
        }


    }

    fun swipeRight(direction: Direction) {
        if (mCurrentPage!!.index <= mPdfRenderer!!.pageCount) {
            showPdf((mCurrentPage!!.index) + 1, direction)
        }

    }

    fun swipeLeft(direction: Direction) {
        if (mCurrentPage!!.index > 0) {
            showPdf((mCurrentPage!!.index) - 1, direction)
        }
    }

    private fun addImageFling() {
        imageView.setOnTouchListener(object: SwipeTouchListener(this){
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                swipeLeft(Direction.Left)
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                swipeRight(Direction.Right)
            }

        })

    }
}