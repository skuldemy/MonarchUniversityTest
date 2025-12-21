package com.MonarchUniversity.MonarchUniversity.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	private Long id;
	private String username;
	private String password;
	private String position;

    public UserDto(Long id, String username, String position) {
        this.id = id;
        this.username = username;
        this.position = position;
    }
}
