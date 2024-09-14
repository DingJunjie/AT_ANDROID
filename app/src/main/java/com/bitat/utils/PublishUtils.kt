package com.bitat.utils

import com.bitat.repository.dto.resp.BlogTagDto
import com.bitat.repository.dto.resp.UserBase1Dto

object PublishUtils {
    private const val AT_REGEX: String = """\\^@{id:nickname}^\\""";
     const val TAG_REGEX: String = """\\^#{id:content}^\\"""
    const val TAG_REGEX_SPLIT: String = """\\\^#\{\d+:\p{IsHan}+\}\^\\"""

    //value.split("""\\\^#\{\d+:\p{IsHan}+\}\^\\""".toRegex())

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

    fun covertContentWithAt(content: String, ats: List<UserBase1Dto>): String {
        var newContent = content
        for (user in ats) {
            val regex = """@${user.nickname}""".toRegex()
            val replaceRegex =
                AT_REGEX.replace("id", user.id.toString()).replace("nickname", user.nickname)
            newContent = newContent.replace(regex, replaceRegex)
        }

        return newContent
    }
}