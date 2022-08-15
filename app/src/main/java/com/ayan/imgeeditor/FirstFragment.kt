package com.ayan.imgeeditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.ayan.imgeeditor.databinding.FragmentFirstBinding
import com.canhub.cropper.CropImage
import java.io.IOException
import java.io.InputStream


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), LifecycleObserver {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //viewmodel
    private lateinit var editorLiveData: EditorLiveData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }

        editorLiveData = ViewModelProvider(requireActivity())[EditorLiveData::class.java]


        binding.explore.setOnClickListener {
            CropImage.activity()
                      .start(requireContext(), this)
           // findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.switchtheme.isChecked =true
        binding.textviewtheme.text = "Dark Theme On"

        binding.switchtheme.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                binding.textviewtheme.text = "Dark Theme On"
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else {
                binding.textviewtheme.text = "Dark Theme Off"

                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        })
        }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {


                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri: Uri? = result?.uriContent
                    Log.e("Result uri","$resultUri")
                    editorLiveData.LiveDataURI.postValue(resultUri)
                    val resultFilePath: String? = result?.getUriFilePath(requireContext())
                    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                    //  println("Result URI $resultUri")
                    //  println("Result Path $resultFilePath")
                    if (result != null) {


                        // bottomSheetDialogReq.show()
                        val imageStream: InputStream? =
                            resultUri?.let {
                                requireActivity().contentResolver.openInputStream(it)
                            };
                        val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream);


                        //x-p  println("Base64 ${convertClass.encodeImage(selectedImage)}")
                        //  editextjson.visibility = View.VISIBLE
                        //    editextjson.setText(Converter().encodeImage(selectedImage))
                        //  Timber.tag("Base64").e(convertClass.encodeImage(selectedImage))
                        //  Timber.tag("Base64 Enode").e(convertClass.encodeImage(selectedImage).encode())

                        //  editextjson.setText(convertClass.encodeImage(selectedImage))

                        if (resultUri != null) {
                            //dashBoardViewModel.setImageURI(resultUri)
                        }

                        // dataClassBloodRequest.encodedFrm = convertClass.encodeImage(selectedImage)

                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result!!.error
                }
            }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycle.addObserver(this)
    }

    override fun onDetach() {
        super.onDetach()
        lifecycle.removeObserver(this)
    }
}