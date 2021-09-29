package uz.invan.rovitalk.data.models.story

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Abdulaziz Rasulbek on 23/09/21.
 * Copyright (c) 2021  All rights reserved.
 **/
@Parcelize
data class Story(val id: String, val image: String) : Parcelable
