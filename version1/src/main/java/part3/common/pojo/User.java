package part3.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable { // 实现Serializable接口，表示该类可以序列化
    private Integer id;
    private String userName;
    private Boolean sex;
}
