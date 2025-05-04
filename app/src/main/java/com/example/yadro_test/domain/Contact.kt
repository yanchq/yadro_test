package com.example.yadro_test.domain

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: Int,
    val name: String,
    val phoneNumber: String,
    val photo: Bitmap?
): Parcelable