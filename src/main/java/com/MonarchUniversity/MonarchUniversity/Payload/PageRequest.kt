package com.MonarchUniversity.MonarchUniversity.Payload

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

data class PageRequest(
    private val offset: Int,
    private val limit: Int,
    private val sort: Sort
) : Pageable {

    override fun getPageNumber() = offset / limit

    override fun getPageSize() = limit

    override fun getOffset() = offset.toLong()

    override fun getSort() = sort

    override fun next() =
        PageRequest(offset + pageSize, pageSize, sort)

    private fun previous(): PageRequest =
        PageRequest(offset - pageSize, pageSize, sort)

    override fun previousOrFirst(): Pageable =
        if (hasPrevious()) previous() else first()

    override fun first() =
        PageRequest(0, pageSize, sort)

    override fun withPage(pageNumber: Int) =
        PageRequest(pageNumber * pageSize, pageSize, sort)

    override fun hasPrevious() =
        offset >= limit
}