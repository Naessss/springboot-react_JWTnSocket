package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Member;
import com.example.demo.domain.MemberCredentials;
import com.example.demo.service.JwtService;
import com.example.demo.service.MemberService;

@RestController
public class MemberController {
	
	@Autowired
	private MemberService memberService;	
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@PostMapping("/login")
	public ResponseEntity<?> getToken(@RequestBody MemberCredentials credentials) {
		UsernamePasswordAuthenticationToken creds = 
				new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword());
		
		Authentication auth = authenticationManager.authenticate(creds);
		
		String jwts = jwtService.createToken(auth.getName(), auth.getAuthorities());
		
		return ResponseEntity.ok()
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwts)
				.header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
				.build();
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody Member member) {
		
		memberService.insert(member);
		
		return new ResponseEntity<>("회원가입성공", HttpStatus.OK);
	}
	
	@GetMapping("/userInfo")
	public ResponseEntity<?> userInfo(Authentication authentication) {
		String username = authentication.getName();
		
		Member member = memberService.getMember(username);
		
		return new ResponseEntity<>(member, HttpStatus.OK);
	}
	
}
