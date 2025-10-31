package com.example.manilaavenue.model

import android.os.Parcel
import android.os.Parcelable

data class ItemsModel(
    var itemID: Int = 0,
    var title: String = "",
    var location: String = "",
    var description: String = "",
    var picUrl: ArrayList<String> = ArrayList(),
    var size: ArrayList<String> = ArrayList(),
    var rating: Double = 0.0,
    var favoriteIconId: String = "",
    var category: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.createStringArrayList() ?: ArrayList(),
        parcel.createStringArrayList() ?: ArrayList(),
        parcel.readDouble(),
        parcel.readString().toString(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(itemID)
        parcel.writeString(title)
        parcel.writeString(location)
        parcel.writeString(description)
        parcel.writeStringList(picUrl)
        parcel.writeStringList(size)
        parcel.writeDouble(rating)
        parcel.writeString(favoriteIconId)
        parcel.writeString(category)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemsModel> {
        override fun createFromParcel(parcel: Parcel): ItemsModel {
            return ItemsModel(parcel)
        }

        override fun newArray(size: Int): Array<ItemsModel?> {
            return arrayOfNulls(size)
        }
    }
}
