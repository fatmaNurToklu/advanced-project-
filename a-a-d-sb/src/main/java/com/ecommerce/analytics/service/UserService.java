package com.ecommerce.analytics.service;

import com.ecommerce.analytics.dto.request.AddressRequest;
import com.ecommerce.analytics.dto.request.UpdateProfileRequest;
import com.ecommerce.analytics.dto.response.AddressResponse;
import com.ecommerce.analytics.dto.response.UserProfileResponse;
import com.ecommerce.analytics.exception.ResourceNotFoundException;
import com.ecommerce.analytics.model.CustomerProfile;
import com.ecommerce.analytics.model.User;
import com.ecommerce.analytics.model.UserAddress;
import com.ecommerce.analytics.repository.CustomerProfileRepository;
import com.ecommerce.analytics.repository.UserAddressRepository;
import com.ecommerce.analytics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final UserAddressRepository userAddressRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String userId) {
        User user = findUser(userId);
        CustomerProfile profile = customerProfileRepository.findByUserUserId(userId).orElse(null);

        UserProfileResponse.UserProfileResponseBuilder builder = UserProfileResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.getGender())
                .role(user.getRole().name())
                .status(user.isStatus())
                .createdAt(user.getCreatedAt());

        if (profile != null) {
            builder.age(profile.getAge())
                    .city(profile.getCity())
                    .membershipType(profile.getMembershipType())
                    .totalSpend(profile.getTotalSpend())
                    .satisfactionLevel(profile.getSatisfactionLevel())
                    .lastLogin(profile.getLastLogin());
        }
        return builder.build();
    }

    @Transactional
    public UserProfileResponse updateProfile(String userId, UpdateProfileRequest request) {
        User user = findUser(userId);
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getGender() != null) user.setGender(request.getGender());
        userRepository.save(user);

        CustomerProfile profile = customerProfileRepository.findByUserUserId(userId)
                .orElse(CustomerProfile.builder().user(user).build());
        if (request.getAge() != null) profile.setAge(request.getAge());
        if (request.getCity() != null) profile.setCity(request.getCity());
        if (request.getMembershipType() != null) profile.setMembershipType(request.getMembershipType());
        profile.setLastLogin(LocalDateTime.now());
        customerProfileRepository.save(profile);

        return getProfile(userId);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(String userId) {
        return userAddressRepository.findByUserUserIdAndIsActiveTrue(userId)
                .stream().map(this::toAddressResponse).collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse addAddress(String userId, AddressRequest request) {
        User user = findUser(userId);

        if (request.isDefault()) {
            userAddressRepository.findByUserUserIdAndIsDefaultTrue(userId)
                    .ifPresent(a -> { a.setDefault(false); userAddressRepository.save(a); });
        }

        UserAddress address = UserAddress.builder()
                .user(user)
                .addressTitle(request.getAddressTitle())
                .addressLine(request.getAddressLine())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .isDefault(request.isDefault())
                .isActive(true)
                .build();
        return toAddressResponse(userAddressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(String userId, String addressId) {
        UserAddress address = userAddressRepository.findByAddressIdAndUserUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + addressId));
        address.setActive(false);
        userAddressRepository.save(address);
    }

    private User findUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private AddressResponse toAddressResponse(UserAddress a) {
        return AddressResponse.builder()
                .addressId(a.getAddressId())
                .addressTitle(a.getAddressTitle())
                .addressLine(a.getAddressLine())
                .city(a.getCity())
                .state(a.getState())
                .country(a.getCountry())
                .postalCode(a.getPostalCode())
                .isDefault(a.isDefault())
                .isActive(a.isActive())
                .build();
    }
}
