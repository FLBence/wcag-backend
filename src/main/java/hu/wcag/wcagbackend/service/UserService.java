package hu.wcag.wcagbackend.service;

import hu.wcag.wcagbackend.model.User;
import hu.wcag.wcagbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User saveUser(User user){
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Ez az email cím már regisztrálva van: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("A felhasználó nem található ezzel az ID-val: " + id));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("A felhasználó nem található ezzel az email címmel: " + email));
    }

    @Transactional
    public void deleteUserById(Long id){
        if (!userRepository.existsById(id))
            throw new RuntimeException("Nem törölhető, a felhasználó nem található ezzel az ID-val: " + id);
        userRepository.deleteById(id);
    }
}
