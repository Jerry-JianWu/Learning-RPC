# Learning-RPC

![image-20250118221230754](https://gitee.com/Jerry-wu-jian/img-load/raw/master/noteImg/202501182212871.png)

RPC调用的执行过程

一次完整的RPC过程如下：

- 在一次RPC调用过程中，客户端首先会将调用的类名、方法名、参数名、参数值等信息，序列化成二进制流

  - 为什么要序列化？
    - 网络传输的数据必须是二进制数据，但调用方请求的出入参数都是对象。
    - 对象是肯定没有方法直接在网络中传输的，系统提前把它转换成可传输的二进制，并且要求转换算法是可逆的。
    - 这个过程一般叫做**序列化**

- 然后客户端将二进制流，通过网络发送给服务端

- 服务端接收到二进制流之后，将它

  反序列化

  ，得到需要调用的类名、方法名、参数名和参数值，在通过动态代理的方式，调用对应的方法得到返回值

  - 服务提供方从TCP通道中收到二进制数据，那如何知道一个请求的数据到哪里结束，是一个什么类型的请求呢？

    - 可以**建立一些“指示牌”，并在上面标明数据包的类型和长度**，这样就可以正确解析数据了。确实可以，并且我们把数据格式的约定内容叫做“协议”。大多数协议会分成两部分，分别是数据头和消息体。数据头一般用于身份识别，包括协议标识、数据大小、请求类型、序列化类型等信息；消息体主要是请求的业务参数信息和扩展属性等。
    - 根据协议格式，服务提供方就可以正确的从二进制数据中分隔出不同的请求来，同时根据请求类型和序列化类型，把二进制的消息体还原成请求对象。这个过程叫做“反序列化”
    - 服务提供方在根据反序列化出来的请求对象找到对应的实现类，完成真正的方法调用，然后把执行结果序列化后，回写到对应的TCP通道里面。调用方获取到应答的数据包之后，再反序列化到应答对象，这样调用方就完成了一次RPC调用

  - 另外，为了不在再让研发人员需要手动写代码去构造请求、调用序列化，并进行网络调用，引入了

    动态代理

    技术，以屏蔽到RPC细节，让使用方只需要关注业务接口，像调用本地一样来调用远程

    - 类似Spring中的API，采用动态代理技术，通过字节码增强对方法进行拦截增强，以便于增加需要的额外处理逻辑。

- 服务端将返回值序列化，再通过网络发送给客户端

- 客户端对结果反序列化之后，就可以得到调用的结果了

![image-20250115211751382](https://gitee.com/Jerry-wu-jian/img-load/raw/master/noteImg/202501152117529.png)

### **注册中心在 RPC 调用中的工作流程**

以下是一个典型的 RPC 调用流程，包含注册中心：

1. **服务端启动**：
   - 服务端启动时，向注册中心注册服务信息（服务名称、地址、端口号等）。
   - 注册中心将服务信息存储在内部的服务注册表中。
2. **客户端调用**：
   - 客户端需要调用某个服务时，先向注册中心查询服务地址。
   - 注册中心返回服务端的地址（如果有多个实例，可以返回一个列表）。
   - 客户端根据地址向服务端发送 RPC 请求。
3. **负载均衡**：
   - 如果注册中心返回多个服务实例，客户端可以使用负载均衡策略选择一个服务实例进行调用。
4. **健康检查**：
   - 注册中心定期检查服务实例的可用性，如果某个实例宕机，会将其从服务注册表中移除。