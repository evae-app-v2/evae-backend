package com.example.demo.services;

import com.example.demo.DTO.*;
import com.example.demo.JWT.CustomerUserDetailsService;
import com.example.demo.JWT.JwtFilter;
import com.example.demo.JWT.JwtUtil;
import com.example.demo.constants.EvaeBackendConstants;
import com.example.demo.models.*;
import com.example.demo.repositories.*;
import com.example.demo.utils.BackendUtils;
import com.example.demo.utils.EmailUtils;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;




import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import oracle.jdbc.OracleConnectionWrapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.sql.DataSource;

@Service
@AllArgsConstructor
public class EvaluationService {


	@Autowired
	UserRepository userRepository;

	@Autowired
	EvaluationRepository evaluationRepository;

	@Autowired
	ElementConstitutifRepository elementConstitutifRepository;

	@Autowired
	EnseignantRepository enseignantRepository;

	@Autowired
	PromotionRepository promotionRepository;

	@Autowired
	RubriqueQuestionRepository RubriquequestionRepository;

	@Autowired
	QuestionEvaluationRepository QuestionevaluationRepository;

	@Autowired
	RubriqueEvaluationRepository rubriqueEvaluationRepository;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	CustomerUserDetailsService customerUserDetailsService;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	JwtFilter jwtFilter;

	@Autowired
	EmailUtils emailUtils;

	@Autowired
	UniteEnseignementRepository uniteEnseignementRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private ReponseEvaluationRepository reponseEvaluationRepository;
	@Autowired
	private DroitRepository droitRepository;

	@Autowired
	QuestionEvaluationRepository questionEvaluationRepository;
	@Autowired
	ReponseQuestionRepository reponseQuestionRepository;





