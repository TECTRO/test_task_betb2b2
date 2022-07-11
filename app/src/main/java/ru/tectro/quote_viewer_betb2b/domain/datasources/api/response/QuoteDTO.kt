package ru.tectro.quote_viewer_betb2b.domain.datasources.api.response

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Valute")
class QuoteDTO(
    @field:Attribute(name = "ID")
    var id: String = "",

    @field:Element(name = "NumCode")
    var isoNumCode: Int = 0,

    @field:Element(name = "CharCode")
    var isoCharCode: String = "",

    @field:Element(name = "Nominal")
    var nominal: Int = 0,

    @field:Element(name = "Name")
    var title: String = "",

    @field:Element(name = "Value")
    var value: String = ""
)