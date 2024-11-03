package com.example.chorequest.service

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase


class FireAnalyticsService {
    private val analytics: FirebaseAnalytics = Firebase.analytics

    // Define id and name (replace with your actual values)
    private val id = "your_item_id"
    private val name = "your_item_name"

    init {
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
            param(FirebaseAnalytics.Param.ITEM_ID, id)
            param(FirebaseAnalytics.Param.ITEM_NAME, name)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        }
    }
}