package uz.invan.rovitalk.ui.story

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.fragment.app.viewModels
import pt.tornelas.segmentedprogressbar.SegmentedProgressBarListener
import uz.invan.rovitalk.data.models.story.Story
import uz.invan.rovitalk.databinding.ScreenStoryBinding
import uz.invan.rovitalk.ui.base.BaseScreen

/**
 * Created by Abdulaziz Rasulbek on 28/09/21.
 * Copyright (c) 2021  All rights reserved.
 **/
class StoryScreen : BaseScreen<ScreenStoryBinding>(false, null) {
    override fun setBinding(): ScreenStoryBinding = ScreenStoryBinding.inflate(layoutInflater)
    private val viewModel by viewModels<StoryViewModel>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewAttach() {
        binding.viewpager.adapter = StoryAdapter(list, parentFragmentManager)
        binding.segmentedProgress.viewPager = binding.viewpager
        binding.segmentedProgress.segmentCount = list.size
        binding.segmentedProgress.start()
        binding.segmentedProgress.listener = object : SegmentedProgressBarListener {
            override fun onFinished() {
                binding.segmentedProgress.reset()
            }

            override fun onPage(oldPageIndex: Int, newPageIndex: Int) {

            }
        }
        binding.viewpager.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.segmentedProgress.pause()
                }
                MotionEvent.ACTION_UP -> {
                    when {
                        event.x < v.width / 4 -> {
                            binding.segmentedProgress.previous()
                        }
                        event.x > v.width * 3 / 4 -> {
                            binding.segmentedProgress.next()
                        }
                        else -> binding.segmentedProgress.start()
                    }
//                    binding.segmentedProgress.start()
                }
            }
            false
        }
    }

    override fun onDestroyView() {
        binding.segmentedProgress.listener = null
        super.onDestroyView()
    }

    private val list = listOf(
        Story("1", "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/91-hkk-pwll-1571999806.jpg"),
        Story("2", "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/614b-qzsvos-1622119198.jpeg"),
        Story("3", "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/71a9iexye7l-1572000260.jpg"),
    )


}