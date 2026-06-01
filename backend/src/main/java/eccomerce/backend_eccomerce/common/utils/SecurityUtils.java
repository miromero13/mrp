package eccomerce.backend_eccomerce.common.utils;

import eccomerce.backend_eccomerce.user.entity.UserEntity;
import eccomerce.backend_eccomerce.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SecurityUtils {

    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<String> getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }
        return Optional.ofNullable(authentication.getName());
    }

    public Optional<UserEntity> getCurrentUser() {
        return getCurrentUserEmail().flatMap(userRepository::findByEmail);
    }

    public Optional<UUID> getCurrentEnterpriseId() {
        return getCurrentUser().map(user -> user.enterprise != null ? user.enterprise.getId() : null);
    }
}
