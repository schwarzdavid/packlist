package app.schwarzdavid.packlist.service.user.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserCreateDto(
        String username,
        String email,
        String password
) {
}
