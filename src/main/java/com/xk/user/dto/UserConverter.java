package com.xk.user.dto;

import com.xk.user.entity.UserEntity;

public class UserConverter {

    public static UserDTO fromUserEntity(UserEntity user) {
        return new UserDTO(user.getId().toString(), user.getUsername(), null, user.getUserType());
    }

}
