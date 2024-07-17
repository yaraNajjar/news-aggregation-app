package org.example.Controller;


import org.example.Model.User;
import org.example.Model.Preferences;
import org.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @PostMapping("/create-preferences")
    public ResponseEntity<Preferences> registerUser(@RequestBody Preferences preferences) {
        return ResponseEntity.ok(userService.createPreferences(preferences));
    }

    @GetMapping("/preferences/{userId}")
    public ResponseEntity<Preferences> getUserPreferences(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserPreferences(userId));
    }

    @GetMapping("/email/{userId}")
    public String getUserEmail(@PathVariable String userId) {
        return userService.getUserEmail(userId);
    }

    @PutMapping("/preferences/{userId}")
    public ResponseEntity<Preferences> updateUserPreferences(@PathVariable String userId, @RequestBody Preferences preferences) {
        return ResponseEntity.ok(userService.updateUserPreferences(userId, preferences));
    }
}