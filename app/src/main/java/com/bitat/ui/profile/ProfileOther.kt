package com.bitat.ui.profile

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// 定义 Address 类
data class Address(var city: String, var street: String)

// 定义 User 类，其中包含 Address 引用属性
data class User(
    var name: String = "www",
    var age: Int = 123,
    var address : List<Address> = listOf(Address("hello", "world"))
)


class VM : ViewModel() {
    private val _state = MutableStateFlow(User())
    val state: StateFlow<User> get() = _state.asStateFlow()

    fun updateUser() {
        _state.update {
            val oAddress = it.address;
            oAddress[0].city = "guiyang"
            it.copy(address = oAddress)
        }
    }

    fun updateInfo() {
        _state.update {
            it.copy(age = _state.value.age + 1)
        }
    }
}

@Composable
fun ProfileOtherPage(id: Int) {
    // 使用 MutableState 包裹 User 实例
//    var user by remember {
//        mutableStateOf(
//            User(
//                "Alice",
//                25,
//                listOf(Address("New York", "5th Avenue"))
//            )
//        )
//    }

    val viewModel: VM = viewModel()
    val state by viewModel.state.collectAsState();

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Name: ${state.name}")
        Text(text = "Age: ${state.age}")
        Text(text = "City: ${state.address[0].city}")
        Text(text = "Street: ${state.address[0].street}")

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            // 更新 User 的 age 属性
            viewModel.updateInfo()
        }) {
            Text(text = "Increase Age")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            // 更新 User 的 Address 引用属性的 city 属性
            viewModel.updateUser()
        }) {
            Text(text = "Move to Los Angeles")
        }
    }
}