package com.paul.voting.model

class Polls {
    var id: String = ""
    var question: String = ""
    var options: List<String> = emptyList()
    var createdAt: Long = System.currentTimeMillis()
    constructor(question:String, options:List<String>, createdAt: Long, id:String) {
        this.question = question
        this.options = options
        this.createdAt = createdAt

        this.id = id
    }
        constructor()

    }