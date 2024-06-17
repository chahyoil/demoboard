package org.example.demoboard.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Role{
    
    /// 주의!!! Role -> RoleName 내부에 정의된 값 이외의 값은 DB + Service 단에 유일함
    public enum RoleName {
        ROLE_ANONYMOUS, // 인증되지 않은 사용자
        ROLE_BRONZE, // 초기 회원 가입 시 기본 권한
        ROLE_SILVER,
        ROLE_GOLD,
        ROLE_DIAMOND,
        ROLE_MASTER,
        ROLE_ADMIN // 관리자 권한을 의미
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName name;

    public String getName() {
        return String.valueOf(name);
    }

    public void setName(String name) {
        this.name = RoleName.valueOf(name);
    }
}