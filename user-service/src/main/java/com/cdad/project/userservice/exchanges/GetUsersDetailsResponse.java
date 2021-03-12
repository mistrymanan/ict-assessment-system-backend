package com.cdad.project.userservice.exchanges;

import com.cdad.project.userservice.dto.UserDetails;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GetUsersDetailsResponse {
    List<UserDetails> usersDetail;
}