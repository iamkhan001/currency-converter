package com.imran.currencyconverter.data

data class Currency(val name: String, val rate: Double) {

    var id = 0

    constructor(id: Int, name: String, rate: Double): this(name, rate) {
        this.id = id
    }

}