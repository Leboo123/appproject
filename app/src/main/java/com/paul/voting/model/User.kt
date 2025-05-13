package com.paul.voting.model


class User{
    var fullname: String=""
    var email: String=""
    var pass: String=""
    var userid: String=""
    constructor(
                fullname: String,
                email: String,
                pass: String,
                userid: String
    ){
        this.fullname=fullname
        this.email=email
        this.pass=pass
        this.userid=userid

    }
    constructor()
}