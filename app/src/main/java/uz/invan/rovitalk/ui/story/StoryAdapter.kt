package uz.invan.rovitalk.ui.story

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import uz.invan.rovitalk.data.models.story.Story

/**
 * Created by Abdulaziz Rasulbek on 28/09/21.
 * Copyright (c) 2021  All rights reserved.
 **/
class StoryAdapter(private val list: List<Story>, fragmentManager: FragmentManager) : FragmentStatePagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Fragment {
        return StoryPage().apply {
            arguments = StoryPageArgs(list[position]).toBundle()
        }
    }
}