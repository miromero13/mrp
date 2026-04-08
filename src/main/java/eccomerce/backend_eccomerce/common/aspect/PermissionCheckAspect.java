package eccomerce.backend_eccomerce.common.aspect;

import eccomerce.backend_eccomerce.common.annotation.RequirePermission;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionCheckAspect {

    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("No estás autenticado");
        }

        String requiredPermission = requirePermission.value();
        boolean hasPermission = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(requiredPermission));

        if (!hasPermission) {
            throw new SecurityException("No tienes permiso para realizar esta acción: " + requiredPermission);
        }
    }
}
