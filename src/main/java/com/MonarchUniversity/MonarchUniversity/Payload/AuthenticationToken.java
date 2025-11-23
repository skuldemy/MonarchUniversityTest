package com.MonarchUniversity.MonarchUniversity.Payload;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationToken {
    private String token;
    private List<String> roles;
}
