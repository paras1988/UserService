package com.patti.services;

import com.patti.dtos.UserDto;
import com.patti.models.JWTObject;
import com.patti.models.Session;
import com.patti.models.SessionStatus;
import com.patti.models.User;
import com.patti.repositories.SessionRepository;
import com.patti.repositories.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private SecretKey key;
    private MacAlgorithm alg;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.alg = Jwts.SIG.HS256; //or HS384 or HS256
        this.key = alg.key().build();
    }
    public UserDto signup(String email, String password) {
        User user  = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password)); // We should store the encrypted password in the DB for a user.

        User savedUser = userRepository.save(user);
        return UserDto.from(savedUser);
    }

    public ResponseEntity<UserDto> login(String email, String password) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            return null;
        }
        User user = optionalUser.get();
        if(!bCryptPasswordEncoder.matches(password,user.getPassword())){
            return null;
        }
        // Create a test key suitable for the desired HMAC-SHA algorithm:


        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("email", user.getEmail());
        //jsonMap.put("roles", List.of(user.getRoles()));
        jsonMap.put("createdAt", new Date());
        jsonMap.put("expiryAt", DateUtils.addDays(new Date(), 30));

        //byte[] content = message.getBytes(StandardCharsets.UTF_8);

        // Create the compact JWS:
        //String jws = Jwts.builder().content(content, "text/plain").signWith(key, alg).compact();
        String jws = Jwts.builder()
                .claims(jsonMap)
                .signWith(key, alg)
                .expiration(DateUtils.addDays(new Date(), 30))
                .compact();

        // Parse the compact JWS:
       /* String tt = "eyJhbGciOiJIUzI1NiJ9.eyJjcmVhdGVkQXQiOjE3MDY2OTM5NDQwMTAsImV4cGlyeUF0IjoxNzA5Mjg1OTQ0MDEwLCJlbWFpbCI6InBwIn0.1sTchOzJZVVfQDHMs1tHAWEaoUPxvDs3RvHOtk5oOOE";
        Object obj = Jwts.parser().verifyWith(key).build().parseSignedClaims(jws).getPayload();
       */ //content =
        //assert message.equals(new String(content, StandardCharsets.UTF_8));

        Session session =  new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(jws);
        session.setUser(user);
        sessionRepository.save(session);

        MultiValueMapAdapter<String,String> multiValueMapAdapter = new MultiValueMapAdapter(new HashMap<>());
        multiValueMapAdapter.add(HttpHeaders.SET_COOKIE,"auth-token:" + jws);

        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        ResponseEntity<UserDto> response = new ResponseEntity<>(userDto,multiValueMapAdapter, HttpStatus.OK);

        return response;
    }

    public JWTObject validate(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            throw new RuntimeException("Session not available");
        }
        //some bug
       // String email = (String)Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("email");

        String email="dummy";
        JWTObject jwtObject = new JWTObject();
        jwtObject.setEmail(email);
        jwtObject.setCreatedAt(new Date());
        jwtObject.setUserId(userId);

        return jwtObject;
    }

    public ResponseEntity<Void> logout(String token, String userId) {

        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, Long.valueOf(userId));

        if (sessionOptional.isEmpty()) {
            return null;
        }
        Session session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.ENDED);
        sessionRepository.save(session);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
