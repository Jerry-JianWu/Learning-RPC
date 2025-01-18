package part3.Server.provider;

import part3.Server.serviceRegister.Impl.ZKServiceRegister;
import part3.Server.serviceRegister.ServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/* 该类提供了一种简单的方式来注册和获取服务，它将服务对象和接口名称进行映射，从而实现服务的本地注册和动态获取
 * */
public class ServiceProvider {
    // 集合中存放服务的实例，接口的全限定名(String type),接口对应的实现类实例(Object type);
    private Map<String, Object> interfaceProvider; // key是服务接口全限定名，即接口的完整路径名，value是服务实例，实现了该接口的对象

    // 为 interfaceProvider 字段分配一个新的HashMap 实例
    public ServiceProvider() {
        this.interfaceProvider = new HashMap<>();
    }
    private int port;
    private String host;
    //注册服务类
    private ServiceRegister serviceRegister;

    public ServiceProvider(String host,int port){
        //需要传入服务端自身的网络地址
        this.host=host;
        this.port=port;
        this.interfaceProvider=new HashMap<>();
        this.serviceRegister=new ZKServiceRegister();
    }

    // 本地注册服务
    // 该方法用于将一个服务实例注册到 interfaceProvider 中， 将服务对象与其实现的接口关联起来。
    public void provideServiceInterface(Object service) {
        // 接收一个服务实例
        String serviceName = service.getClass().getName(); // 获取服务对象的完整类名
        Class<?>[] interfaceName = service.getClass().getInterfaces(); // 通过反射获取服务对象实现的所有接口
        for (Class<?> clazz : interfaceName) {
            // 本机的映射表
            interfaceProvider.put(clazz.getName(), service);
            // 在注册中心注册服务
            serviceRegister.register(clazz.getName(), new InetSocketAddress(host, port));
            /*// 遍历 service 实现的所有接口
            interfaceProvider.put(clazz.getName(), service); // 将接口的全限定名和对应的服务的实例添加到map中*/
        }
    }

    // 获取服务实例
    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }


}
