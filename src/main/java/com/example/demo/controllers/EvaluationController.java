package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.demo.DTO.*;
import com.example.demo.constants.EvaeBackendConstants;
import com.example.demo.models.Formation;
import com.example.demo.utils.BackendUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

	/*@GetMapping("/etudiantR")
	public ResponseEntity<List<EvaluationDTO>> getEvaluationsEtudiantR() {
		try {
			return evaluationsservice.getEvaluationsEtudiantR();
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}*/

	@PostMapping("/avancerWorkflow/{idEvaluation}")
	public ResponseEntity<String> avancerWorkflow(@PathVariable long idEvaluation) {
		return evaluationsservice.faireAvancerWorkflow(idEvaluation);
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

	@PostMapping(value="/update")
	public ResponseEntity<String> modifierEvaluation(@RequestBody EvaluationUpdateDTO requestMap) {
		try {
			return evaluationsservice.update(requestMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value = "/get-Promotions-By-Enseignant")
	public ResponseEntity<List<PromotionDTO>> getPromotionsByEnseignant(){
		try {
			return evaluationsservice.getPromotionsByEnseignant();

		} catch(Exception e){
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value = "/get-Formations-By-Enseignant-And-Annee/{anneePro}")
	public ResponseEntity<List<PromotionDTO>> getPromotionsByEnseignantAndFormation(@PathVariable("anneePro") String anneePro){
		try {
			return evaluationsservice.getPromotionsByEnseignantAndFormation(anneePro);

		} catch(Exception e){
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/*@GetMapping(value = "/get-Formations")
	public ResponseEntity<List<Formation>> getFormations(){
		try {
			return evaluationsservice.getFormations();

		} catch(Exception e){
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}*/

	@GetMapping(value = "/get-Ue-By-Enseignant-And-Formation/{codeFormation}")
	public ResponseEntity<List<UniteEnseignementDTO>> getUniteEnseignementByCodeFormationAndNoEnseignant(@PathVariable("codeFormation") String codeFormation){
		try {
			return evaluationsservice.getUniteEnseignementByCodeFormationAndNoEnseignant(codeFormation);

		} catch(Exception e){
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value = "/get-Ec-By-Enseignant-And-Formation/{codeFormation}/{codeUe}")
	public ResponseEntity<List<ElementConstitutifDTO>> getElementConstitutifByNoEnseignantAndCodeFormationAndCodeUe(@PathVariable("codeFormation") String codeFormation, @PathVariable("codeUe") String codeUe){
		try {
			return evaluationsservice.getElementConstitutifByNoEnseignantAndCodeFormationAndCodeUe(codeFormation,codeUe);

		} catch(Exception e){
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value="/delete/{id}")
	public ResponseEntity<String> SupprimerEvaluation(@PathVariable("id") int id) {
		try {
			return evaluationsservice.deleteEvaluation(id);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping(value = "/repondre")
	public ResponseEntity<String> RepondreEvaluation(@RequestBody ReponseEvaluationDTO requestMap){
		try {
			return evaluationsservice.RepondreEvaluation(requestMap);
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG , HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value="/isEtudiantRepondreEvaluation/{idEvaluation}")
	public ResponseEntity<ReponseEvaluationOutputDTO> isEtudiantRepondreEvaluation(@PathVariable("idEvaluation") int idEvaluation) {
		try {
			return evaluationsservice.isEtudiantRepondreEvaluation(idEvaluation);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ReponseEvaluationOutputDTO(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping("/getEvaluationById/{evaluationId}")
	public ResponseEntity<EvaluationDTO> getEvaluationById(@PathVariable Integer evaluationId) {
		return evaluationsservice.getEvaluationById(evaluationId);
	}




}