package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Board;
import com.example.demo.service.BoardService;

@RestController
public class BoardController {
	
	@Autowired
	private BoardService boardService;

	@PostMapping("/board")
	public ResponseEntity<?> newBoard(@RequestBody Board board) {
		System.out.println(board);
		
		boardService.newBoard(board);
		
		return new ResponseEntity<>("게시등 등록 성공", HttpStatus.OK);
	}
	
	@GetMapping("/board")
	public ResponseEntity<?> getBoardList() {
		List<Board> boardList = boardService.getBoardList();
		
		return new ResponseEntity<>(boardList, HttpStatus.OK);
	}
	
	@GetMapping("/board/{id}")
	public ResponseEntity<?> getBoard(@PathVariable Integer id) {
		Board board = boardService.getBoard(id);
		
		return new ResponseEntity<>(board, HttpStatus.OK);
	}
	
	@DeleteMapping("/board")                           // ("id")는 없어도 됨
	public ResponseEntity<?> deleteBoard(@RequestParam Integer id) {
		
		boardService.deleteBoard(id);
		
		return new ResponseEntity<>(id + "번 게시물 삭제 성공", HttpStatus.OK);
	}
}
