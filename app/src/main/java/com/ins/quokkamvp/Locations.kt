package com.ins.quokkamvp

val Locations = listOf(
    Location(
        "home",
        "Home",
        10.804556051565548,
        106.69819686733187,
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
    ),
    Location(
        "id1",
        "8 Phan dang luu",
        10.803212285761292,
        106.69561282532035,
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
    ),
    Location(
        "id2",
        "Nha sach Van Lang",
        10.802894254913667,
        106.69379406251471,
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
    ),
    Location(
        "id3",
        "FPT shop",
        10.803399172321734,
        106.69218262696396,
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
    ),
)

data class Location(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val url: String,
)
