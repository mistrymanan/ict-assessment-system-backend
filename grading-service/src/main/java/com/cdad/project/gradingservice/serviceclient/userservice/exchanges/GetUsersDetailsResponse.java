package com.cdad.project.classroomservice.serviceclient.userservice.exchanges;

import com.cdad.project.classroomservice.serviceclient.userservice.dtos.UserDetail;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
public class GetUsersDetailsResponse {
    List<UserDetail> usersDetail;
}