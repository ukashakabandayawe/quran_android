@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.quran.mobile.bookmark.model

import com.quran.data.dao.VerseNotesDao
import com.quran.data.di.AppScope
import com.quran.data.model.SuraAyah
import com.quran.data.model.bookmark.VerseNote
import com.quran.labs.androidquran.BookmarksDatabase
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

@SingleIn(AppScope::class)
class VerseNotesDaoImpl @Inject constructor(
  private val bookmarksDatabase: BookmarksDatabase,
) : VerseNotesDao {
  private val updates = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

  override val changes: Flow<Unit> =
    updates

  override suspend fun notes(): List<VerseNote> {
    return withContext(Dispatchers.IO) {
      bookmarksDatabase.noteQueries.getNotes().executeAsList().map { it.asVerseNote() }
    }
  }

  override suspend fun note(suraAyah: SuraAyah): VerseNote? {
    return withContext(Dispatchers.IO) {
      bookmarksDatabase.noteQueries
        .getNoteForAyah(suraAyah.sura, suraAyah.ayah)
        .executeAsOneOrNull()
        ?.asVerseNote()
    }
  }

  override fun noteFlow(suraAyah: SuraAyah): Flow<VerseNote?> {
    return flow { emit(note(suraAyah)) }
  }

  override suspend fun saveNote(suraAyah: SuraAyah, note: String): Boolean {
    if (note.isBlank()) {
      return removeNote(suraAyah)
    }

    return withContext(Dispatchers.IO) {
      bookmarksDatabase.noteQueries.saveNote(
        suraAyah.sura,
        suraAyah.ayah,
        note,
        System.currentTimeMillis() / 1000
      )
      updates.tryEmit(Unit)
      true
    }
  }

  override suspend fun removeNote(suraAyah: SuraAyah): Boolean {
    return withContext(Dispatchers.IO) {
      bookmarksDatabase.noteQueries.deleteNote(suraAyah.sura, suraAyah.ayah)
      updates.tryEmit(Unit)
      true
    }
  }

  override suspend fun replaceNotes(notes: List<VerseNote>) {
    withContext(Dispatchers.IO) {
      bookmarksDatabase.noteQueries.transaction {
        bookmarksDatabase.noteQueries.deleteAll()
        notes.forEach { note ->
          bookmarksDatabase.noteQueries.saveNote(
            note.sura,
            note.ayah,
            note.note,
            note.timestamp
          )
        }
      }
      updates.tryEmit(Unit)
    }
  }

  private fun com.quran.mobile.bookmark.Notes.asVerseNote(): VerseNote {
    return VerseNote(
      sura = sura,
      ayah = ayah,
      note = note,
      timestamp = added_date
    )
  }
}
