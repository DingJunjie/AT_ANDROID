package com.bitat.utils

import com.bitat.repository.dto.resp.BlogTagDto

object PublishUtils {
    val atRegex: String = """\\^@{id:nickname}^\\""";
    private const val TAG_REGEX: String = """\\^#{id:content}^\\"""

    fun convertContentWithTag(content: String, tags: List<BlogTagDto>): String {
        var newContent = content;
        for (tag in tags) {
            val regex = """#${tag.name}""".toRegex()
            val replaceRegex =
                TAG_REGEX.replace("id", tag.id.toString()).replace("content", tag.name)
            newContent = newContent.replace(regex, replaceRegex)
        }

        return newContent;
    }
}