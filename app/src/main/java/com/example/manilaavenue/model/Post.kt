package com.example.manilaavenue.model

import android.os.Parcel
import android.os.Parcelable

data class Post(
    var title: String = "",
    var description: String = "",
    var imageUrls: MutableList<String> = ArrayList(),
    var timestamp: Long = 0L
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",

        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: ArrayList(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeStringList(imageUrls)
        parcel.writeLong(timestamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}
