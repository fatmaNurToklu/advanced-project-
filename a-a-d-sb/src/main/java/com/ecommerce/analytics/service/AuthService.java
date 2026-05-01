package com.ecommerce.analytics.service;

import com.ecommerce.analytics.dto.request.LoginRequest;
import com.ecommerce.analytics.dto.request.RegisterRequest;
import com.ecommerce.analytics.dto.response.AuthResponse;
import com.ecommerce.analytics.exception.ResourceNotFoundException;
import com.ecommerce.analytics.model.*;
import com.ecommerce.analytics.model.enums.RoleType;
import com.ecommerce.analytics.model.enums.StoreStatus;
import com.ecommerce.analytics.repository.*;
import com.ecommerce.analytics.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${app.jwt.expiration-ms}")
    private Long jwtExpirationMs;

    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final StoreRepository storeRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .gender(request.getGender())
                .role(request.getRole())
                .status(true)
                .build();
        userRepository.save(user);

        if (request.getRole() == RoleType.CUSTOMER) {
            CustomerProfile profile = CustomerProfile.builder().user(user).build();
            customerProfileRepository.save(profile);
        }

        if (request.getRole() == RoleType.CORPORATE && request.getStoreName() != null) {
            Store store = Store.builder()
                    .owner(user)
                    .storeName(request.getStoreName())
                    .status(StoreStatus.Open)
                    .build();
            storeRepository.save(store);
        }

        String accessToken = jwtTokenProvider.generateTokenFromUserId(user.getUserId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUserId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs)
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String accessToken = jwtTokenProvider.generateToken(authentication);
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUserId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs)
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse refreshToken(String token) {
        RefreshToken refreshToken = refreshTokenService.findByToken(token);
        refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();
        String accessToken = jwtTokenProvider.generateTokenFromUserId(user.getUserId());
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs)
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.deleteByToken(refreshToken);
    }
}
