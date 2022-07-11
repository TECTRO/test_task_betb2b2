package ru.tectro.quote_viewer_betb2b.domain.datasources.util

sealed class UpdateEvents<T>(val data: T) {
    class Add<T>(data: T) : UpdateEvents<T>(data)
    class Remove<T>(data: T) : UpdateEvents<T>(data)
    class Update<T>(data: T) : UpdateEvents<T>(data)
}
