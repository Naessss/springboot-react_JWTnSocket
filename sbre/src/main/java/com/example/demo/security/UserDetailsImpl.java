package com.example.demo.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.domain.Member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsImpl implements UserDetails{
	private static final long serialVersionUID = 1L;
	
	private Member member;

	public UserDetailsImpl(Member member) {
		this.member = member;
	}

	// 계정이 가지고 있는 권한 목록을 저장해서 반환
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		// 권한 목록 저장 컬렉션 생성
		Collection<GrantedAuthority> roleList = new ArrayList<>();
		
		// 권한 목록 설정
		roleList.add(() -> {
			return "ROLE_" + member.getRole();
		});
		
		return roleList;
	}

	@Override
	public String getPassword() {
		// noop : 암호화 하지 않기 위한 설정
		return member.getPassword();
	}

	@Override
	public String getUsername() {
		return member.getUsername();
	}

	// 계정이 만료되지 않았는지 반환
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	// 계정이 잠겨있는지 반환
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// 비밀번호가 만료되지 않았는지 반환
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	// 계정이 활성화되었는지 반환
	@Override
	public boolean isEnabled() {
		return true;
	}

}