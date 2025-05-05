package com.example.yadro_test.domain

import android.graphics.Bitmap

data class Contact(
    val id: Int,
    val name: String,
    val phoneNumber: String,
    val photo: Bitmap?
)