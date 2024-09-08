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

    fun getReportTypeList(): Array<ConstBean> {
        val list: Array<ConstBean> = arrayOf(
            ConstBean("诈骗信息", REASON_FRAUD_INFORMATION, false),
            ConstBean("色情低俗", REASON_PORNOGRAPHIC_VULGAR, false),
            ConstBean("涉企侵权", REASON_ENTERPRISE_TORT, false),
            ConstBean("抄袭洗稿", REASON_PLAGIARISM_MANU_WASH, false),
            ConstBean("未成年不宜观看", REASON_MINORS_NOT_WATCH, false),

            ConstBean("引人不适", REASON_CAUSING_DISCOMFORT, false),
            ConstBean("时政不实信息", REASON_POLITICS_NOT, false),
            ConstBean("诱导关注点赞", REASON_INDUCE_FOLLOW_AGREE, false),
            ConstBean("侵权投诉", REASON_TORT_COMPLAINT, false),
            ConstBean("疑似其他", REASON_SUSPECT_OTHER, false),

            ConstBean("违法违规", REASON_VIOLATION_LAW, false),
            ConstBean("虚假不实", REASON_FALSE_AND_UNTRUE, false),
            ConstBean("危险行为", REASON_DANGEROUS_ACT, false),
            ConstBean("谩骂攻击", REASON_ABUSIVE_ATTACK, false),
            ConstBean("侵害未成年人", REASON_TORT_MINORS, false),

            ConstBean("未成年人不当行为", REASON_MINOR_MISCONDUCT, false),
            ConstBean("违规售卖", REASON_ILLEGAL_SALE, false),
            ConstBean("疑似虐待动物", REASON_CRUELTY_ANIMAL, false),
            ConstBean("其他类型", REASON_OTHER, false)

        )
        return list
    }
    
}