	public ResponseEntity<String> AjouterEvaluation(Map<String, String> requestMap) {
		System.out.println("Inside Ajout Evaluation:" + requestMap);
		try {
			if (jwtFilter.isEnseignant()) {
				Authentification user = userRepository.findByEmail(jwtFilter.getCurrentuser());
				short noEvaluation = (short) (evaluationRepository.getMaxNoEvaluation()+ 1);


				Evaluation evaluation = evaluationRepository.findByNoEvaluation(noEvaluation);

				ElementConstitutifId ecId = new ElementConstitutifId();
				ecId.setCodeFormation(requestMap.get("codeFormation"));
				ecId.setCodeEc(requestMap.get("codeEC"));
				ecId.setCodeUe(requestMap.get("codeUE"));

				ElementConstitutif ec = elementConstitutifRepository.findByElementConstitutifId(ecId);

				PromotionId proId = new PromotionId();
				proId.setAnneeUniversitaire(requestMap.get("promotion"));
				proId.setCodeFormation(requestMap.get("codeFormation"));

				Promotion pro = promotionRepository.findByPromotionId(proId);
				System.out.println("promotion : " + pro.getLieuRentree());

				UniteEnseignementId UeId = new UniteEnseignementId();
				UeId.setCodeUe(requestMap.get("codeUE"));
				UeId.setCodeFormation(requestMap.get("codeFormation"));

				UniteEnseignement ue = uniteEnseignementRepository.findByUniteEnseignementId(UeId);
				if (Objects.isNull(evaluation)) {
					// Créez la requête d'insertion avec la structure fournie
					String sqlQuery = "INSERT INTO EVALUATION (ID_EVALUATION, NO_ENSEIGNANT, CODE_FORMATION, ANNEE_UNIVERSITAIRE, CODE_UE, CODE_EC, NO_EVALUATION, DESIGNATION, ETAT, PERIODE, DEBUT_REPONSE, FIN_REPONSE) " +
							"VALUES (null, ?, ?, ?, ?, ?, ?, ?, DEFAULT, ?, TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS'))";

					jdbcTemplate.update(sqlQuery, user.getNoEnseignant().getId(), requestMap.get("codeFormation"), requestMap.get("promotion"), requestMap.get("codeUE"),requestMap.get("codeEC"),noEvaluation, requestMap.get("designation"), requestMap.get("periode"), requestMap.get("debutReponse"), requestMap.get("finReponse"));

					return BackendUtils.getResponseEntity("Evaluation Successfully Registered", HttpStatus.OK);
				} else {
					return BackendUtils.getResponseEntity("Evaluation already exists", HttpStatus.BAD_REQUEST);
				}
			}

			else {
				return BackendUtils.getResponseEntity(EvaeBackendConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Transactional
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			if (jwtFilter.isEnseignant()) {
				Evaluation evaluation = evaluationRepository.findByNoEvaluation(Short.parseShort(requestMap.get("noEvaluation")));
				Authentification user = userRepository.findByEmail(jwtFilter.getCurrentuser());


				if (evaluation != null) {
					if (evaluation.getEtat().equals("ELA")) {


						System.out.println("evaluation : " + evaluation);

						ElementConstitutifId ecId = new ElementConstitutifId();
						ecId.setCodeFormation(requestMap.get("codeFormation"));
						ecId.setCodeEc(requestMap.get("codeEC"));
						ecId.setCodeUe(requestMap.get("codeUE"));

						ElementConstitutif ec = elementConstitutifRepository.findByElementConstitutifId(ecId);
						String ecString = "";
						if(ec != null){
							ecString = requestMap.get("codeEC");
						}
						System.out.println("codeEC : "+ ecString);

						PromotionId proId = new PromotionId();
						proId.setAnneeUniversitaire(requestMap.get("promotion"));
						proId.setCodeFormation(requestMap.get("codeFormation"));

						Promotion pro = promotionRepository.findByPromotionId(proId);
						System.out.println("promotion : " + pro);

						UniteEnseignementId UeId = new UniteEnseignementId();
						UeId.setCodeUe(requestMap.get("codeUE"));
						UeId.setCodeFormation(requestMap.get("codeFormation"));

						UniteEnseignement ue = uniteEnseignementRepository.findByUniteEnseignementId(UeId);

					/*evaluation.setDebutReponse(LocalDate.parse(requestMap.get("debutReponse")));
					evaluation.setDesignation(requestMap.get("designation"));
					evaluation.setElementConstitutif(ec);
					evaluation.setEtat("ELA");
					evaluation.setFinReponse(LocalDate.parse(requestMap.get("finReponse")));
					evaluation.setNoEnseignant(user.getNoEnseignant());
					evaluation.setNoEvaluation(Short.parseShort(requestMap.get("noEvaluation")));
					evaluation.setPeriode(requestMap.get("periode"));
					evaluation.setUniteEnseignement(ue);
					evaluation.setPromotion(pro);



					evaluationRepository.save(evaluation);*/
						evaluationRepository.updateEvaluation(requestMap.get("codeFormation"), requestMap.get("promotion"), LocalDate.parse(requestMap.get("debutReponse")), LocalDate.parse(requestMap.get("finReponse")), requestMap.get("designation"), ecString, requestMap.get("codeUE"), Short.parseShort(requestMap.get("noEvaluation")), requestMap.get("periode"));
						return BackendUtils.getResponseEntity(EvaeBackendConstants.USER_STATUS, HttpStatus.OK);
					}else{
						return BackendUtils.getResponseEntity("Evaluation n est pas en cours d elaboration", HttpStatus.BAD_REQUEST);
					}
				} else {
					return BackendUtils.getResponseEntity("Evaluation n existe pas ", HttpStatus.BAD_REQUEST);
				}
			} else {
				return BackendUtils.getResponseEntity(EvaeBackendConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch (NumberFormatException e) {
			return BackendUtils.getResponseEntity("Données en format non valide.", HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<List<PromotionDTO>> getPromotionsByEnseignant() {
		try {
			if (jwtFilter.isEnseignant()) {
				Authentification user = userRepository.findByEmail(jwtFilter.getCurrentuser());
				List<Promotion> promotions = promotionRepository.getPromotionsByNoEnseignant(user.getNoEnseignant());
				List<PromotionDTO> promotionDTOs = new ArrayList<>();

				for (Promotion promotion : promotions) {
					PromotionDTO promotionDTO = new PromotionDTO();

					promotionDTO.setAnneePro(promotion.getId().getAnneeUniversitaire());
					promotionDTO.setCodeFormation(promotion.getCodeFormation().getCodeFormation());

					promotionDTOs.add(promotionDTO);
				}
				return new ResponseEntity<>(promotionDTOs, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<List<PromotionDTO>> getPromotionsByEnseignantAndFormation(String anneePro) {
		try {
			if (jwtFilter.isEnseignant()) {
				Authentification user = userRepository.findByEmail(jwtFilter.getCurrentuser());
				List<Promotion> promotions = promotionRepository.getPromotionsByAnneeProAndNoEnseignant(user.getNoEnseignant(),anneePro);
				List<PromotionDTO> promotionDTOs = new ArrayList<>();

				for (Promotion promotion : promotions) {
					PromotionDTO promotionDTO = new PromotionDTO();

					promotionDTO.setAnneePro(promotion.getId().getAnneeUniversitaire());
					promotionDTO.setCodeFormation(promotion.getCodeFormation().getCodeFormation());

					promotionDTOs.add(promotionDTO);

				}
				return new ResponseEntity<>(promotionDTOs, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<List<UniteEnseignementDTO>> getUniteEnseignementByCodeFormationAndNoEnseignant(String codeFormation) {
		try {
			if (jwtFilter.isEnseignant()) {
				Authentification user = userRepository.findByEmail(jwtFilter.getCurrentuser());
				List<UniteEnseignement> ues = uniteEnseignementRepository.getUniteEnseignementByCodeFormationAndNoEnseignant(user.getNoEnseignant(),codeFormation);
				List<UniteEnseignementDTO> ueDTOs = new ArrayList<>();

				for (UniteEnseignement ue : ues) {
					UniteEnseignementDTO ueDTO = new UniteEnseignementDTO();

					ueDTO.setCodeUe(ue.getId().getCodeUe());

					ueDTOs.add(ueDTO);

				}
				return new ResponseEntity<>(ueDTOs, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<List<ElementConstitutifDTO>> getElementConstitutifByNoEnseignantAndCodeFormationAndCodeUe(String codeFormation, String codeUe) {
		try {
			if (jwtFilter.isEnseignant()) {
				Authentification user = userRepository.findByEmail(jwtFilter.getCurrentuser());
				List<ElementConstitutif> ecs = elementConstitutifRepository.getElementConstitutifByNoEnseignantAndCodeFormationAndCodeUe(user.getNoEnseignant(),codeFormation,codeUe);
				List<ElementConstitutifDTO> ecDTOs = new ArrayList<>();

				for (ElementConstitutif ec : ecs) {
					ElementConstitutifDTO ecDTO = new ElementConstitutifDTO();

					ecDTO.setCodeEc(ec.getId().getCodeEc());

					ecDTOs.add(ecDTO);

				}
				return new ResponseEntity<>(ecDTOs, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Transactional
	public ResponseEntity<String> deleteEvaluation(int noEvaluation) {
		try {
			if (jwtFilter.isEnseignant()) {
				if (TestService.intPositifNegatif(noEvaluation)) {
					short noEvaluationShort = (short) noEvaluation;
					Evaluation evaluation = evaluationRepository.findByNoEvaluation(noEvaluationShort);
					if (evaluation.getEtat().equals("ELA")) {
						Set<RubriqueEvaluation> rubriqueEvaluations = evaluation.getRubriqueEvaluations();
						System.out.println("liste Rubrique Evaluations : "+ rubriqueEvaluations);

						for(RubriqueEvaluation rubEve : rubriqueEvaluations){
							Set<QuestionEvaluation> questionEvaluations = rubEve.getQuestionEvaluations();
							for (QuestionEvaluation queve : questionEvaluations){
								questionEvaluationRepository.deleteQuestionEvaluation(queve.getId());
							}
							rubriqueEvaluationRepository.deleteRubriqueEvaluation(rubEve.getId());
						}
						//rubriqueEvaluationRepository.deleteAll(rubriqueEvaluations);

						Set<ReponseEvaluation> reponseEvaluations = evaluation.getReponseEvaluations();
						System.out.println("liste reponse evaluation :" + reponseEvaluations);
						reponseEvaluationRepository.deleteAll(reponseEvaluations);

						Set<Droit> droits = evaluation.getDroits();
						droitRepository.deleteAll(droits);
						//evaluationRepository.delete(evaluation);
						evaluationRepository.deleteEvaluation(evaluation.getNoEvaluation());
						return BackendUtils.getResponseEntity("Evaluation supprimee avec succes ", HttpStatus.OK);
					} else {
						return BackendUtils.getResponseEntity("Evaluation n est pas en cours d elaboration", HttpStatus.BAD_REQUEST);
					}
				} else {
					return BackendUtils.getResponseEntity("Numero evaluation est negative", HttpStatus.BAD_REQUEST);
				}
			} else {
				return BackendUtils.getResponseEntity(EvaeBackendConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}


	/*public ResponseEntity<List<EvaluationDTO>> getEvaluations() {
		try {
			if (jwtFilter.isEnseignant()) {
				Authentification user = userRepository.findByEmail(jwtFilter.getCurrentuser());
				List<Evaluation> evaluations = evaluationRepository.findByNoEnseignant(user.getNoEnseignant());
				List<EvaluationDTO> evaluationsDTO = new ArrayList<>();

				for (Evaluation evaluation : evaluations) {
					EvaluationDTO evaluationDTO = new EvaluationDTO();
					evaluationDTO.setDebutReponse(evaluation.getDebutReponse());
					evaluationDTO.setDesignation(evaluation.getDesignation());
					evaluationDTO.setCodeUE(evaluation.getUniteEnseignement().getId().getCodeUe());
					evaluationDTO.setCodeFormation(evaluation.getPromotion().getCodeFormation().getCodeFormation());
					evaluationDTO.setEtat(evaluation.getEtat());
					evaluationDTO.setFinReponse(evaluation.getFinReponse());
					evaluationDTO.setId(evaluation.getId());
					evaluationDTO.setNoEvaluation(evaluation.getNoEvaluation());
					evaluationDTO.setPeriode(evaluation.getPeriode());
					evaluationDTO.setPromotion(evaluation.getPromotion().getId().getAnneeUniversitaire());

					EnseignantDTO ens = new EnseignantDTO();
					ens.setEmailUbo(evaluation.getNoEnseignant().getEmailUbo());
					ens.setId(evaluation.getNoEnseignant().getId());
					ens.setNom(evaluation.getNoEnseignant().getNom());
					ens.setPrenom(evaluation.getNoEnseignant().getPrenom());
					evaluationDTO.setNoEnseignant(ens);

					List<RubriqueEvaluation> rubevae = rubriqueEvaluationRepository.findByIdEvaluation(evaluation);
					List<EvaluationQuestionDTO> rqs = new ArrayList<>();

					for (RubriqueEvaluation rubevaluation : rubevae) {
						EvaluationQuestionDTO evaluationQuestionDTO = new EvaluationQuestionDTO();
						evaluationQuestionDTO.setIdRubrique(rubevaluation.getIdRubrique().getId());
						evaluationQuestionDTO.setOrdre(Long.valueOf(rubevaluation.getOrdre()));
						evaluationQuestionDTO.setDesignation(rubevaluation.getIdRubrique().getDesignation());
						evaluationQuestionDTO.setType(rubevaluation.getIdRubrique().getType());

						Set<QuestionDTO> uniqueQuestions = new LinkedHashSet<>();
						List<RubriqueQuestion> rubriqueQuestions = RubriquequestionRepository.findByIdRubrique(rubevaluation.getIdRubrique().getId());
						for (RubriqueQuestion rubriqueQuestion : rubriqueQuestions) {
							QuestionDTO questionDTO = new QuestionDTO();
							questionDTO.setId(rubriqueQuestion.getIdQuestion().getId());
							questionDTO.setIntitule(rubriqueQuestion.getIdQuestion().getIntitule());
							questionDTO.setType(rubriqueQuestion.getIdQuestion().getType());
							questionDTO.setOrdre(rubriqueQuestion.getOrdre());

							QualificatifDTO qualificatifDTO = new QualificatifDTO();
							qualificatifDTO.setId(rubriqueQuestion.getIdQuestion().getIdQualificatif().getId());
							qualificatifDTO.setMaximal(rubriqueQuestion.getIdQuestion().getIdQualificatif().getMaximal());
							qualificatifDTO.setMinimal(rubriqueQuestion.getIdQuestion().getIdQualificatif().getMinimal());
							questionDTO.setIdQualificatif(qualificatifDTO);

							uniqueQuestions.add(questionDTO);
						}

						List<QuestionDTO> questionsList = new ArrayList<>(uniqueQuestions);
						evaluationQuestionDTO.setQuestions(questionsList);
						rqs.add(evaluationQuestionDTO);
					}

					evaluationDTO.setRubriques(rqs);
					evaluationsDTO.add(evaluationDTO);
				}

				return new ResponseEntity<>(evaluationsDTO, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}*/
	public ResponseEntity<List<EvaluationDTO>> getEvaluations() {
		try {
			if (jwtFilter.isEnseignant()) {
				Authentification user = userRepository.findByEmail(jwtFilter.getCurrentuser());
				List<Evaluation> evaluations = evaluationRepository.findByNoEnseignant(user.getNoEnseignant());
				List<EvaluationDTO> evaluationsDTO = new ArrayList<>();

				for (Evaluation evaluation : evaluations) {
					EvaluationDTO evaluationDTO = new EvaluationDTO();
					evaluationDTO.setDebutReponse(evaluation.getDebutReponse());
					evaluationDTO.setDesignation(evaluation.getDesignation());
					evaluationDTO.setCodeUE(evaluation.getUniteEnseignement().getId().getCodeUe());
					evaluationDTO.setCodeFormation(evaluation.getPromotion().getCodeFormation().getCodeFormation());
					evaluationDTO.setEtat(evaluation.getEtat());
					evaluationDTO.setFinReponse(evaluation.getFinReponse());
					evaluationDTO.setId(evaluation.getId());
					evaluationDTO.setNoEvaluation(evaluation.getNoEvaluation());
					evaluationDTO.setPeriode(evaluation.getPeriode());
					evaluationDTO.setPromotion(evaluation.getPromotion().getId().getAnneeUniversitaire());
					if (evaluation.getElementConstitutif() != null) {
						evaluationDTO.setCodeEC(evaluation.getElementConstitutif().getId().getCodeEc());
					}else {
						evaluationDTO.setCodeEC("");
					}

					EnseignantDTO ens = new EnseignantDTO();
					ens.setEmailUbo(evaluation.getNoEnseignant().getEmailUbo());
					ens.setId(evaluation.getNoEnseignant().getId());
					ens.setNom(evaluation.getNoEnseignant().getNom());
					ens.setPrenom(evaluation.getNoEnseignant().getPrenom());
					evaluationDTO.setNoEnseignant(ens);

					List<RubriqueEvaluation> rubevae = rubriqueEvaluationRepository.findByIdEvaluation(evaluation);
					List<EvaluationQuestionDTO> rqs = new ArrayList<>();

					for (RubriqueEvaluation rubevaluation : rubevae) {
						EvaluationQuestionDTO evaluationQuestionDTO = new EvaluationQuestionDTO();
						evaluationQuestionDTO.setIdRubrique(rubevaluation.getIdRubrique().getId());
						evaluationQuestionDTO.setOrdre(Long.valueOf(rubevaluation.getOrdre()));
						evaluationQuestionDTO.setDesignation(rubevaluation.getIdRubrique().getDesignation());
						evaluationQuestionDTO.setType(rubevaluation.getIdRubrique().getType());

						Set<QuestionDTO> uniqueQuestions = new LinkedHashSet<>();
						List<RubriqueQuestion> rubriqueQuestions = RubriquequestionRepository.findByIdRubrique(rubevaluation.getIdRubrique().getId());
						for (RubriqueQuestion rubriqueQuestion : rubriqueQuestions) {
							QuestionDTO questionDTO = new QuestionDTO();
							questionDTO.setId(rubriqueQuestion.getIdQuestion().getId());
							questionDTO.setIntitule(rubriqueQuestion.getIdQuestion().getIntitule());
							questionDTO.setType(rubriqueQuestion.getIdQuestion().getType());
							questionDTO.setOrdre(rubriqueQuestion.getOrdre());
							//questionDTO.setPositionnements(0L);

							QualificatifDTO qualificatifDTO = new QualificatifDTO();
							qualificatifDTO.setId(rubriqueQuestion.getIdQuestion().getIdQualificatif().getId());
							qualificatifDTO.setMaximal(rubriqueQuestion.getIdQuestion().getIdQualificatif().getMaximal());
							qualificatifDTO.setMinimal(rubriqueQuestion.getIdQuestion().getIdQualificatif().getMinimal());
							questionDTO.setIdQualificatif(qualificatifDTO);

							uniqueQuestions.add(questionDTO);
						}

						List<QuestionDTO> questionsList = new ArrayList<>(uniqueQuestions);
						evaluationQuestionDTO.setQuestions(questionsList);
						rqs.add(evaluationQuestionDTO);
					}

					evaluationDTO.setRubriques(rqs);
					evaluationsDTO.add(evaluationDTO);
				}

				return new ResponseEntity<>(evaluationsDTO, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}




	/*public ResponseEntity<List<EvaluationDTO>> getEvaluationsEtudiant() {
		Logger logger = LoggerFactory.getLogger(this.getClass());

		try {


			if (jwtFilter.isEtudiant()) {
				Authentification user = userRepository.findByEmail(jwtFilter.getCurrentuser());
				String codeFormation = Optional.ofNullable(user.getNoEtudiant().getPromotion().getId().getCodeFormation()).orElse("");
				String anneeUniversitaire = Optional.ofNullable(user.getNoEtudiant().getPromotion().getId().getAnneeUniversitaire()).orElse("");

				if (codeFormation.isEmpty() || anneeUniversitaire.isEmpty()) {
					logger.error("Code de formation ou année universitaire vide.");
					return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
				}

				logger.info("Recherche des évaluations pour le code de formation {} et l'année universitaire {}.", codeFormation, anneeUniversitaire);

				List<Evaluation> evaluations = evaluationRepository.findByCodeFormationAnneeUnivAndEtat(codeFormation, anneeUniversitaire);

				if (evaluations.isEmpty()) {
					logger.warn("Aucune évaluation trouvée pour le code de formation {} et l'année universitaire {}.", codeFormation, anneeUniversitaire);
					return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
				}

				List<EvaluationDTO> evaluationsDTO = new ArrayList<>();

				for (Evaluation evaluation : evaluations) {
					EvaluationDTO evaluationDTO = new EvaluationDTO();
					evaluationDTO.setDebutReponse(evaluation.getDebutReponse());
					evaluationDTO.setDesignation(evaluation.getDesignation());
					evaluationDTO.setCodeUE(evaluation.getUniteEnseignement().getId().getCodeUe());
					evaluationDTO.setCodeFormation(evaluation.getPromotion().getCodeFormation().getCodeFormation());
					evaluationDTO.setEtat(evaluation.getEtat());
					evaluationDTO.setFinReponse(evaluation.getFinReponse());
					evaluationDTO.setId(evaluation.getId());
					evaluationDTO.setNoEvaluation(evaluation.getNoEvaluation());
					evaluationDTO.setPeriode(evaluation.getPeriode());
					evaluationDTO.setPromotion(evaluation.getPromotion().getId().getAnneeUniversitaire());

					EnseignantDTO ens = new EnseignantDTO();
					ens.setEmailUbo(evaluation.getNoEnseignant().getEmailUbo());
					ens.setId(evaluation.getNoEnseignant().getId());
					ens.setNom(evaluation.getNoEnseignant().getNom());
					ens.setPrenom(evaluation.getNoEnseignant().getPrenom());
					evaluationDTO.setNoEnseignant(ens);

					List<RubriqueEvaluation> rubevae = rubriqueEvaluationRepository.findByIdEvaluation(evaluation);
					List<EvaluationQuestionDTO> rqs = new ArrayList<>();

					for (RubriqueEvaluation rubevaluation : rubevae) {
						EvaluationQuestionDTO evaluationQuestionDTO = new EvaluationQuestionDTO();
						evaluationQuestionDTO.setIdRubrique(rubevaluation.getIdRubrique().getId());
						evaluationQuestionDTO.setOrdre(Long.valueOf(rubevaluation.getOrdre()));
						evaluationQuestionDTO.setDesignation(rubevaluation.getIdRubrique().getDesignation());
						evaluationQuestionDTO.setType(rubevaluation.getIdRubrique().getType());

						Set<QuestionDTO> uniqueQuestions = new LinkedHashSet<>();
						List<RubriqueQuestion> rubriqueQuestions = RubriquequestionRepository.findByIdRubrique(rubevaluation.getIdRubrique().getId());
						for (RubriqueQuestion rubriqueQuestion : rubriqueQuestions) {
							QuestionDTO questionDTO = new QuestionDTO();
							questionDTO.setId(rubriqueQuestion.getIdQuestion().getId());
							questionDTO.setIntitule(rubriqueQuestion.getIdQuestion().getIntitule());
							questionDTO.setType(rubriqueQuestion.getIdQuestion().getType());
							questionDTO.setOrdre(rubriqueQuestion.getOrdre());

							QualificatifDTO qualificatifDTO = new QualificatifDTO();
							qualificatifDTO.setId(rubriqueQuestion.getIdQuestion().getIdQualificatif().getId());
							qualificatifDTO.setMaximal(rubriqueQuestion.getIdQuestion().getIdQualificatif().getMaximal());
							qualificatifDTO.setMinimal(rubriqueQuestion.getIdQuestion().getIdQualificatif().getMinimal());
							questionDTO.setIdQualificatif(qualificatifDTO);

							uniqueQuestions.add(questionDTO);
						}

						List<QuestionDTO> questionsList = new ArrayList<>(uniqueQuestions);
						evaluationQuestionDTO.setQuestions(questionsList);
						rqs.add(evaluationQuestionDTO);
					}

					evaluationDTO.setRubriques(rqs);
					evaluationsDTO.add(evaluationDTO);
				}

				return new ResponseEntity<>(evaluationsDTO, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch(Exception e) {
			logger.error("Une erreur s'est produite lors de la récupération des évaluations de l'étudiant.", e);
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}*/



	public ResponseEntity<List<EvaluationDTO>> getEvaluationsEtudiant() {
		Logger logger = LoggerFactory.getLogger(this.getClass());

		try {
			if (jwtFilter.isEtudiant()) {
				Authentification user = userRepository.findByEmail(jwtFilter.getCurrentuser());
				String codeFormation = Optional.ofNullable(user.getNoEtudiant().getPromotion().getId().getCodeFormation()).orElse("");
				String anneeUniversitaire = Optional.ofNullable(user.getNoEtudiant().getPromotion().getId().getAnneeUniversitaire()).orElse("");
				System.out.println("NoEtudiant: "+user.getNoEtudiant().getNoEtudiant());
				if (codeFormation.isEmpty() || anneeUniversitaire.isEmpty()) {
					logger.error("Code de formation ou année universitaire vide.");
					return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
				}

				logger.info("Recherche des évaluations pour le code de formation {} et l'année universitaire {}.", codeFormation, anneeUniversitaire);

				List<Evaluation> evaluations = evaluationRepository.findByCodeFormationAnneeUnivAndEtat(codeFormation, anneeUniversitaire);

				if (evaluations.isEmpty()) {
					logger.warn("Aucune évaluation trouvée DISPONIBLE.", codeFormation, anneeUniversitaire);
					return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
				}

				List<EvaluationDTO> evaluationsDTO = new ArrayList<>();

				for (Evaluation evaluation : evaluations) {
					EvaluationDTO evaluationDTO = new EvaluationDTO();
					evaluationDTO.setDebutReponse(evaluation.getDebutReponse());
					evaluationDTO.setDesignation(evaluation.getDesignation());
					evaluationDTO.setCodeUE(evaluation.getUniteEnseignement().getId().getCodeUe());
					evaluationDTO.setCodeFormation(evaluation.getPromotion().getCodeFormation().getCodeFormation());
					evaluationDTO.setEtat(evaluation.getEtat());
					evaluationDTO.setFinReponse(evaluation.getFinReponse());
					evaluationDTO.setId(evaluation.getId());
					evaluationDTO.setNoEvaluation(evaluation.getNoEvaluation());
					evaluationDTO.setPeriode(evaluation.getPeriode());
					evaluationDTO.setPromotion(evaluation.getPromotion().getId().getAnneeUniversitaire());

					EnseignantDTO ens = new EnseignantDTO();
					ens.setEmailUbo(evaluation.getNoEnseignant().getEmailUbo());
					ens.setId(evaluation.getNoEnseignant().getId());
					ens.setNom(evaluation.getNoEnseignant().getNom());
					ens.setPrenom(evaluation.getNoEnseignant().getPrenom());
					evaluationDTO.setNoEnseignant(ens);

					List<RubriqueEvaluation> rubevae = rubriqueEvaluationRepository.findByIdEvaluation(evaluation);
					List<EvaluationQuestionDTO> rqs = new ArrayList<>();

					for (RubriqueEvaluation rubevaluation : rubevae) {
						EvaluationQuestionDTO evaluationQuestionDTO = new EvaluationQuestionDTO();
						evaluationQuestionDTO.setIdRubrique(rubevaluation.getIdRubrique().getId());
						evaluationQuestionDTO.setOrdre(Long.valueOf(rubevaluation.getOrdre()));
						evaluationQuestionDTO.setDesignation(rubevaluation.getIdRubrique().getDesignation());
						evaluationQuestionDTO.setIdRubriqueEva(rubevaluation.getId());

						evaluationQuestionDTO.setType(rubevaluation.getIdRubrique().getType());

						ReponseEvaluation reponseEvaluation = new ReponseEvaluation();





						// Liste des questions avec réponses aléatoires
						List<RubriqueQuestion> rubriqueQuestions = RubriquequestionRepository.findByIdRubrique(rubevaluation.getIdRubrique().getId());

						Set<QuestionDTO> uniqueQuestions = new LinkedHashSet<>();
						for (RubriqueQuestion rubriqueQuestion : rubriqueQuestions) {
							QuestionDTO questionDTO = new QuestionDTO();
							questionDTO.setId(rubriqueQuestion.getIdQuestion().getId());
							questionDTO.setIntitule(rubriqueQuestion.getIdQuestion().getIntitule());
							questionDTO.setType(rubriqueQuestion.getIdQuestion().getType());
							questionDTO.setOrdre(rubriqueQuestion.getOrdre());



							reponseEvaluation = reponseEvaluationRepository.findByEtudiantEvaluation(user.getNoEtudiant().getNoEtudiant(), evaluation.getId());

							if (reponseEvaluation != null) {
								System.out.println("ID Reponse EValuation"+reponseEvaluation.getId());

								ReponseQuestion reponse = reponseQuestionRepository.findByQuestionIdQuestionRubriqueEva(rubriqueQuestion.getIdQuestion().getId(),rubevaluation.getId(),evaluation.getId() ,reponseEvaluation.getId());

								if (reponse != null) {
									questionDTO.setPositionnements((long) Math.toIntExact(reponse.getPositionnement()));
								} else {
									questionDTO.setPositionnements(0L);
								}
							} else {
								questionDTO.setPositionnements(0L);
							}






							QualificatifDTO qualificatifDTO = new QualificatifDTO();
							qualificatifDTO.setId(rubriqueQuestion.getIdQuestion().getIdQualificatif().getId());
							qualificatifDTO.setMaximal(rubriqueQuestion.getIdQuestion().getIdQualificatif().getMaximal());
							qualificatifDTO.setMinimal(rubriqueQuestion.getIdQuestion().getIdQualificatif().getMinimal());
							questionDTO.setIdQualificatif(qualificatifDTO);

							uniqueQuestions.add(questionDTO);
						}

						List<QuestionDTO> questionsList = new ArrayList<>(uniqueQuestions);
						evaluationQuestionDTO.setQuestions(questionsList);
						rqs.add(evaluationQuestionDTO);
					}

					evaluationDTO.setRubriques(rqs);
					evaluationsDTO.add(evaluationDTO);
				}

				logger.info("Les évaluations ont été récupérées avec succès.");
				return new ResponseEntity<>(evaluationsDTO, HttpStatus.OK);
			} else {
				logger.warn("L'utilisateur n'est pas autorisé à accéder à cette ressource.");
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch(Exception e) {
			logger.error("Une erreur s'est produite lors de la récupération des évaluations de l'étudiant.", e);
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}




/*	public ResponseEntity<String> AjouterEvaluation(Map<String, String> requestMap) {
		System.out.println("Inside Ajout Evaluation:" + requestMap);
		try {
			if (jwtFilter.isEnseignant()) {
				Authentification user = userRepository.findByEmail(jwtFilter.getCurrentuser());
				short noEvaluation = (short) (evaluationRepository.getMaxNoEvaluation()+ 1);


					Evaluation evaluation = evaluationRepository.findByNoEvaluation(noEvaluation);

					ElementConstitutifId ecId = new ElementConstitutifId();
					ecId.setCodeFormation(requestMap.get("codeFormation"));
					ecId.setCodeEc(requestMap.get("codeEc"));
					ecId.setCodeUe(requestMap.get("codeUe"));

					ElementConstitutif ec = elementConstitutifRepository.findByElementConstitutifId(ecId);

					PromotionId proId = new PromotionId();
					proId.setAnneeUniversitaire(requestMap.get("anneePro"));
					proId.setCodeFormation(requestMap.get("codeFormation"));

					Promotion pro = promotionRepository.findByPromotionId(proId);
					System.out.println("promotion : " + pro.getLieuRentree());

					UniteEnseignementId UeId = new UniteEnseignementId();
					UeId.setCodeUe(requestMap.get("codeUe"));
					UeId.setCodeFormation(requestMap.get("codeFormation"));

					UniteEnseignement ue = uniteEnseignementRepository.findByUniteEnseignementId(UeId);
					if (Objects.isNull(evaluation)) {
						// Créez la requête d'insertion avec la structure fournie
						String sqlQuery = "INSERT INTO EVALUATION (ID_EVALUATION, NO_ENSEIGNANT, CODE_FORMATION, ANNEE_UNIVERSITAIRE, CODE_UE, CODE_EC, NO_EVALUATION, DESIGNATION, ETAT, PERIODE, DEBUT_REPONSE, FIN_REPONSE) " +
								"VALUES (null, ?, ?, ?, ?, ?, ?, ?, DEFAULT, ?, TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS'))";

						// Exécutez la requête d'insertion
						jdbcTemplate.update(sqlQuery, user.getNoEnseignant().getId(), requestMap.get("codeFormation"), requestMap.get("anneePro"), requestMap.get("codeUe"),requestMap.get("codeEc"),noEvaluation, requestMap.get("designation"), requestMap.get("periode"), requestMap.get("debutReponse"), requestMap.get("finReponse"));

						// Retournez une réponse OK si l'insertion est réussie
						return BackendUtils.getResponseEntity("Evaluation Successfully Registered", HttpStatus.OK);
					} else {
						// Retournez une réponse BAD_REQUEST si l'évaluation existe déjà
						return BackendUtils.getResponseEntity("Evaluation already exists", HttpStatus.BAD_REQUEST);
					}
				}

			 else {
				// Retournez une réponse UNAUTHORIZED si l'accès n'est pas autorisé
				return BackendUtils.getResponseEntity(EvaeBackendConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			// Gérez les erreurs et imprimez-les
			ex.printStackTrace();
		}
		// Retournez une réponse INTERNAL_SERVER_ERROR si quelque chose se passe mal
		return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}*/

	@Transactional
	public ResponseEntity<String> faireAvancerWorkflow(long idEvaluation) {
		try {
			Evaluation evaluation = evaluationRepository.findById((int) idEvaluation);

			// Vérifiez si l'évaluation existe
			if (evaluation == null) {
				return BackendUtils.getResponseEntity("Evaluation not found", HttpStatus.NOT_FOUND);
			}

			// Vérifiez si l'utilisateur est un enseignant autorisé à modifier l'évaluation
			if (!jwtFilter.isEnseignant()) {
				return BackendUtils.getResponseEntity(EvaeBackendConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}

			// Vérifiez si l'évaluation est actuellement à l'état ELA (En Cours d'élaboration)
			if (evaluation.getEtat().equals("ELA")) {
				// Mettez à jour l'état de l'évaluation à DIS (Mise à Disposition)
				evaluationRepository.updateEvaluationState((int) idEvaluation, "DIS");
				return BackendUtils.getResponseEntity("Workflow advanced successfully to Mise à Disposition", HttpStatus.OK);
			} else if (evaluation.getEtat().equals("DIS")) {
				// Mettez à jour l'état de l'évaluation à CLO (Clôture)
				evaluationRepository.updateEvaluationState((int) idEvaluation, "CLO");
				return BackendUtils.getResponseEntity("Workflow advanced successfully to Clôture", HttpStatus.OK);
			} else {
				// L'évaluation n'est pas dans un état valide pour être avancée
				return BackendUtils.getResponseEntity("Evaluation cannot be advanced from its current state", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception ex) {
			// Gérez les erreurs et imprimez-les
			ex.printStackTrace();
			return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}




}