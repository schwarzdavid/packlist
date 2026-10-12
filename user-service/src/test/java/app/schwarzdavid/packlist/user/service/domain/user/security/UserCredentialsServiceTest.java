package app.schwarzdavid.packlist.user.service.domain.user.security;

import app.schwarzdavid.packlist.user.service.domain.user.UserRepository;
import app.schwarzdavid.packlist.user.service.domain.user.entity.UserEntity;
import app.schwarzdavid.packlist.user.service.fixture.UserFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserCredentialsServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserCredentialsService userCredentialsService;

    @Test
    void shouldCreateUserPrincipalByUsername() {
        var entity = UserEntity.builder()
                .userId(UserFixtures.USER_ID)
                .username(UserFixtures.USERNAME)
                .email(UserFixtures.EMAIL)
                .password(UserFixtures.HASHED_PASSWORD)
                .build();

        when(userRepository.findByUsernameOrEmailIgnoreCase(UserFixtures.USERNAME, UserFixtures.USERNAME)).thenReturn(Optional.of(entity));

        var principal = userCredentialsService.loadUserByUsername(UserFixtures.USERNAME);

        assertInstanceOf(UserPrincipal.class, principal);

        var user = (UserPrincipal) principal;

        assertEquals(UserFixtures.USER_ID, user.getUserId());
        assertEquals(UserFixtures.USERNAME, user.getUsername());
        assertEquals(UserFixtures.EMAIL, user.getEmail());
        assertEquals(UserFixtures.HASHED_PASSWORD, user.getPassword());
    }

    @Test
    void shouldCreateUserPrincipalByEmail() {
        var entity = UserEntity.builder()
                .userId(UserFixtures.USER_ID)
                .username(UserFixtures.USERNAME)
                .email(UserFixtures.EMAIL)
                .password(UserFixtures.HASHED_PASSWORD)
                .build();

        when(userRepository.findByUsernameOrEmailIgnoreCase(UserFixtures.EMAIL, UserFixtures.EMAIL)).thenReturn(Optional.of(entity));

        var principal = userCredentialsService.loadUserByUsername(UserFixtures.EMAIL);

        assertInstanceOf(UserPrincipal.class, principal);

        var user = (UserPrincipal) principal;

        assertEquals(UserFixtures.USER_ID, user.getUserId());
        assertEquals(UserFixtures.USERNAME, user.getUsername());
        assertEquals(UserFixtures.EMAIL, user.getEmail());
        assertEquals(UserFixtures.HASHED_PASSWORD, user.getPassword());
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findByUsernameOrEmailIgnoreCase(anyString(), anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userCredentialsService.loadUserByUsername(UserFixtures.EMAIL));
    }
}
