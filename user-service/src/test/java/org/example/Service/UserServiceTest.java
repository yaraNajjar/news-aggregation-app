package org.example.Service;

import org.example.Model.User;
import org.example.Model.Preferences;
import org.example.Repository.UserRepository;
import org.example.Repository.PreferencesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PreferencesRepository preferencesRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser() {
        User user = new User();
        user.setId("123");
        when(userRepository.save(user)).thenReturn(user);

        userService.registerUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testGetUserPreferences() {
        String userId = "123";
        Preferences preferences = new Preferences();
        preferences.setUserId(userId);
        when(preferencesRepository.findByUserId(userId)).thenReturn(preferences);

        userService.getUserPreferences(userId);

        verify(preferencesRepository, times(1)).findByUserId(userId);
    }

    @Test
    public void testUpdateUserPreferences() {
        String userId = "123";
        Preferences preferences = new Preferences();
        preferences.setUserId(userId);
        preferences.setCategories(Collections.singletonList("Business, Sports"));

        Preferences existingPreferences = new Preferences();
        existingPreferences.setUserId(userId);
        existingPreferences.setCategories(Collections.singletonList("Tech, Science"));

        when(preferencesRepository.findByUserId(userId)).thenReturn(existingPreferences);
        when(preferencesRepository.save(existingPreferences)).thenReturn(preferences);

        userService.updateUserPreferences(userId, preferences);

        verify(preferencesRepository, times(1)).findByUserId(userId);
        verify(preferencesRepository, times(1)).save(existingPreferences);
    }

    @Test
    public void testCreatePreferences() {
        Preferences preferences = new Preferences();
        preferences.setUserId("123");

        when(preferencesRepository.save(preferences)).thenReturn(preferences);

        userService.createPreferences(preferences);

        verify(preferencesRepository, times(1)).save(preferences);
    }

    @Test
    public void testGetUserEmail() {
        String userId = "123";
        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        userService.getUserEmail(userId);

        verify(userRepository, times(1)).findById(userId);
    }
}
