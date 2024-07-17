package org.example.Service;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.example.Model.User;
import org.example.Model.Preferences;
import org.example.Repository.UserRepository;
import org.example.Repository.PreferencesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PreferencesRepository preferencesRepository;

    @Value("${user.service.url}")
    private String userServiceUrl;

    private final DaprClient daprClient = new DaprClientBuilder().build();

    /**
     * Registers a new user.
     * @param user User object to register.
     * @return The registered user.
     */
    public User registerUser(User user) {
        try {
            User savedUser = userRepository.save(user);
            logger.info("User registered successfully: {}", savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves user preferences by user ID.
     * @param userId ID of the user.
     * @return User preferences.
     */
    public Preferences getUserPreferences(String userId) {
        try {
            Preferences preferences = preferencesRepository.findByUserId(userId);
            logger.info("Preferences retrieved for user: {}", userId);
            return preferences;
        } catch (Exception e) {
            logger.error("Error retrieving preferences for user {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    /**
     * Updates user preferences.
     * @param userId ID of the user.
     * @param preferences New preferences to update.
     * @return Updated preferences.
     */
    public Preferences updateUserPreferences(String userId, Preferences preferences) {
        try {
            Preferences existingPreferences = preferencesRepository.findByUserId(userId);
            existingPreferences.setCategories(preferences.getCategories());
            Preferences updatedPreferences = preferencesRepository.save(existingPreferences);
            logger.info("Preferences updated for user: {}", userId);
            return updatedPreferences;
        } catch (Exception e) {
            logger.error("Error updating preferences for user {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    /**
     * Creates new preferences.
     * @param preferences Preferences to create.
     * @return Created preferences.
     */
    public Preferences createPreferences(Preferences preferences) {
        try {
            Preferences savedPreferences = preferencesRepository.save(preferences);
            logger.info("Preferences created successfully for user: {}", preferences.getUserId());
            return savedPreferences;
        } catch (Exception e) {
            logger.error("Error creating preferences: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves user email by user ID.
     * @param userId ID of the user.
     * @return Email address of the user.
     */
    public String getUserEmail(String userId) {
        try {
            return userRepository.findById(userId)
                    .map(user -> {
                        logger.info("Email retrieved for user: {}", userId);
                        return user.getEmail();
                    })
                    .orElse("Email not found");
        } catch (Exception e) {
            logger.error("Error retrieving email for user {}: {}", userId, e.getMessage());
            throw e;
        }
    }
}
