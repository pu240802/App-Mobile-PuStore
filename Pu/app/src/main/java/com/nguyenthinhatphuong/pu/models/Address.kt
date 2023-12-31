package com.nguyenthinhatphuong.pu.models

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import androidx.versionedparcelable.VersionedParcelize

@VersionedParcelize
data class Address(
    val user_id: String? = "",
    val name: String? ="",
    val mobileNumber: String? = "",
    val address: String? ="",
    val zipcode: String? ="",
    val additionalNote: String? ="",

    val type: String? ="",
    val otherDetails: String? ="",
    var id: String? ="",


    ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(user_id)
        parcel.writeString(name)
        parcel.writeString(mobileNumber)
        parcel.writeString(address)
        parcel.writeString(zipcode)
        parcel.writeString(additionalNote)
        parcel.writeString(type)
        parcel.writeString(otherDetails)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }
}