package com.quran.data.dao

import com.quran.data.model.SuraAyah
import com.quran.data.model.bookmark.VerseNote
import kotlinx.coroutines.flow.Flow

interface VerseNotesDao {
  val changes: Flow<Unit>

  suspend fun notes(): List<VerseNote>
  suspend fun note(suraAyah: SuraAyah): VerseNote?
  fun noteFlow(suraAyah: SuraAyah): Flow<VerseNote?>

  suspend fun saveNote(suraAyah: SuraAyah, note: String): Boolean
  suspend fun removeNote(suraAyah: SuraAyah): Boolean
  suspend fun replaceNotes(notes: List<VerseNote>)
}
