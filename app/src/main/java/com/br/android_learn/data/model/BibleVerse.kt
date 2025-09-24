package com.br.android_learn.data.model

data class BibleVerse(
    val reference: String,
    val verses: List<Verse>,
    val text: String
)

data class Verse(
    val book_id: String,
    val book_name: String,
    val chapter: Int,
    val verse: Int,
    val text: String
)


