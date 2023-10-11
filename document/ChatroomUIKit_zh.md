# ChatroomUIKit

*English | [英文](ChatroomUIKit.md)*

## 目录结构
```
┌─ Example                  	// Demo代码集成目录
│  ├─ ChatroomListActivity  			// 主要提供 ChatroomUIKit 的房间列表页面
│  └─ ChatroomSettingActivity   		// 主要提供 ChatroomUIKit 的房间设置页面
├─ ChatroomService           	// ChatroomUIKit协议模块
│  ├─ model                      		// ChatroomUIKit所用到的实体对象（用户、房间信息、配置信息）
│  ├─ service                   		// ChatroomUIKit所用到的协议和协议实现（房间协议、用户协议、礼物协议）
│  └─                     		// 
└─ ChatroomUIKit            	// 包含UI和数据绑定处理
   ├─ binder                    	// 把UI Compose和Service关联起来的业务绑定模块
   ├─ compose                   	// 基础UI Compose组件(底部输入框、消息列表、礼物列表、底部抽屉)支持明暗主题换肤
   ├─ theme                     	// 资源文件 提供项目需要的颜色、字体、主题、渐变、尺寸等属性
   ├─ viewModel                 	// 数据处理
   ├─ widget                    	// 小单元组件
   └─ ui                        	// 整体组装UI模块 提供完整的聊天室场景UI
```