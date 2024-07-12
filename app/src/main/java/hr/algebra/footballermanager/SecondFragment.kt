package hr.algebra.footballermanager

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.Global
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import hr.algebra.footballermanager.dao.Footballer
import hr.algebra.footballermanager.databinding.FragmentSecondBinding
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

const val FOOTBALLER_ID ="hr.algebra.footballermanager.footballer_id"
private const val IMAGE_TYPE = "image/*"
class SecondFragment : Fragment() {
    private lateinit var footballer : Footballer
    private var _binding: FragmentSecondBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchFootballer()
        setupListeners()


    }



    private fun fetchFootballer() {
        val id = arguments?.getLong(FOOTBALLER_ID)
        if(id!=null)
        {
            GlobalScope.launch(Dispatchers.Main) {
                footballer = withContext(Dispatchers.IO){
                    (context?.applicationContext as App).getFootballerDao().getFootballer(id) ?: Footballer()
                }
                bindFootballer()
            }

        }else{
            footballer = Footballer()
            bindFootballer()
        }
    }

    private fun bindFootballer() {
        Picasso.get()
            .load(File(footballer.picturePath?: ""))
            .error(R.mipmap.ic_launcher)
            .transform(RoundedCornersTransformation(50,5))
            .into(binding.ivImage)
        binding.tvDate.text=footballer.birthDate.format(DateTimeFormatter.ISO_DATE)
        binding.etFullName.setText(footballer.fullName ?: "")
        binding.etOverall.setText(footballer.overall.toString() ?: "")
        binding.etPosition.setText(footballer.position ?: "")
    }
    private fun setupListeners() {
        binding.etFullName.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                footballer.fullName=text?.toString()?.trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.etOverall.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                footballer.overall=text?.toString()?.trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.etPosition.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                footballer.position=text?.toString()?.trim()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding.tvDate.setOnClickListener{
            handleDate()
        }
        binding.ivImage.setOnClickListener{
            handleImage()
            true
        }
        binding.btnCommit.setOnClickListener {
            if(formValid()) commit()
        }

    }

    private fun handleDate() {
        DatePickerDialog(
            requireContext(),
            {_, year, month, dayOfMonth ->
                footballer.birthDate = LocalDate.of(year, month + 1, dayOfMonth)
                bindFootballer()
            },
            footballer.birthDate.year,
            footballer.birthDate.monthValue - 1,
            footballer.birthDate.dayOfMonth
        ).show()
    }

    private fun handleImage() {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = IMAGE_TYPE
            imageResult.launch(this)
        }
    }
    private val imageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            if (footballer.picturePath != null) {
                File(footballer.picturePath).delete()
            }
            val dir = context?.applicationContext?.getExternalFilesDir(null)
            val file = File(dir, File.separator.toString() + UUID.randomUUID().toString() + ".jpg")
            context?.contentResolver?.openInputStream(it.data?.data as Uri).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val bos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                    outputStream.write(bos.toByteArray())
                    footballer.picturePath = file.absolutePath
                    bindFootballer()
                }
            }
        }
    }


    private fun formValid(): Boolean {
        var ok = true
        arrayOf(binding.etFullName, binding.etOverall,binding.etPosition).forEach {
            if (it.text.trim().isNullOrEmpty()) {
                it.error = getString(R.string.please_insert_a_value)
                ok = false
            }
        }
        return ok && footballer.picturePath != null
    }

    private fun commit() {
        GlobalScope.launch (Dispatchers.Main){
            withContext(Dispatchers.IO) {
                if (footballer._id == null)
                    (context?.applicationContext as App).getFootballerDao().insert(footballer)
                else {
                    (context?.applicationContext as App).getFootballerDao().update(footballer)
                }
            }
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}