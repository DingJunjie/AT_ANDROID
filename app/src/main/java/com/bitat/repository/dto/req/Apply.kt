package  com.bitat.repository.dto.req

import kotlinx.serialization.Serializable


@Serializable
class CreateApplyDto {
    var protocolId: Long = 0 //同意法律协议id
    var kind: Byte = 0 //用户申请类型  //1撰稿人,2播客人,3音乐人
    var aptitude: Array<String> = emptyArray() //用户其他资质,放在七牛云info捅
}

@Serializable
class CreateRusticApplyDto {
    var protocolId: Long = 0 //同意法律协议id
    var aptitude: Array<String> = emptyArray()//用户其他资质,放在七牛云info捅
    var standCity: String = ""////代表城市
}