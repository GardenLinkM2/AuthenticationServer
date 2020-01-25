package com.gardenlink.authentication.controller;

import com.gardenlink.authentication.domain.AuthUser;
import com.gardenlink.authentication.domain.dto.DTOAuthUser;
import com.gardenlink.authentication.domain.dto.DTOTokenInformation;
import com.gardenlink.authentication.service.AuthTokenService;
import com.gardenlink.authentication.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

@RestController
public class UserController {

    final UserService userService;
    final AuthTokenService authTokenService;

    public UserController(UserService userService, AuthTokenService authTokenService) {
        this.userService = userService;
        this.authTokenService=authTokenService;
    }

    @GetMapping("/users")
    public ResponseEntity<Page<AuthUser>> getUsers(Pageable page, HttpServletRequest request) {
        DTOTokenInformation token = authTokenService.introspect(request.getHeader(HttpHeaders.AUTHORIZATION));

        Page<AuthUser> authUsers =userService.getUsers(page);

        if(token==null || !token.isAdmin || !token.emitter.equals("account")){
            authUsers.forEach(e -> { e.setEmail("hidden"); e.setPhone("hidden");});
        }

        return ResponseEntity.ok().body(userService.getUsers(page));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<AuthUser> getUserInfo(@PathVariable("id") String id, HttpServletRequest request) {
        DTOTokenInformation token = authTokenService.introspect(request.getHeader(HttpHeaders.AUTHORIZATION));

        AuthUser authUser = userService.getById(id);

        if(authUser==null){
            return ResponseEntity.notFound().build();
        }

        if (token == null || !token.emitter.equals("account") || (!token.isAdmin && !token.uuid.equals(id))) {
            authUser.setEmail("hidden");
            authUser.setPhone("hidden");
        }
        return ResponseEntity.ok(authUser);

    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id, HttpServletRequest request) {
        DTOTokenInformation token = authTokenService.introspect(request.getHeader(HttpHeaders.AUTHORIZATION));

        if (token == null || !token.emitter.equals("account") || ((!token.isAdmin && !token.uuid.equals(id)))) {
            return ResponseEntity.status(403).build();
        }

        userService.delete(id);

        if(token.uuid.equals(id)){
            authTokenService.repudiateToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        }

        return ResponseEntity.ok().build();
    }


    @PutMapping("/users/{id}")
    public ResponseEntity<AuthUser> updateUserInfo(@PathVariable("id") String id, @NotNull @Validated @RequestBody DTOAuthUser dtoAuthUser, HttpServletRequest request) {
        DTOTokenInformation token = authTokenService.introspect(request.getHeader(HttpHeaders.AUTHORIZATION));

        if (token == null || !token.emitter.equals("account") ||((!token.isAdmin && !token.uuid.equals(id)))) {
            return ResponseEntity.status(403).build();
        }

        AuthUser authUser = userService.updateUser(id, dtoAuthUser);
        return (authUser == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok().build();
    }


    @PostMapping(value = "/users")
    public ResponseEntity<Object> createUser(@NotNull @Validated @RequestBody DTOAuthUser dtoAuthUser, UriComponentsBuilder ucb) {
        AuthUser authUser = userService.create(dtoAuthUser);
        return (authUser == null) ? ResponseEntity.badRequest().build() : ResponseEntity.created(ucb.path("/users/" + authUser.getId()).buildAndExpand().toUri()).build();
    }




    //LostPassword

    @GetMapping("/lostpassword/{email}")
    public ResponseEntity<Void> lostPassword(@PathVariable("email") String email){
        Boolean bool = userService.sendPasswordResetMail(email);
        return (bool)?ResponseEntity.ok().build():ResponseEntity.badRequest().build();
    }

    @PostMapping("/newpassword/{token}")
    public ResponseEntity<Void> newPassword(@PathVariable("token") String token, @NotNull @Validated @RequestBody DTOAuthUser dtoAuthUser){
        if(token==null || token.isEmpty() || dtoAuthUser.password==null || dtoAuthUser.password.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        Boolean bool = userService.newPassword(token, dtoAuthUser);
        return (bool)?ResponseEntity.ok().build():ResponseEntity.badRequest().build();
    }

}