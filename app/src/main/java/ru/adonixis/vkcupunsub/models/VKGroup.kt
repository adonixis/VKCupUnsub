package ru.adonixis.vkcupunsub.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

data class VKGroup(
    val id: Int = 0,
    var name: String = "",
    var screenName: String = "",
    val isClosed: Int = 0,
    var deactivated: String = "",
    var type: String = "",
    var photo50: String = "",
    var photo100: String = "",
    var photo200: String = "",
    var description: String = "",
    var membersCount: Int = 0,
    var isChecked: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(screenName)
        parcel.writeInt(isClosed)
        parcel.writeString(deactivated)
        parcel.writeString(type)
        parcel.writeString(photo50)
        parcel.writeString(photo100)
        parcel.writeString(photo200)
        parcel.writeString(description)
        parcel.writeInt(membersCount)
        parcel.writeByte(if (isChecked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VKGroup> {
        override fun createFromParcel(parcel: Parcel): VKGroup {
            return VKGroup(parcel)
        }

        override fun newArray(size: Int): Array<VKGroup?> {
            return arrayOfNulls(size)
        }

        fun parse(json: JSONObject) = VKGroup(
                id = json.optInt("id", 0),
                name = json.optString("name", ""),
                screenName = json.optString("screen_name", ""),
                isClosed = json.optInt("is_closed", 0),
                deactivated = json.optString("deactivated", ""),
                type = json.optString("type", ""),
                photo50 = json.optString("photo_50", ""),
                photo100 = json.optString("photo_100", ""),
                photo200 = json.optString("photo_200", ""),
                description = json.optString("description", ""),
                membersCount = json.optInt("members_count", 0)
        )
    }
}
