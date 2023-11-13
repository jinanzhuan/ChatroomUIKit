# ChatroomUIKit

*English | [英文](ChatroomUIKit.md)*

# [Sample Demo](https://github.com/apex-wang/ChatroomUIKit#sample-demo)

In this project, there is a best practice demonstration project in the `Example` folder for you to build your own business capabilities.

To experience functions of the ChatroomUIKit, you can scan the following QR code to try a demo.

[![SampleDemo](https://github.com/apex-wang/ChatroomUIKit/raw/main/Documentation/demo.png)](https://github.com/apex-wang/ChatroomUIKit/blob/main/Documentation/demo.png).

# [Chatroom UIKit Guide](https://github.com/apex-wang/ChatroomUIKit#chatroom-uikit-guide)

## [Introduction](https://github.com/apex-wang/ChatroomUIKit#introduction)

This guide presents an overview and usage examples of the ChatroomUIKit framework in Android development, as well as describes various components and features of this UIKit, enabling developers to have a good understanding of the UIKit and make effective use of it.

## [Table of Contents](https://github.com/apex-wang/ChatroomUIKit#table-of-contents)

- [Requirements](https://github.com/apex-wang/ChatroomUIKit#requirements)
- [Installation](https://github.com/apex-wang/ChatroomUIKit#installation)
- [Documentation](https://github.com/apex-wang/ChatroomUIKit#documentation)
- [Structure](https://github.com/apex-wang/ChatroomUIKit#structure)
- [QuickStart](https://github.com/apex-wang/ChatroomUIKit#quickStart)
- [Precautions](https://github.com/apex-wang/ChatroomUIKit#precautions)
- [AdvancedUsage](https://github.com/apex-wang/ChatroomUIKit#advancedusage)
- [Customize](https://github.com/apex-wang/ChatroomUIKit#customize)
- [BusinessFlowchart](https://github.com/apex-wang/ChatroomUIKit#businessflowchart)
- [ApiSequenceDiagram](https://github.com/apex-wang/ChatroomUIKit#apisequencediagram)
- [DesignGuidelines](https://github.com/apex-wang/ChatroomUIKit#designguidelines)
- [Contributing](https://github.com/apex-wang/ChatroomUIKit#contributing)
- [License](https://github.com/apex-wang/ChatroomUIKit#license)

# [Requirements](https://github.com/apex-wang/ChatroomUIKit#requirements)

- Jetpack Compose The minimum support for Android API 21, which is version 5.0
- Android Studio 4.0+
- Must use kotlin language

# [Installation](https://github.com/apex-wang/ChatroomUIKit#installation)

You can use build.gradle to rely on the ChatroomUIKit library as a dependency for app projects.

## [Local_module_dependencies](https://github.com/apex-wang/ChatroomUIKit#Local_module_dependencies)

1. Open your project in Android Studio.

2. Choose **File** > **import Module**.

3. Search for **ChatroomUIKit** and select it.

## [CocoaPods](https://github.com/apex-wang/ChatroomUIKit#cocoapods)

```
implementation 'ChatroomUIKit'
```

# [Structure](https://github.com/apex-wang/ChatroomUIKit#structure)

### [ChatroomUIKit Basic Components](https://github.com/apex-wang/ChatroomUIKit#chatroomuikit-basic-components)

## 目录结构
```
┌─ Example                        // SampleDemo directory
│  ├─ ChatroomListActivity              // Mainly providing room list Activity
│  ├─ ChatroomActivity                  // display ChatroomUIKit chatroom Activity
│  ├─ compose                           // SampleDemo 
│  ├─ http                              // Encapsulated network requests for interaction with app services
│  └─ SplashActivity                    // Program Launch Page
├─ ChatroomService                // ChatroomUIKit Protocol module
│  ├─ model                              // The entity objects used by ChatroomUIKit (user, room information, configuration information)
│  ├─ service                            // The protocols and protocol implementations used by ChatroomUIKit (room protocol, user protocol, gift protocol)
│  │    └─ Protocol                        
│  │         ├─ GiftService              // Gift sending and receiving channel.
│  │         ├─ UserService              // Component for user login and user attribute update.
│  │         └─ ChatroomService          // Component for implementing the protocol for chat room management, including joining and leaving the chat room and sending and receiving messages.
│  └─ ChatroomUIKit                      // ChatroomUIKit initialization class.
└─ ChatroomUIKit            
       ├─ compose                   	// UI Compose组件(底部输入框、消息列表、礼物列表、底部抽屉)支持明暗主题换肤
       ├─ theme                     	// Resource files provide properties such as colors, fonts, themes, gradients, and sizes required for the project
       ├─ viewModel                 	// data processing
       ├─ widget                    	// input widget
       └─ ui                        	// search activity
```

# [Documentation](https://github.com/apex-wang/ChatroomUIKit#documentation)

## [Document](https://github.com/apex-wang/ChatroomUIKit/tree/main/Documentation/ChatroomUIKit.doccarchive)

You can open the `ChatroomUIKit.doccarchive` file in Xcode to view files in it or deploy this file to your homepage.

Also, you can right-click the file to show the package contents and copy all files inside to a folder. Then drag this folder to the `terminal` app and run the following command to deploy it on the local IP address.

```
python3 -m http.server 8080
```

After deployment, you can visit `http://yourlocalhost:8080/documentation/chatroomuikit` in your browser, where `yourlocalhost` is your local IP address. Alternatively, you can deploy this folder on an external network address.

## [Appearance](https://github.com/apex-wang/ChatroomUIKit/tree/main/Documentation/Appearance.md).

Detailed descriptions of available items in the `UI` component.

# [QuickStart](https://github.com/apex-wang/ChatroomUIKit#quickstart)

This guide provides several usage examples for different ChatroomUIKit components. Refer to the `Examples` folder for detailed code snippets and projects showing various use cases.

Please refer to the following steps to run the Android platform application in Android Studio

* First download the demo to the local location
* Then configure the CHATROOM_APP_KEY and REQUEST_HOST in the local.properties folder in the root directory
* Run demo

### [Step 1: Initialize ChatroomUIKit](https://github.com/apex-wang/ChatroomUIKit#step-1-initialize-chatroomuikit)

``` Kotlin
class ChatroomApplication : Application() {
    override fun onCreate() {
    
        val chatroomUIKitOptions = ChatroomUIKitOptions(
            uiOptions = UiOptions(
                targetLanguageList = listOf(GlobalConfig.targetLanguage.code),
                useGiftsInList = false,
            )
        )
        
        ChatroomUIKitClient.getInstance().setUp(
            applicationContext = this,
            options = chatroomUIKitOptions,
            appKey = BuildConfig.CHATROOM_APP_KEY
        )
    }
}
```

### [Step 2: Login](https://github.com/apex-wang/ChatroomUIKit#step-2-login)

```
// Log in to the ChatroomUIKit with the user information of the current user object that conforms to the `UserInfoProtocol` protocol.
// The token needs to be obtained from your app server. You can also log in with a temporary token generated on the Agora Console.
// To generate a user and a temporary user token on the Agora Console, see https://docs.agora.io/en/agora-chat/get-started/enable?platform=ios#manage-users-and-generate-tokens.
ChatroomUIKitClient.getInstance().login(with userId: "user id", token: "token", completion: <#T##(ChatError?) -> Void#>)
```

### [Step 3: Create chat room](https://github.com/apex-wang/ChatroomUIKit#step-3-create-chat-room-view)

```
// 1. Get a chat room list and join a chat room. Alternatively, create a chat room on the Agora Console.
// Choose ProjectManager > Operation Manager > Chat Room and click Create Chat Room and set parameters in the displayed dialog box to create a chat room. Get the chat room ID to pass it in to the following `launchRoomView` method.
// 2. Load ComposeChatroom with setContent in activity. ComposeChatroom is a fully packaged chatroom scenario component that we have packaged. 
// 3. Set the parameters required for ComposeChatroom
// 4. Add users to the chat room on the Console.
// Choose ProjectManager > Operation Manager > Chat Room. Select View Chat Room Members in the Action column of a chat room and add users to the chat room in the displayed dialog box.  
```

[![CreateChatroom](https://github.com/apex-wang/ChatroomUIKit/raw/main/Documentation/CreateChatroom.png)](https://github.com/apex-wang/ChatroomUIKit/blob/main/Documentation/CreateChatroom.png).

Please refer to the next chapter for transparent transmission of events.

# [AdvancedUsage](https://github.com/apex-wang/ChatroomUIKit#advancedusage)

Here are three examples of advanced usage.


### [1.Login](https://github.com/apex-wang/ChatroomUIKit#1login)

``` Kotlin
class YourAppUser: UserInfoProtocol {
    var userId: String = "your application user id"
            
    var nickName: String = "you user nick name"
            
    var avatarURL: String = "you user avatar url"
            
    var gender: Int = 1
            
    var identity: String =  "you user level symbol url"
            
}
// Use the user information of the current user object that conforms to the UserInfoProtocol protocol to log in to ChatroomUIKit.
// You need to get a user token from your app server. Alternatively, you can use a temporary token. To generate a temporary toke, visit https://docs.agora.io/en/agora-chat/get-started/enable?platform=ios#generate-a-user-token.
ChatroomUIKitClient.getInstance().login(YourAppUser, token, onSuccess = {}, onError = {code,error ->})
```


### [2.Initializing the chat room compose](https://github.com/apex-wang/ChatroomUIKit#2initializing-the-chat-room-view)

``` Kotlin
// 1. Get a chat room list and join a chat room. Alternatively, create a chat room on the Agora Console.
// 2. Load ComposeChatroom with setContent in activity.
    
    val chatroomUIKitOptions = ChatroomUIKitOptions(
            chatOptions = ChatSDKOptions(),
            uiOptions = UiOptions(
                targetLanguageList = listOf(GlobalConfig.targetLanguage.code),
                useGiftsInList = false,
            )
        )
    ChatroomUIKitClient.getInstance().setUp(applicationContext: Context,appKey:String,options: ChatroomUIKitOptions = ChatroomUIKitOptions())        

```

### [3.Listening to ChatroomUIKit events and errors](https://github.com/apex-wang/ChatroomUIKit#3listening-to-chatroomuikit-events-and-errors)

You can call the `registerRoomResultListener` method to listen for ChatroomUIKit events and errors.

```
ChatroomUIKitClient.getInstance().registerRoomResultListener(this)
```

# [Customization](https://github.com/apex-wang/ChatroomUIKit#customization)

### [1.Switch original or custom theme](https://github.com/apex-wang/ChatroomUIKit#3switch-original-or-custom-theme)

- Switch to the light or dark theme that comes with the ChatroomUIKit.

```
ChatroomUIKitClient.getInstance().setCurrentTheme(isDarkTheme)
```

- Switch to a custom theme.

```
/**
How to customize a theme?

To customize a theme, you need to define the hue values of the following five theme colors by reference to the theme color of the design document.

All colors in ChatroomUIKit are defined with the HSLA color model that is a way of representing colors using hue, saturation, lightness, and alpha. 

H (Hue): Hue, the basic attribute of color, is a degree on the color wheel from 0 to 360. 0 is red, 120 is green, and 240 is blue.

S (Saturation): Saturation is the intensity and purity of a color. The higher the saturation is, the brighter the color is; the lower the saturation is, the closer the color gets to gray. Saturation is represented by a percentage value, ranging from 0% to 100%. 0% means a shade of gray, and 100% is the full color.

L (Lightness): Lightness is the brightness or darkness of a color. The higher the brightness is, the brighter the color is; the lower the brightness is, the darker the color is. Lightness is represented by a percentage value, ranging from 0% to 100%. 0% indicates a black color and 100% will result in a white color.

A (Alpha): Alpha is the transparency of a color. The value 1 means fully opaque and 0 is fully transparent.

By adjusting the values of individual components of the HSLA model, you can achieve precise color control.
 */
Appearance.primaryHue = 191/360.0
Appearance.secondaryHue = 210/360.0
Appearance.errorHue = 189/360.0
Appearance.neutralHue = 191/360.0
Appearance.neutralSpecialHue = 199/360.0
Theme.switchTheme(style: .custom)
```

Note that custom themes and built-in themes are mutually exclusive. 

# [BusinessFlowchart](https://github.com/apex-wang/ChatroomUIKit#businessflowchart)

The following figure presents the entire logic of business requests and callbacks.

![Overall flow diagram of business logic](https://github.com/apex-wang/ChatroomUIKit/raw/main/Documentation/BusinessFlowchart.png)

# [ApiSequenceDiagram](https://github.com/apex-wang/ChatroomUIKit#apisequencediagram)

The following figure is the best-practice API calling sequence diagram in the `Example` project.

![APIUML](https://github.com/apex-wang/ChatroomUIKit/raw/main/Documentation/Api.png)

# [DesignGuidelines](https://github.com/apex-wang/ChatroomUIKit#designguidelines)

For any questions about design guidelines and details, you can add comments to the Figma design draft and mention our designer Stevie Jiang.

See the [UI design drawing](https://www.figma.com/file/OX2dUdilAKHahAh9VwX8aI/Streamuikit?node-id=137%3A38589&mode=dev).

See the [UI design guidelines](https://www.figma.com/file/OX2dUdilAKHahAh9VwX8aI/Streamuikit?node-id=137)

# [Contributing](https://github.com/apex-wang/ChatroomUIKit#contributing)

Contributions and feedback are welcome! For any issues or improvement suggestions, you can open an issue or submit a pull request.

## [Author](https://github.com/apex-wang/ChatroomUIKit#author)

apex-wang, [1746807718@qq.com](mailto:1746807718@qq.com)

## [License](https://github.com/apex-wang/ChatroomUIKit#license)

ChatroomUIKit is available under the MIT license. See the LICENSE file for more information.
