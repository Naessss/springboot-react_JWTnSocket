package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.TestVO;

@RestController
public class TestController {

	@GetMapping("/test")
	public String test() {
		return "hell r & sb ";
	}
	
	@GetMapping("/test2")
	public TestVO test2() {
		TestVO vo = new TestVO();
		vo.setId("abc");
		vo.setPw("1234");
		vo.setAge(20);
		
		return vo;
	}
	
	@PostMapping("/test3/{no}")
	public void test3(@RequestBody TestVO vo, String msg, @PathVariable int no) {
		System.out.println(vo);
		System.out.println(msg);
		System.out.println(no);
	}
	
	
}
