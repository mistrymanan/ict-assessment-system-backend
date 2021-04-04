package com.cdad.project.gradingservice.serviceclient.userservice.exchanges;


import com.cdad.project.gradingservice.serviceclient.userservice.dtos.UserDetail;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GetUsersDetailsResponse {
    List<UserDetail> usersDetail;
}