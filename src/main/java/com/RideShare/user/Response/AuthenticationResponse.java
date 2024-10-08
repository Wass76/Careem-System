package com.RideShare.user.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private Integer id;
    private String firstName;
    private String lastName;
    private String phone;
    private String _username;
    private String token;

}
