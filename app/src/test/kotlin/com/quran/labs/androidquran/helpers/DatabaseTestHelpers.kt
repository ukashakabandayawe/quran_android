package com.quran.labs.androidquran.helpers

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.quran.labs.androidquran.BookmarksDatabase
import com.quran.labs.androidquran.database.BookmarksDBAdapter
import com.quran.mobile.bookmark.Bookmarks
import com.quran.mobile.bookmark.Last_pages
import com.quran.mobile.bookmark.Notes

fun inMemoryBookmarksAdapter(): BookmarksDBAdapter {
  val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  BookmarksDatabase.Schema.create(driver)
  val database = BookmarksDatabase(
    driver,
    Bookmarks.Adapter(IntColumnAdapter, IntColumnAdapter, IntColumnAdapter),
    Last_pages.Adapter(IntColumnAdapter),
    Notes.Adapter(IntColumnAdapter, IntColumnAdapter)
  )
  return BookmarksDBAdapter(database)
}
