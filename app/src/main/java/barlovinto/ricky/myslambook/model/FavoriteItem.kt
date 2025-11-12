package barlovinto.ricky.myslambook.model

import android.os.Parcel
import android.os.Parcelable

data class FavoriteItem(
    val category: String,
    val name: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FavoriteItem> {
        override fun createFromParcel(parcel: Parcel): FavoriteItem {
            return FavoriteItem(parcel)
        }

        override fun newArray(size: Int): Array<FavoriteItem?> {
            return arrayOfNulls(size)
        }
    }
}