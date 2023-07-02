package com.greenfoxacademy.hta.controllers;

import com.greenfoxacademy.hta.dtos.*;
import com.greenfoxacademy.hta.exceptions.HtaException;
import com.greenfoxacademy.hta.exceptions.UserNotFoundException;
import com.greenfoxacademy.hta.services.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController {
    private final IUserService iUserService ;

    @GetMapping("/hello")
    public boolean sayHello (Authentication authentication) {
        if (!authentication.getName().isEmpty()) {
            return true;
        } else {
            return false;
        }

    }

    @GetMapping("/hello-user")
    public String sayHelloUser () {
        return "Hello User" ;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register (@RequestBody RegisterDTO registerDto) {
        try {
            ResponseCookie jwtHTATokenCookie = ResponseCookie.from(
                            "jwtHTATokenCookie",
                            iUserService.register(registerDto))
                    .httpOnly(true).path("/")
                    .maxAge(60*60*10)
                    .secure(true)
                    .sameSite("None")
                    .build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtHTATokenCookie.toString()).body(null);
        } catch (HtaException exception) {
            return ResponseEntity.status(exception.getStatus()).body(exception.getMessage());
        }
    }

    @GetMapping("/update-user-profile")
    public ResponseEntity<?> updateUserProfilePageData(Authentication authentication) {
        UpdateProfileDTO updateDTO = iUserService.getUserProfileData(authentication);
        System.out.println(updateDTO.getUsername());
        return ResponseEntity.status(200).body(updateDTO);
    }

    @PutMapping("/update-user-profile")
    public ResponseEntity<?> updateUserProfile (@RequestBody UpdateProfileDTO updateProfileDTO, Authentication authentication) {
        try {
            return ResponseEntity.ok(iUserService.updateUserProfile(updateProfileDTO, authentication));
        } catch (HtaException exception) {
            return ResponseEntity.status(exception.getStatus()).body(exception.getMessage());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody LoginDTO loginDto) throws UserNotFoundException {
        ResponseCookie jwtHTATokenCookie = ResponseCookie.from(
                        "jwtHTATokenCookie",
                        iUserService.authenticate(loginDto))
                .httpOnly(true).path("/")
                .maxAge(60*60*10)
                .secure(true)
                .sameSite("None")
                .build();
        System.out.println("This is your cookie: " + jwtHTATokenCookie);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtHTATokenCookie.toString()).body(null);
    }

    @PostMapping("/pw-update")
    public ResponseEntity<?> changePassword(@RequestBody NewPWDTO newPWDTO, Authentication authentication) {
        try {
            return ResponseEntity.ok(iUserService.userChangePassword(newPWDTO.getNewPassword(), authentication));
        } catch (UserNotFoundException exception) {
            return ResponseEntity.status(exception.getStatus()).body(exception.getMessage());
        }
    }

    @GetMapping("/get-cityname-list")
    public ResponseEntity<?> getCityList() {
            return ResponseEntity.ok(iUserService.getCityNameList());
    }
}
