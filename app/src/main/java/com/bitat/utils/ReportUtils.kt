package com.bitat.utils

import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import com.bitat.repository.consts.REASON_ABUSIVE_ATTACK
import com.bitat.repository.consts.REASON_CAUSING_DISCOMFORT
import com.bitat.repository.consts.REASON_CRUELTY_ANIMAL
import com.bitat.repository.consts.REASON_DANGEROUS_ACT
import com.bitat.repository.consts.REASON_ENTERPRISE_TORT
import com.bitat.repository.consts.REASON_FALSE_AND_UNTRUE
import com.bitat.repository.consts.REASON_FRAUD_INFORMATION
import com.bitat.repository.consts.REASON_ILLEGAL_SALE
import com.bitat.repository.consts.REASON_INDUCE_FOLLOW_AGREE
import com.bitat.repository.consts.REASON_MINORS_NOT_WATCH
import com.bitat.repository.consts.REASON_MINOR_MISCONDUCT
import com.bitat.repository.consts.REASON_OTHER
import com.bitat.repository.consts.REASON_PLAGIARISM_MANU_WASH
import com.bitat.repository.consts.REASON_POLITICS_NOT
import com.bitat.repository.consts.REASON_PORNOGRAPHIC_VULGAR
import com.bitat.repository.consts.REASON_SUSPECT_OTHER
import com.bitat.repository.consts.REASON_TORT_COMPLAINT
import com.bitat.repository.consts.REASON_TORT_MINORS
import com.bitat.repository.consts.REASON_VIOLATION_LAW

/**
 *    author : shilu
 *    date   : 2024/8/19  18:03
 *    desc   :
 */

object ReportUtils {
    fun getReportTypeList(): ArrayMap<Int, String> {

        val map: ArrayMap<Int, String> = arrayMapOf(REASON_FRAUD_INFORMATION to "诈骗信息",
            REASON_PORNOGRAPHIC_VULGAR to "色情低俗",
            REASON_ENTERPRISE_TORT to "涉企侵权",
            REASON_PLAGIARISM_MANU_WASH to "抄袭洗稿",
            REASON_MINORS_NOT_WATCH to "未成年不宜观看",
            REASON_CAUSING_DISCOMFORT to "引人不适",
            REASON_POLITICS_NOT to "时政不实信息",
            REASON_INDUCE_FOLLOW_AGREE to "诱导关注点赞",
            REASON_TORT_COMPLAINT to "侵权投诉",
            REASON_SUSPECT_OTHER to "疑似其他",
            REASON_VIOLATION_LAW to "违法违规",
            REASON_FALSE_AND_UNTRUE to "虚假不实",
            REASON_DANGEROUS_ACT to "危险行为",
            REASON_ABUSIVE_ATTACK to "谩骂攻击",
            REASON_TORT_MINORS to "侵害未成年人",
            REASON_MINOR_MISCONDUCT to "未成年人不当行为",
            REASON_ILLEGAL_SALE to "违规售卖",
            REASON_CRUELTY_ANIMAL to "疑似虐待动物",
            REASON_OTHER to "其他类型")
        return map
    }
}