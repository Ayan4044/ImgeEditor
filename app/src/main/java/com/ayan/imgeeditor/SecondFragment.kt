package com.ayan.imgeeditor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import com.ayan.imgeeditor.databinding.FragmentSecondBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(), LifecycleObserver {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //viewmodel
    private lateinit var liveData: EditorLiveData

    //image info
    private var imageData: String= ""
    private var imageInfo: String= ""
    private var imageName: String =""
    private var imageType : String =""
    private var imageSize: String =""
    private  var displayName: String=""

    private  var   selectedImage: Bitmap? = null

    private var imageURI: Uri?= null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        liveData = ViewModelProvider(requireActivity())[EditorLiveData::class.java]

        binding.showInfo.setOnClickListener {
            showDialog()
        }


        binding.saveimage.setOnClickListener {
            saveImage()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycle.addObserver(this)
    }

    override fun onDetach() {
        super.onDetach()
        lifecycle.removeObserver(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStarted(){
        liveData.LiveDataURI.observe(viewLifecycleOwner){
            uriData ->
            if(uriData == null)
                return@observe
            else{
                imageURI = uriData
              Log.e("Image Type::","${getMime(uriData,requireActivity())}")
                imageType ="Image Type: ${getMime(uriData,requireActivity())} \n"
                displayMediaFileMetaData(uriData)
                val imageStream: InputStream? =
                    uriData.let {
                        requireActivity().contentResolver.openInputStream(it)
                    };
                try {
                    requireActivity().contentResolver.openInputStream(uriData).use { inputStream ->
                        val exif = inputStream?.let { ExifInterface(it) }
                        val orientation: Int =
                            exif!!.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL
                            )
                        exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                        exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                        exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                        exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

                        Log.e("Exif::", "${ exif.getAttribute(ExifInterface.TAG_COPYRIGHT)}")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                 selectedImage = BitmapFactory.decodeStream(imageStream);
                binding.imageView2.setImageBitmap(selectedImage)
            }
        }
    }


    @SuppressLint("Range")
    fun displayMediaFileMetaData(uri: Uri?) {
        if (uri != null) {
            requireActivity().contentResolver
                .query(uri, null, null, null, null, null).use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        // DISPLAY NAME
                        displayName=
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                        Log.e("Display"," $displayName")

                        imageName ="File Name Type: $displayName \n"

                        // MIME TYPE
//                        val mime: String = cursor.getString(cursor.getColumnIndex("mime_type"))
//                        Log.e("Mine"," $mime")

                        // FILE SIZE
                        val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                        var byte_size = -1
                        if (!cursor.isNull(sizeIndex)) {
                            byte_size = cursor.getInt(sizeIndex)
                        }
                        if (byte_size > -1) {
                            Log.e("Size"," ${byte_size.toLong()}")
                            imageSize = "Size = ${byte_size.toLong()}  bytes \n "
                        }

                        // LAST MODIFIED
                        val lastModifiedIndex: Int = cursor.getColumnIndex("last_modified")
                        var last_mod: Long = -1
//                        if (!cursor.isNull(lastModifiedIndex)) {
//                            last_mod = cursor.getLong(lastModifiedIndex)
//                        }
                        if (last_mod > -1) {
                            val simpleDateFormat =
                                SimpleDateFormat("dd MMM yyyy", Locale.US)
                            Log.e("Date"," $simpleDateFormat")
                        }
                    }
                }
        }
    }


    fun getMime(uri : Uri, context: Context) : String?{
        val cr = context.contentResolver
        return cr.getType(uri)
    }

    fun showDialog(){
        imageData = "$imageType $imageName $imageSize"
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Image Info")
            .setMessage(imageData)
            .setPositiveButton(
                "Close"
            ) { dialogInterface, i -> }
            .show()
    }

    private fun saveImage() {
        try {
            binding.imageView2.buildDrawingCache()
            val bm: Bitmap = binding.imageView2.getDrawingCache()
            MediaStore.Images.Media.insertImage(
                requireActivity().getContentResolver(),
                selectedImage,
                displayName,
                "Crooped"
            );

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Info")
                .setMessage("File Saved in Pictures")
                .setPositiveButton(
                    "Close"
                ) { dialogInterface, i -> }
                .show()
        }
        catch (ex: Exception){
            Log.e("Exception","$ex")
        }

    }
}