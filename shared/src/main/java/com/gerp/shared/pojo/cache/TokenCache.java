package com.gerp.shared.pojo.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TypeAlias("TokenCache")
public class TokenCache implements Serializable {
    private String username;
    private List<GrantedAuthority> authorities;


}
