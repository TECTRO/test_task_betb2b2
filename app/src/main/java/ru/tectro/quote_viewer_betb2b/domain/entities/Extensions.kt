package ru.tectro.quote_viewer_betb2b.domain.entities

fun List<Quote>.filterQuotes(filterQuery: String) =
    if (filterQuery.isBlank()) this else filter {
        it.isoCharCode.contains(filterQuery, true) ||
                it.isoNumCode.toString().contains(filterQuery, true) ||
                it.nominal.toString().contains(filterQuery, true) ||
                it.title.contains(filterQuery, true) ||
                it.value.toString().contains(filterQuery, true)
    }

fun List<Quote>.sortQuotes(sortedField: SortedField, sortedOrder: SortedOrder) =
    when (sortedOrder) {
        SortedOrder.ASC -> when (sortedField) {
            SortedField.IsoNumCode -> sortedBy { quote -> quote.isoNumCode }
            SortedField.IsoCharCode -> sortedBy { quote -> quote.isoCharCode }
            SortedField.Nominal -> sortedBy { quote -> quote.nominal }
            SortedField.Title -> sortedBy { quote -> quote.title }
            SortedField.Value -> sortedBy { quote -> quote.value }
            SortedField.IsFavourite -> sortedBy { quote -> quote.isFavourite }
        }

        SortedOrder.DESC -> when (sortedField) {
            SortedField.IsoNumCode -> sortedByDescending { quote -> quote.isoNumCode }
            SortedField.IsoCharCode -> sortedByDescending { quote -> quote.isoCharCode }
            SortedField.Nominal -> sortedByDescending { quote -> quote.nominal }
            SortedField.Title -> sortedByDescending { quote -> quote.title }
            SortedField.Value -> sortedByDescending { quote -> quote.value }
            SortedField.IsFavourite -> sortedByDescending { quote -> quote.isFavourite }
        }
    }