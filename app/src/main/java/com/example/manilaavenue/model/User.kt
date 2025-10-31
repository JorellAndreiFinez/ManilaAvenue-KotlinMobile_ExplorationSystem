package com.example.manilaavenue.model

class User {

    private var username: String = ""
    private var fullname: String = ""
    private var bio: String = ""
    private var image: String = ""
    private var coverimage: String = ""
    private var uid: String = ""

    // Default constructor
    constructor()

    // Parameterized constructor
    constructor(username: String, fullname: String, bio: String, image: String, coverimage: String, uid: String) {
        this.username = username
        this.fullname = fullname
        this.bio = bio
        this.image = image
        this.coverimage = coverimage
        this.uid = uid
    }

    // Getter and setter for username
    fun getUserName(): String {
        return username
    }

    fun setUserName(username: String) {
        this.username = username
    }

    // Getter and setter for fullname
    fun getFullName(): String {
        return fullname
    }

    fun setFullName(fullname: String) {
        this.fullname = fullname
    }

    // Getter and setter for bio
    fun getBio(): String {
        return bio
    }

    fun setBio(bio: String) {
        this.bio = bio
    }

    // Getter and setter for image
    fun getImage(): String {
        return image
    }

    fun setCoverImage(image: String) {
        this.image = image
    }

    // Getter and setter for image
    fun getCoverImage(): String {
        return image
    }

    fun setImage(image: String) {
        this.image = image
    }

    // Getter and setter for uid
    fun getUID(): String {
        return uid
    }

    fun setUID(uid: String) {
        this.uid = uid
    }
}
