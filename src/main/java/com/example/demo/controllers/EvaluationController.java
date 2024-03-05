package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.demo.constants.EvaeBackendConstants;
import com.example.demo.utils.BackendUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.DTO.EvaluationDTO;
import com.example.demo.services.EvaluationService;

@RestController
@RequestMapping("/api/v1/evaluation")
public class EvaluationController {
	
	@Autowired
	EvaluationService evaluationsservice;
	
	@GetMapping
	public ResponseEntity<List<EvaluationDTO>> getEvaluations(){
		try {
			return evaluationsservice.getEvaluations();
			
		} catch(Exception e){
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping("/etudiant")
	public ResponseEntity<List<EvaluationDTO>> getEvaluationsEtudiant() {
		try {
			return evaluationsservice.getEvaluationsEtudiant();
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/ajouter")
	public ResponseEntity<String> AjouterEvaluation(@RequestBody Map<String, String> requestMap){
		try {
			return evaluationsservice.AjouterEvaluation(requestMap);
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG , HttpStatus.INTERNAL_SERVER_ERROR);
	}


}
