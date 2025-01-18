package part3.Client.serviceCenter;

import java.net.InetSocketAddress;

/*InetSocketAddress 是 Java 中的一个类，属于 java.net 包。它表示了一个网络地址（包含 IP 地址和端口号），通常用于在网络中标识一个计算机的端口。  */
public interface ServiceCenter {
    InetSocketAddress serviceDiscovery(String serviceName);
}
