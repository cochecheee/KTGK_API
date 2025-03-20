package com.datingapp.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datingapp.entity.User;
import com.datingapp.model.LoginRequest;
import com.datingapp.model.RegisterRequest;
import com.datingapp.model.RegisterResponse;
import com.datingapp.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    
    // Lưu trữ OTP tạm thời trong Map (email -> OTP)
    private Map<String, String> otpStorage = new HashMap<>();
    
    // Tạo OTP ngẫu nhiên
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Tạo số 6 chữ số
        return String.valueOf(otp);
    }
    
    // Tạo và lưu OTP cho email
    public String createOtp(String email) {
        String otp = generateOtp();
        otpStorage.put(email, otp);
        return otp; // Trả về OTP để hiển thị cho người dùng (có thể qua console hoặc UI)
    }
    
    // Xác thực OTP
    private boolean verifyOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        return storedOtp != null && storedOtp.equals(otp);
    }
    
    // Xóa OTP sau khi sử dụng
    private void clearOtp(String email) {
        otpStorage.remove(email);
    }

    @Override
    public boolean login(LoginRequest req) {
        String email = req.getEmail();
        String password = req.getPassword();
        Optional<User> user = userRepository.findUserByEmail(email);
        if (!user.isPresent()) {
            return false;
        } else {
            if (user.get().getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
	public RegisterResponse requestRegister(RegisterRequest req) {
    	// Tạo OTP trước
        String generatedOtp = createOtp(req.getEmail());
        System.out.println("Your OTP is: " + generatedOtp); // Hiển thị OTP cho người dùng
        String email = req.getEmail();
        String username = req.getUsername();
        String password = req.getPassword();
     
        return RegisterResponse.builder()
        		.email(email)
        		.username(username)
        		.password(password)
        		.otp(generatedOtp)
        		.build();
    }
    
    @Override
    public User register(RegisterResponse req) {
        String email = req.getEmail();
        String username = req.getUsername();
        String password = req.getPassword();
        String otp = req.getOtp();
        
        Optional<User> user = userRepository.findUserByEmail(email);
        
        if (!user.isPresent()) {
            boolean isOtpValid = verifyOtp(email, otp);
            
            if (isOtpValid) {
                User newUser = User.builder()
                    .email(email)
                    .username(username)
                    .password(password) // Nên mã hóa password
                    .build();
                userRepository.save(newUser);
                clearOtp(email); // Xóa OTP sau khi đăng ký thành công
                return newUser;
            } else {
                throw new IllegalArgumentException("Invalid OTP");
            }
        } else {
            throw new IllegalStateException("Email already exists");
        }
    }
}