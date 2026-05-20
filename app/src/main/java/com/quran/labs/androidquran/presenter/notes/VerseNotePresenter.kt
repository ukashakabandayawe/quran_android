package com.quran.labs.androidquran.presenter.notes

import com.quran.data.dao.VerseNotesDao
import com.quran.data.model.SuraAyah
import com.quran.data.model.bookmark.VerseNote
import com.quran.labs.androidquran.presenter.Presenter
import com.quran.labs.androidquran.ui.fragment.VerseNoteFragment
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class VerseNotePresenter @Inject internal constructor(
  private val verseNotesDao: VerseNotesDao
) : Presenter<VerseNoteFragment> {
  private val scope = MainScope()
  private var fragment: VerseNoteFragment? = null
  private var currentVerse: SuraAyah? = null
  private var currentNote: VerseNote? = null

  fun setVerse(sura: Int, ayah: Int) {
    val verse = SuraAyah(sura, ayah)
    if (currentVerse == verse) {
      return
    }

    currentVerse = verse
    scope.launch {
      currentNote = verseNotesDao.note(verse)
      fragment?.bindNote(currentNote?.note.orEmpty())
    }
  }

  fun saveNote(text: String) {
    val verse = currentVerse ?: return
    scope.launch {
      verseNotesDao.saveNote(verse, text)
      currentNote = if (text.isBlank()) null else VerseNote(verse.sura, verse.ayah, text)
    }
  }

  fun removeNote() {
    val verse = currentVerse ?: return
    scope.launch {
      verseNotesDao.removeNote(verse)
      currentNote = null
      fragment?.bindNote("")
    }
  }

  override fun bind(what: VerseNoteFragment) {
    fragment = what
    currentNote?.let { what.bindNote(it.note) }
  }

  override fun unbind(what: VerseNoteFragment) {
    if (fragment == what) {
      fragment = null
    }
  }
}