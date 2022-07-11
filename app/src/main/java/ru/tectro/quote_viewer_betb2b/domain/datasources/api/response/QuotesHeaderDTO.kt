package ru.tectro.quote_viewer_betb2b.domain.datasources.api.response

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "valCurs", strict = false)
class QuotesHeaderDTO(
    @field:ElementList(entry = "Valute", inline = true)
    var quotes: MutableList<QuoteDTO> = mutableListOf(),

    @field:Attribute(name = "Date")
    var date: String = "",

    @field:Attribute(name = "name")
    var owner: String = ""
)

