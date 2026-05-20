package com.quran.labs.androidquran.fakes

import com.quran.data.dao.VerseNotesDao
import com.quran.data.model.SuraAyah
import com.quran.data.model.bookmark.VerseNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf

class FakeVerseNotesDao : VerseNotesDao {
  private val notes = linkedMapOf<SuraAyah, VerseNote>()
  private val updates = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

  override val changes: Flow<Unit> = updates

  override suspend fun notes(): List<VerseNote> = notes.values.toList()

  override suspend fun note(suraAyah: SuraAyah): VerseNote? = notes[suraAyah]

  override fun noteFlow(suraAyah: SuraAyah): Flow<VerseNote?> = flowOf(notes[suraAyah])

  override suspend fun saveNote(suraAyah: SuraAyah, note: String): Boolean {
    if (note.isBlank()) {
      return removeNote(suraAyah)
    }
    notes[suraAyah] = VerseNote(suraAyah.sura, suraAyah.ayah, note, 1_000L)
    updates.tryEmit(Unit)
    return true
  }

  override suspend fun removeNote(suraAyah: SuraAyah): Boolean {
    notes.remove(suraAyah)
    updates.tryEmit(Unit)
    return true
  }

  override suspend fun replaceNotes(notes: List<VerseNote>) {
    this.notes.clear()
    notes.forEach { note ->
      this.notes[SuraAyah(note.sura, note.ayah)] = note
    }
    updates.tryEmit(Unit)
  }
}
