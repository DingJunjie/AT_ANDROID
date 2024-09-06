package com.bitat.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.ext.cdp
import com.bitat.ext.csp
import com.bitat.ui.component.CommonTopBar
import com.bitat.ui.theme.hintTextColor
import com.bitat.ui.theme.linkTextColor
import com.bitat.viewModel.CancelAgreementViewModel
import com.bitat.viewModel.SignoutViewModel

/**
 *    author : shilu
 *    date   : 2024/9/6  15:30
 *    desc   :
 */

@Composable
fun CancelAgreementPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm: CancelAgreementViewModel = viewModel()

    val singoutVm = viewModelProvider[SignoutViewModel::class]
    val singoutState = singoutVm.state.collectAsState()
    val isAgree = remember { mutableStateOf(false) }
    val pagerState = rememberScrollState()
    Scaffold(topBar = {
        CommonTopBar(title = stringResource(id = R.string.signout_auth_account),
            backFn = { navHostController.popBackStack() },
            padingStatus = true)
    }) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            Column(modifier = Modifier.fillMaxHeight().weight(1f).padding(all = 30.cdp)
                .verticalScroll(pagerState)) {
                Text(modifier = Modifier,
                    text = "用户账号注销须知协议\n" + "尊敬的用户，感谢您使用我们的服务。在您决定注销账户之前，请仔细阅读以下内容。注销账户后，您将无法恢复数据或使用与该账户相关的功能" + "与服务。\n" + "\n" + "1. 注销账户的后果\n" + "1.1 数据删除\n" + "一旦注销账户，您的账户信息将被永久删除，包括但不限于：\n" + "\n" + "个人资料信息（" + "头像、用户名、简介等）\n" + "发表的内容（动态、评论、点赞、收藏等）\n" + "与其他用户的社交关系（好友、关注、粉丝等）\n" + "账户积分、虚拟货币、购买记录等信" + "息\n" + "1.2 不可恢复性\n" + "账户一旦注销，所有与您账户相关的数据将被永久删除，且无法恢复。即便您重新注册相同的手机号或邮箱，也无法找回之前的数据。\n" + "\n" + "2. 注销条件\n" + "2.1 账户清理\n" + "注销账户前，您必须确保：\n" + "\n" + "账户内没有未完成的交易或争议；\n" + "账户内没有未使用的虚拟货币、积分或其他权益；\n" + "账户内无任何未解决的法律或财务纠纷；\n" + "如有任何未支付的款项，您必须先行支付。\n" + "2.2 注销限制\n" + "以下情况将暂时无法注销账户：\n" + "\n" + "账" + "户存在未解决的法律争议、投诉或账户安全问题；\n" + "账户存在资金冻结或余额问题；\n" + "账户正在进行中的服务或交易未完成。\n" + "3. 注销后无法使用的服务\n" + "注销后，您将无法使用与账户绑定的以下服务：\n" + "\n" + "使用该账户登录的应用、游戏或其他关联服务；\n" + "接收账户相关的通知、邮件、或系统消息；\n" + "使用账" + "户关联的优惠券、积分或其他虚拟财产；\n" + "社交关系将被解除，其他用户将无法再访问您的资料或与您互动。\n" + "4. 隐私与数据处理\n" + "4.1 数据删除\n" + "在账户注" + "销后，我们将依据相关法律法规，删除或匿名化处理您的个人数据。但为履行法律义务或解决争议，部分必要数据将可能保留在我们的备份系统中一段时间，直至相关问题妥善处理。\n" + "\n" + "4.2 第三方平台关联\n" + "如果您的账户关联了第三方平台（如 Google、Apple、Facebook 等），请确保您已处理好与第三方平台之间的登录和绑定关系。账户注销后，我们无法再为您提供" + "与第三方账户相关的服务。\n" + "\n" + "5. 账户注销流程\n" + "5.1 申请流程\n" + "如您决定继续注销账户，请按以下步骤操作：\n" + "\n" + "进入“账户设置”页面；\n" + "点击" + "“注销账户”选项；\n" + "阅读并同意《用户账号注销须知协议》；\n" + "提交注销申请。\n" + "5.2 审核与处理\n" + "我们将在收到您的注销申请后进行审核，确保您的账户符合注销条件。" + "如符合条件，账户将在 7 个工作日内 完成注销。如有任何问题，客服将与您联系。\n" + "\n" + "6. 免责声明\n" + "您在注销账户后，我们不再对因账户注销导致的任何数据丢失、社交关系解除或" + "服务不可用承担责任。请您谨慎考虑注销账户的后果。\n" + "\n" + "如有任何问题或需要帮助，请随时通过客服与我们联系。\n" + "\n" + "感谢您对我们的支持！\n" + "\n" + "该协议适用于一般" + "社交应用中的账户注销功能。如果有特定的法律要求或特定功能，需要根据具体业务进行调整。")
            }
            Column(modifier = Modifier.height(150.dp)
               ,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isAgree.value, onCheckedChange = { isCheck ->
                        isAgree.value = isCheck
                    })
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row {
                            Text("我已阅读并同意",
                                modifier = Modifier.align(Alignment.CenterVertically),
                                style = MaterialTheme.typography.bodySmall)
                            TextButton(onClick = {

                            }) {
                                Text(text = "艾特账号注销协议,",
                                    modifier = Modifier.clickable { },
                                    style = MaterialTheme.typography.bodySmall.copy(color = linkTextColor))
                            }
                        }
                        Text("并自愿放弃账号资产和权益",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.bodySmall)
                    }

                }
                Spacer(modifier = Modifier.height(30.cdp))

                Button(modifier = Modifier.fillMaxWidth().height(56.dp).padding(start = 30.cdp, end = 30.cdp), onClick = {
                    vm.cancelAccount(singoutState.value.phone, singoutState.value.captch)
                }) {
                    Text(stringResource(id = R.string.signout_auth_account))
                }

                Spacer(modifier = Modifier.fillMaxSize().height(30.dp))
            }
        }
    }
}