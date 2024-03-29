package com.example.taxibooking.service;

import com.example.taxibooking.contract.request.LoginRequest;
import com.example.taxibooking.contract.request.SignUpRequest;
import com.example.taxibooking.contract.request.UpdateAccountRequest;
import com.example.taxibooking.contract.response.LoginResponse;
import com.example.taxibooking.contract.response.SignUpResponse;
import com.example.taxibooking.contract.response.UpdateAccountResponse;
import com.example.taxibooking.exception.EntityAlreadyExistsException;
import com.example.taxibooking.exception.EntityNotFoundException;
import com.example.taxibooking.exception.InvalidUserException;
import com.example.taxibooking.model.User;
import com.example.taxibooking.repository.UserRepository;
import com.example.taxibooking.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public SignUpResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EntityAlreadyExistsException(request.getEmail());
        }
        User user =
                User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .accountBalance(0D)
                        .build();
        userRepository.save(user);
        return modelMapper.map(user, SignUpResponse.class);
    }

    public LoginResponse login(LoginRequest request) {
        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() -> new InvalidUserException("Login"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidUserException("Login");
        }
        String jwtToken = jwtService.generateToken(user);
        return LoginResponse.builder().token(jwtToken).build();
    }

    public UpdateAccountResponse updateBalance(Long user_id, UpdateAccountRequest request) {
        User user =
                userRepository
                        .findById(user_id)
                        .orElseThrow(() -> new EntityNotFoundException("User", user_id));
        user =
                User.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .accountBalance(user.getAccountBalance() + request.getAccountBalance())
                        .build();
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UpdateAccountResponse.class);
    }
}
