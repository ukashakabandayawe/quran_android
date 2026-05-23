package com.quran.labs.androidquran.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.quran.labs.androidquran.R
import com.quran.labs.androidquran.presenter.notes.VerseNotePresenter
import com.quran.labs.androidquran.ui.PagerActivity
import com.quran.labs.androidquran.ui.helpers.SlidingPagerAdapter
import com.quran.labs.androidquran.util.QuranUtils
import com.quran.mobile.di.AyahActionFragmentProvider
import dev.zacsweers.metro.Inject

class VerseNoteFragment : AyahActionFragment() {
  private lateinit var noteInput: TextInputEditText
  private lateinit var verseReference: TextView
  private var pendingNoteText: String? = null

  @Inject
  lateinit var notePresenter: VerseNotePresenter

  object Provider : AyahActionFragmentProvider {
    override val order = SlidingPagerAdapter.TRANSCRIPT_PAGE + 1
    override val iconResId = R.drawable.ic_edit
    override fun newAyahActionFragment() = VerseNoteFragment()
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    (activity as? PagerActivity)?.pagerActivityComponent?.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.verse_note_panel, container, false)
    verseReference = view.findViewById(R.id.verse_reference)
    noteInput = view.findViewById(R.id.note_input)
    val saveButton = view.findViewById<Button>(R.id.save_note)
    val clearButton = view.findViewById<Button>(R.id.clear_note)

    start?.let { setVerseReference(it.sura, it.ayah) }
    pendingNoteText?.let { setNoteText(it) }

    saveButton.setOnClickListener {
      notePresenter.saveNote(noteInput.text?.toString().orEmpty())
      (activity as? PagerActivity)?.endAyahMode()
    }
    clearButton.setOnClickListener {
      noteInput.setText("")
      notePresenter.removeNote()
    }
    return view
  }

  override fun onStart() {
    super.onStart()
    notePresenter.bind(this)
  }

  override fun onStop() {
    notePresenter.unbind(this)
    super.onStop()
  }

  override fun refreshView() {
    start?.let {
      setVerseReference(it.sura, it.ayah)
      notePresenter.setVerse(it.sura, it.ayah)
    }
  }

  fun bindNote(note: String) {
    if (this::noteInput.isInitialized) {
      setNoteText(note)
    } else {
      pendingNoteText = note
    }
  }

  private fun setNoteText(note: String) {
    if (noteInput.text?.toString() != note) {
      noteInput.setText(note)
      noteInput.setSelection(note.length)
    }
    pendingNoteText = null
  }

  private fun setVerseReference(sura: Int, ayah: Int) {
    if (this::verseReference.isInitialized) {
      verseReference.text = "${QuranUtils.getLocalizedNumber(sura)}:${QuranUtils.getLocalizedNumber(ayah)}"
    }
  }
}
