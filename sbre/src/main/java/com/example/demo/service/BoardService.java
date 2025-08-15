package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Board;
import com.example.demo.repository.BoardRepository;

@Service
public class BoardService {

	@Autowired
	private BoardRepository boardRepository;
	
	public void newBoard(Board board) {
		boardRepository.save(board);
	}
	
	public List<Board> getBoardList() {
		return boardRepository.findAllByOrderByIdDesc();
	}
	
	public Board getBoard(Integer id) {
		return boardRepository.findById(id).get();
	}
	
	public void deleteBoard(Integer id) {
		boardRepository.deleteById(id);
	}
}
