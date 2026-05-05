# ChatBoxL
- 使用ListAdapter+DiffUtil+RecyclerView+RxJava+MVVM架构仿写了AI ChatBox
##基本功能
- 使用RecyclerView实现消息列表，并利用DiffUtil实现了性能优化，利用Retrofit+RxJava实现了网络请求
  
- 利用TextInputLayout获得了一个相对优美的输入框

  <img width="1080" height="2400" alt="Screenshot_20260505_142906" src="https://github.com/user-attachments/assets/85aa5d2a-8700-4d65-88ec-87c849b852e3" />

- 利用WindowInsetsCompat处理了软键盘以及状态栏和导航栏的避让，实现了滚动呼出/隐藏软键盘的效果
  
  <img width="400" height="889" alt="Screen_recording_20260505_143022" src="https://github.com/user-attachments/assets/e5e592e9-4684-4814-9264-5aed99b096b4" />

- 利用popupMenu实现消息长按呼出菜单

  
  <img width="400" height="889" alt="Screen_recording_20260505_143134" src="https://github.com/user-attachments/assets/ca1e425b-8791-4c6c-94e8-d9f3f2c60748" />


##写的还可以的地方/不足之处
关于WindowInsets的那一部分，写的相对还可以。
其他地方就很烂，网络请求也写的不好，不是很理解RxJava和Deeepseek文档里的一些字段。

##遇到的问题
设置softInputMode为adjustPan时，长按菜单位置不准确；设置为adjustResize时，软键盘会遮挡输入框。
好像Resize只对可以滚动的view有用，于是后来查了一下就用了ViewCompat手动管理。
