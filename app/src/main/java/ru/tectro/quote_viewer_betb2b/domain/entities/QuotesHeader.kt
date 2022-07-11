package ru.tectro.quote_viewer_betb2b.domain.entities

import org.joda.time.LocalDate

data class QuotesHeader(
    val date: LocalDate,
    val owner: String,
    val quotes: List<Quote>
) {
    var filteredQuotes: List<Quote> = quotes
        private set

    fun filterQuotes(query: String) {
        filteredQuotes = if (query.isBlank()) quotes else quotes.filter {
            it.isoCharCode.contains(query, true) ||
                    it.isoNumCode.toString().contains(query, true) ||
                    it.nominal.toString().contains(query, true) ||
                    it.title.contains(query, true) ||
                    it.value.toString().contains(query, true)
        }
    }

    fun sortQuotes(sortedField: SortedField, sortedOrder: SortedOrder) {
        filteredQuotes = when (sortedOrder) {
            SortedOrder.ASC -> when (sortedField) {
                SortedField.IsoNumCode -> filteredQuotes.sortedBy { quote -> quote.isoNumCode }
                SortedField.IsoCharCode -> filteredQuotes.sortedBy { quote -> quote.isoCharCode }
                SortedField.Nominal -> filteredQuotes.sortedBy { quote -> quote.nominal }
                SortedField.Title -> filteredQuotes.sortedBy { quote -> quote.title }
                SortedField.Value -> filteredQuotes.sortedBy { quote -> quote.value }
                SortedField.IsFavourite -> filteredQuotes.sortedBy { quote -> quote.isFavourite }
            }

            SortedOrder.DESC -> when (sortedField) {
                SortedField.IsoNumCode -> filteredQuotes.sortedByDescending { quote -> quote.isoNumCode }
                SortedField.IsoCharCode -> filteredQuotes.sortedByDescending { quote -> quote.isoCharCode }
                SortedField.Nominal -> filteredQuotes.sortedByDescending { quote -> quote.nominal }
                SortedField.Title -> filteredQuotes.sortedByDescending { quote -> quote.title }
                SortedField.Value -> filteredQuotes.sortedByDescending { quote -> quote.value }
                SortedField.IsFavourite -> filteredQuotes.sortedByDescending { quote -> quote.isFavourite }
            }
        }

    }
}