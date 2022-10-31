package com.example.randomuselessfacts

import com.example.randomuselessfacts.model.Fact

object DummyData {

    fun getDummyFact(id: String? = null) = Fact(
        id ?: "162gvf321761d",
        "en",
        "",
        "djtech.net",
        "http:\\/\\/www.djtech.net\\/humor\\/useless_facts.htm",
        "Months that begin on a Sunday will always have a `Friday the 13th`.",
    )

}