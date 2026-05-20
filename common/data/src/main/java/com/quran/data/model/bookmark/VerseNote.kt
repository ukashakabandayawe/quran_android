package com.quran.data.model.bookmark

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerseNote @JvmOverloads constructor(
  val sura: Int,
  val ayah: Int,
  val note: String,
  val timestamp: Long = System.currentTimeMillis()
)
