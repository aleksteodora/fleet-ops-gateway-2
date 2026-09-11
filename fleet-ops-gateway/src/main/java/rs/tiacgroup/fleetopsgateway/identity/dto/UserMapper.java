package rs.tiacgroup.fleetopsgateway.identity.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.UserResponse;
import rs.tiacgroup.fleetopsgateway.identity.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "companyName", source = "company.name")
    UserResponse toResponse(User user);
}