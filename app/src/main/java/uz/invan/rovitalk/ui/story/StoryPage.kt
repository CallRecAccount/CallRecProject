package uz.invan.rovitalk.ui.story

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import jp.wasabeef.blurry.Blurry
import uz.invan.rovitalk.R
import uz.invan.rovitalk.databinding.PageStoryBinding
import uz.invan.rovitalk.ui.base.BaseScreen
import uz.invan.rovitalk.util.ktx.onBitmap

/**
 * Created by Abdulaziz Rasulbek on 28/09/21.
 * Copyright (c) 2021  All rights reserved.
 **/
class StoryPage : BaseScreen<PageStoryBinding>(false, null) {
    override fun setBinding(): PageStoryBinding = PageStoryBinding.inflate(layoutInflater)
    private val viewModel by viewModels<StoryViewModel>({ requireParentFragment() })
    private val args by navArgs<StoryPageArgs>()

    override fun onViewAttach() {
        Glide.with(binding.blur)
            .asBitmap()
            .load(args.story.image)
            .onBitmap { bitmap ->
                Blurry.with(requireContext()).radius(25).sampling(2)
                    .color(Color.argb(66, 255, 255, 0))
                    .animate()
                    .from(bitmap).into(binding.blur)
                binding.image.setImageBitmap(bitmap)
            }

    }
}