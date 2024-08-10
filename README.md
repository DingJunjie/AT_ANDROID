## 项目结构

1. config - 配置文件
2. ext - 扩展方法
3. repository - 仓库
    1. common - 通用仓库内容
    2. consts - 常量
    3. dto - 传输类型对象
    4. repo - HTTP + DB
        1. httpReq - HTTP请求
        2. SQL - 数据库
4. router - 路由
5. state - 状态
6. store - 内存数据
7. style - 样式
8. ui - 页面
9. utils - 工具类
10. viewModel - 视图方法

## 通用工具

### 页面通用

1. Toast
   可以在compose和ViewModel中调用，调用后弹出通用toast（切换线程后不可用）
```
ToastModel("发布成功！", ToastModel.Type.Error).showToast()
ToastModel("发布失败", ToastModel.Type.Info).showToast()
ToastModel("警告", ToastModel.Type.Warning).showToast()
ToastModel("normal", ToastModel.Type.Normal).showToast()
```