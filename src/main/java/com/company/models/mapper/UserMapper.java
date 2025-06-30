package com.company.models.mapper;

import com.company.models.dto.request.RegistrationRequest;
import com.company.models.entity.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(RegistrationRequest request);

}
