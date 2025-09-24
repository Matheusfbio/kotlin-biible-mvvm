package com.br.android_learn.repository

import com.br.android_learn.data.api.BibleApiService
import com.br.android_learn.data.model.BibleVerse

class BibleRepository(private val api: BibleApiService) {

    suspend fun getVerse(passage: String): BibleVerse {
        return api.getVerse(passage)
    }
}
