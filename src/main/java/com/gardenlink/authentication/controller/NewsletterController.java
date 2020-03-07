package com.gardenlink.authentication.controller;

import com.gardenlink.authentication.domain.AuthUser;
import com.gardenlink.authentication.domain.dto.DTOAuthUser;
import com.gardenlink.authentication.domain.dto.DTONewsletter;
import com.gardenlink.authentication.domain.dto.DTOTokenInformation;
import com.gardenlink.authentication.service.AuthTokenService;
import com.gardenlink.authentication.service.NewsletterService;
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

import static com.gardenlink.authentication.Constants.ACCOUNT_CLIENT_NAME;
import static com.gardenlink.authentication.Constants.HIDDEN_VAR;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class NewsletterController {

    final UserService userService;
    final AuthTokenService authTokenService;
    final NewsletterService newsletterService;

    public NewsletterController(UserService userService, AuthTokenService authTokenService, NewsletterService newsletterService) {
        this.userService = userService;
        this.authTokenService=authTokenService;
        this.newsletterService=newsletterService;
    }


    @PostMapping(value = "/newsletters")
    public ResponseEntity<Object> sendNewsletter(HttpServletRequest request, @NotNull @Validated @RequestBody DTONewsletter dtoNewsletter) {
            DTOTokenInformation token = authTokenService.introspect(request.getHeader(HttpHeaders.AUTHORIZATION));
            if (token == null || !token.getEmitter().equals(ACCOUNT_CLIENT_NAME) || Boolean.FALSE.equals(token.getAdmin())) {
                return ResponseEntity.status(403).build();
            }

            newsletterService.sendNewsletter(dtoNewsletter.getContent(), dtoNewsletter.getTitle());
            return ResponseEntity.ok().build();
    }

}