package com.tgplayer.app.domain.model

enum class TrackSortOrder(val label: String) {
    DATE_DESC("Newest"),
    DATE_ASC("Oldest"),
    TITLE("Title A–Z"),
    ARTIST("Artist A–Z"),
    DURATION("Duration")
}

enum class RepeatMode { OFF, ALL, ONE }
