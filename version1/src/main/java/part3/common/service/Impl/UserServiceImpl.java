package part3.common.service.Impl;

import part3.common.pojo.User;
import part3.common.service.UserService;

import java.util.Random;
import java.util.UUID;

public class UserServiceImpl implements UserService {

    @Override
    public User getUserByUserId(Integer id) {
        System.out.println("客户端查询了 "+id+" 的用户");
        // 模拟从数据库中取用户的行为
        Random random = new Random();
        // random.nextBoolean():随机生成true或false 表示用户的性别
        // UUID.randomUUID() 生成一个全局唯一的字符串，作为随机用户名
        // User.builder() 使用Lombok的Builder模式构造一个新的用户对象
        User user = User.builder().userName(UUID.randomUUID().toString())
                .id(id)
                .sex(random.nextBoolean())
                .build();
        return user;

    }

    @Override
    public Integer insertUserId(User user) {

        System.out.println("插入数据成功 " + user.getUserName());
        return user.getId();
    }
}
