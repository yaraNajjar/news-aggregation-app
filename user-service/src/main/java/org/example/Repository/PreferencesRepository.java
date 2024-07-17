package org.example.Repository;

import org.example.Model.Preferences;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PreferencesRepository extends MongoRepository<Preferences, String> {
    Preferences findByUserId(String userId);
}