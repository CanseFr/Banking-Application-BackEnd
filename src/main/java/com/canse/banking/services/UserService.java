package com.canse.banking.services;

import com.canse.banking.dto.AuthenticationRequest;
import com.canse.banking.dto.AuthenticationResponse;
import com.canse.banking.dto.UserDto;

public interface UserService extends AbstractService<UserDto> {

    Integer validateAccount(Integer id);
    Integer invalidateAccount(Integer id);
    AuthenticationResponse register (UserDto user);
    AuthenticationResponse authenticate(AuthenticationRequest request);

}
