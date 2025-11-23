package com.MonarchUniversity.MonarchUniversity.Payload;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
