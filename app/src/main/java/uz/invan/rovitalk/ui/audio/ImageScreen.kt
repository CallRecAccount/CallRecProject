package uz.invan.rovitalk.ui.audio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import uz.invan.rovitalk.R
import uz.invan.rovitalk.databinding.ScreenImageBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.util.ktx.toBitmap

@Deprecated("Not using now")
@AndroidEntryPoint
class ImageScreen : BaseScreen<ScreenImageBinding>(false, direction = null) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context)
                .inflateTransition(android.R.transition.move)
    }

    override fun setBinding() = ScreenImageBinding.inflate(layoutInflater)
    override fun attachObservers() {

    }

    override fun onViewAttach() {
        val appCompatActivity = activity as AppCompatActivity?
        appCompatActivity?.setSupportActionBar(binding.actionBar)
        appCompatActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appCompatActivity?.supportActionBar?.setDisplayShowHomeEnabled(true)
        appCompatActivity?.supportActionBar?.title = ""

        Glide.with(this)
            .load(R.drawable.image)
            .into(binding.image)
        toBitmap(R.drawable.image)?.let {
            Palette.from(it).generate { palette ->
                val color = palette?.getDominantColor(ContextCompat.getColor(requireContext(),
                    R.color.colorPrimaryDark))
                updateColors(color!!)
                binding.imageBackground.setBackgroundColor(color)
            }
        }
    }
}