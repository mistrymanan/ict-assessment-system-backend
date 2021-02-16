package com.cdad.project.userservice.exchanges;

import com.cdad.project.userservice.dto.UsersDetail;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
public class GetUsersDetailsResponse {
    HashMap<String, UsersDetail> usersDetail;
}