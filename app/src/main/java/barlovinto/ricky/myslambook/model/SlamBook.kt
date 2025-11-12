package barlovinto.ricky.myslambook.model

import android.os.Parcel
import android.os.Parcelable
import barlovinto.ricky.myslambook.R

data class SlamBook(
    var profilePic: Int = R.drawable.profile_icon,
    var firstName: String? = null,
    var lastName: String? = null,
    var nickName: String? = null,
    var friendCallMe: String? = null,
    var likeToCallMe: String? = null,
    var birthDate: String? = null,
    var gender: String? = null,
    var status: String? = null,
    var email: String? = null,
    var contactNo: String? = null,
    var address: String? = null,
    var favorites: ArrayList<FavoriteItem> = ArrayList(),
    var defineLove: String? = null,
    var defineFriendship: String? = null,
    var memorableExperience: String? = null,
    var describeMe: String? = null,
    var adviceForMe: String? = null,
    var rateMe: Int? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(FavoriteItem.CREATOR) ?: arrayListOf(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(profilePic)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(nickName)
        parcel.writeString(friendCallMe)
        parcel.writeString(likeToCallMe)
        parcel.writeString(birthDate)
        parcel.writeString(gender)
        parcel.writeString(status)
        parcel.writeString(email)
        parcel.writeString(contactNo)
        parcel.writeString(address)
        parcel.writeTypedList(favorites)
        parcel.writeString(defineLove)
        parcel.writeString(defineFriendship)
        parcel.writeString(memorableExperience)
        parcel.writeString(describeMe)
        parcel.writeString(adviceForMe)
        parcel.writeValue(rateMe)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SlamBook> {
        override fun createFromParcel(parcel: Parcel): SlamBook {
            return SlamBook(parcel)
        }

        override fun newArray(size: Int): Array<SlamBook?> {
            return arrayOfNulls(size)
        }
    }
}