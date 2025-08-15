package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Member;
import com.example.demo.domain.RoleType;
import com.example.demo.repository.MemberRepository;

@Service
public class MemberService {

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	public void insert(Member member) {
		member.setPassword(passwordEncoder.encode(member.getPassword()));
		member.setRole(RoleType.USER);
		memberRepository.save(member);
	}
	
	public Member getMember(String username) {
		return memberRepository.findById(username).get();
	}
}
