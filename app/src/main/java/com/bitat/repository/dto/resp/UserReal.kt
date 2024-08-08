package  com.bitat.repository.dto.resp

class UserRealDto {
    var userId: Long = 0 //用户id
    var status: Byte = 0 //状态
    var name: String = "" //名字
    var sex: String = "" //性别
    var birthday: String = "" //出生日期
    var idNumber: String = "" // 省份证号
    var frontImage: String = "" //身份证正面
    var backImage: String = "" //身份证反面
    var auditTime: Long = 0//认证时间
}