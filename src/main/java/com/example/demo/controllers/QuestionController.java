package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.DTO.QuestionDTO;
import com.example.demo.constants.EvaeBackendConstants;
import com.example.demo.services.QuestionService;
import com.example.demo.utils.BackendUtils;

@RestController
@RequestMapping("/api/v1/question")
public class QuestionController {
	
	@Autowired
	QuestionService questionservice;
	
	@GetMapping
	public ResponseEntity<List<QuestionDTO>> getQuestions(){
		try {
			return questionservice.getQuestions();
			
		} catch(Exception e){
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@PostMapping(value = "/ajouter")
	public ResponseEntity<String> ajouterQuestion(@RequestBody Map<String, String> requestMap){
		try {
            return questionservice.AjouterQuestion(requestMap);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG , HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@PostMapping(value="/update")
	public ResponseEntity<String> modifierQuestion(@RequestBody Map<String, String> requestMap) {
		try {
			return questionservice.update(requestMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping(value="/delete/{id}")
	public ResponseEntity<String> SupprimerQuestion(@PathVariable("id") int id) {
		try {
			return questionservice.deleteQuestion(id);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping("/questionIsUsedInRubriqueOrEvaluation/{id}")
	public boolean questionIsUsedInRubriqueOrEvaluation(@PathVariable Integer id){
		return questionservice.questionIsUsedInRubriqueOrEvaluation(id);
	}


}
