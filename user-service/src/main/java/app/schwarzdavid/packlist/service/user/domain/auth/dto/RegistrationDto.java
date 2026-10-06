package app.schwarzdavid.packlist.service.user.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RegistrationDto(
        String username,
        String email,
        String password
) {
}
