package eccomerce.backend_eccomerce.common.dto;

public class AuthResponseDto {
    public String jwtToken;

    public AuthResponseDto(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}

