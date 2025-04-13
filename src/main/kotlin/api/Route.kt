package org.rikardv.api

class Route {
    val id: Int
    val start: String
    val end: String

    constructor(id: Int, start: String, end: String) {
        this.id = id
        this.start = start
        this.end = end
    }

}
