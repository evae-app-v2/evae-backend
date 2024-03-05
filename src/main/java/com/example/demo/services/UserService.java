package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.example.demo.DTO.EnseignantDTO;
import com.example.demo.DTO.EtudiantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.example.demo.JWT.CustomerUserDetailsService;
import com.example.demo.JWT.JwtFilter;
import com.example.demo.JWT.JwtUtil;
import com.example.demo.constants.EvaeBackendConstants;
import com.example.demo.models.Authentification;
import com.example.demo.models.Enseignant;
import com.example.demo.models.Etudiant;
import com.example.demo.repositories.EnseignantRepository;
import com.example.demo.repositories.EtudiantRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.utils.EmailUtils;
import com.example.demo.utils.BackendUtils;
import com.google.common.base.Strings;
import com.example.demo.DTO.UserDTO;

@Service
public class UserService {
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
	private UserRepository userrepos;

	@Autowired
	EnseignantRepository enseignantRepository;

	@Autowired
	EtudiantRepository etudiantRepository;

	public UserService(AuthenticationManager authenticationManager, CustomerUserDetailsService customerUserDetailsService, JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.customerUserDetailsService = customerUserDetailsService;
		this.jwtUtil = jwtUtil;
	}

	public ResponseEntity<String> inscrireUtilisateur(Map<String, String> requestMap) {
		System.out.println("Request Map: " + requestMap);
		System.out.println("Validation Result: " + validateSignUpMap(requestMap));
		try {
			if (validateSignUpMap(requestMap)) {

				Authentification user = userrepos.findByEmail(requestMap.get("loginConnection"));
				if (Objects.isNull(user)) {
					userrepos.save(getUserFromMap(requestMap));
					return BackendUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);
				} else {
					return BackendUtils.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
				}
			} else {
				return BackendUtils.getResponseEntity(EvaeBackendConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private boolean validateSignUpMap(Map<String, String> requestMap) {
		if (requestMap.containsKey("loginConnection") && requestMap.containsKey("pseudoConnection")
				&& requestMap.containsKey("motpasse")
				&& requestMap.containsKey("role")
		) {
			if (requestMap.get("pseudoConnection") != "" && !TestService.isInteger(requestMap.get("pseudoConnection"))
					&& !TestService.isStartWithNumber(requestMap.get("loginConnection")) && requestMap.get("loginConnection") != ""
					&& requestMap.get("idEnseignant") != ""
					&& !TestService.isInteger(requestMap.get("noEtudiant")) && requestMap.get("noEtudiant") != ""
					&& requestMap.get("role") != "" && !TestService.isInteger(requestMap.get("role"))
					&& !TestService.isStartWithNumber(requestMap.get("role"))) {
				return true;
			} else {
				return false;
			}

		}
		return false;
	}

	private Authentification getUserFromMap(Map<String, String> requestMap) {
		//Enseignant ens = enseignantRepository.findById(Short.parseShort(requestMap.get("idEnseignant"))).get();
		//Etudiant etu = etudiantRepository.findByNoEtudiant(requestMap.get("noEtudiant"));
		Authentification user = new Authentification();
		user.setPseudoConnection(requestMap.get("pseudoConnection"));
		user.setLoginConnection(requestMap.get("loginConnection"));
		user.setMotPasse(requestMap.get("motpasse"));
		user.setNoEnseignant(null);
		user.setNoEtudiant(null);
		user.setRole(requestMap.get("role"));

		return user;
	}

	public ResponseEntity<String> login(Map<String, String> requestMap) {
		System.out.println("Inside login");
		try {
			if (!requestMap.containsKey("loginConnection") || !requestMap.containsKey("motpasse")) {
				return new ResponseEntity<String>("{\"message\":\"" + "Bad Credentials." + "\"}",
						HttpStatus.BAD_REQUEST);
			} else {
				Authentication auth = authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(requestMap.get("loginConnection"), requestMap.get("motpasse")));

				if (auth != null && auth.isAuthenticated()) {
					return new ResponseEntity<String>(
							"{\"token\":\""
									+ jwtUtil.generateToken(customerUserDetailsService.getUserDetails().getLoginConnection(),
									customerUserDetailsService.getUserDetails().getRole())
									+ "\"}",
							HttpStatus.OK);
				}
			}

		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<String>("{\"message\":\"" + "Bad Credentials." + "\"}", HttpStatus.BAD_REQUEST);
	}


	public ResponseEntity<String> checkToken() {
		return BackendUtils.getResponseEntity("true", HttpStatus.OK);
	}

	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		try {
			Authentification userObj = userrepos.findByEmail(jwtFilter.getCurrentuser());
			if (!userObj.equals(null)) {
				if (userObj.getMotPasse().equals(requestMap.get("oldPassword"))) {
					userObj.setMotPasse(requestMap.get("newPassword"));
					userrepos.save(userObj);
					emailUtils.sendSimpleMessage(jwtFilter.getCurrentuser(), "Password changed",
							"Account password is changed.", null);
					return BackendUtils.getResponseEntity("Password Updated Successfully", HttpStatus.OK);
				}
				return BackendUtils.getResponseEntity("Incoreect Old Password", HttpStatus.BAD_REQUEST);
			}
			return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG,
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
		try {
			Authentification userObj = userrepos.findByEmail(requestMap.get("pseudoConnection"));
			if (!Objects.isNull(userObj) && !Strings.isNullOrEmpty(userObj.getLoginConnection())) {
				emailUtils.forgotPasswordMail(userObj.getLoginConnection(), "Your Login Credentials for Etron Management System",
						userObj.getMotPasse());
			}
			return BackendUtils.getResponseEntity("Check your mail box...", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<List<UserDTO>> getUsers() {
		try {
			if (jwtFilter.isAdmin()) {
				List<Authentification> users = userrepos.findAll();
				List<UserDTO> userDTOs = new ArrayList<>();

				for (Authentification user : users) {
					UserDTO userDTO = new UserDTO();

					// Map user data to the DTO
					userDTO.setId(user.getId());
					userDTO.setLoginConnection(user.getLoginConnection());
					userDTO.setMotPasse(user.getMotPasse());

					EnseignantDTO ens = new EnseignantDTO();
					ens.setId(user.getNoEnseignant().getId());
					ens.setNom(user.getNoEnseignant().getNom());
					ens.setPrenom(user.getNoEnseignant().getPrenom());
					ens.setEmailUbo(user.getNoEnseignant().getEmailUbo());

					EtudiantDTO etu = new EtudiantDTO();
					etu.setNoEtudiant(user.getNoEtudiant().getNoEtudiant());

					userDTO.setNoEnseignant(ens);
					userDTO.setNoEtudiant(etu);
					userDTO.setPseudoConnection(user.getPseudoConnection());
					userDTO.setRole(user.getRole());


					userDTOs.add(userDTO);
				}

				return new ResponseEntity<>(userDTOs, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			System.out.println(requestMap);
			if (jwtFilter.isAdmin()) {
				if (requestMap.containsKey("loginConnection") && requestMap.containsKey("idEnseignant")
						&& requestMap.containsKey("pseudoConnection") && requestMap.containsKey("motpasse")
						&& requestMap.containsKey("noEtudiant") && requestMap.containsKey("role") ) {
					if (!requestMap.get("loginConnection").isEmpty()
							&& !requestMap.get("role").isEmpty()
							&& !requestMap.get("pseudoConnection").isEmpty() && !requestMap.get("motpasse").isEmpty()) {
						if (!TestService.isInteger(requestMap.get("loginConnection"))
								&& !TestService.isInteger(requestMap.get("noEtudiant"))
								&& !TestService.isInteger(requestMap.get("role"))
								&& !TestService.isInteger(requestMap.get("pseudoConnection"))) {
							Authentification user = userrepos.findByEmail(requestMap.get("pseudoConnection"));
							Enseignant ens = enseignantRepository.findById(Short.parseShort(requestMap.get("idEnseignant"))).get();
							Etudiant etu = etudiantRepository.findByNoEtudiant(requestMap.get("noEtudiant"));
							if (user != null) {
								user.setLoginConnection(requestMap.get("loginConnection"));;
								user.setNoEnseignant(ens);
								user.setNoEtudiant(etu);
								user.setRole(requestMap.get("role"));
								user.setPseudoConnection(requestMap.get("pseudoConnection"));
								user.setMotPasse(requestMap.get("motpasse"));

								userrepos.save(user);
								return BackendUtils.getResponseEntity(EvaeBackendConstants.USER_STATUS, HttpStatus.OK);
							} else {
								return BackendUtils.getResponseEntity("User id doesn't exist.", HttpStatus.OK);
							}
						} else {
							return BackendUtils.getResponseEntity("Type is Not String , Please give a String Value",
									HttpStatus.BAD_REQUEST);
						}
					} else {
						return BackendUtils.getResponseEntity("Argument Value Missing", HttpStatus.BAD_REQUEST);
					}
				} else {
					return BackendUtils.getResponseEntity("Argument Missing", HttpStatus.BAD_REQUEST);
				}
			} else {
				return BackendUtils.getResponseEntity(EvaeBackendConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<String> deleteUser(int idUser) {
		try {
			if (jwtFilter.isAdmin()) {
				if (TestService.intPositifNegatif(idUser)) {
					Authentification user = userrepos.findById(idUser);
					userrepos.delete(user);
					return BackendUtils.getResponseEntity("User Deleted Successfully", HttpStatus.OK);
				} else {
					return BackendUtils.getResponseEntity(
							"Negative or Zero idUser Not Supported , Please Give a Positive idUser",
							HttpStatus.BAD_REQUEST);
				}

			} else {
				return BackendUtils.getResponseEntity(EvaeBackendConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BackendUtils.getResponseEntity(EvaeBackendConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<UserDTO> getUserByToken() {
		try {
			Authentification user = userrepos.findByEmail(jwtFilter.getCurrentuser());

			UserDTO userDTO = new UserDTO();

			userDTO.setId(user.getId());
			userDTO.setLoginConnection(user.getLoginConnection());
			userDTO.setMotPasse(user.getMotPasse());

			EnseignantDTO ens = new EnseignantDTO();
			if (user.getNoEnseignant() != null) {
				ens.setId(user.getNoEnseignant().getId());
				ens.setNom(user.getNoEnseignant().getNom());
				ens.setPrenom(user.getNoEnseignant().getPrenom());
				ens.setEmailUbo(user.getNoEnseignant().getEmailUbo());
			} else {
				ens.setId(null);
				ens.setNom(null);
				ens.setPrenom(null);
				ens.setEmailUbo(null);
			}

			EtudiantDTO etu = new EtudiantDTO();
			if (user.getNoEtudiant() != null) {
				etu.setNoEtudiant(user.getNoEtudiant().getNoEtudiant());
				etu.setNom(user.getNoEtudiant().getNom());
				etu.setPrenom(user.getNoEtudiant().getPrenom());
				etu.setEmailUbo(user.getNoEtudiant().getEmailUbo());
			} else {
				etu.setNoEtudiant(null);
				etu.setNoEtudiant(null);
				etu.setNom(null);
				etu.setPrenom(null);
				etu.setEmailUbo(null);

			}

			userDTO.setNoEnseignant(ens);
			userDTO.setNoEtudiant(etu);
			userDTO.setPseudoConnection(user.getPseudoConnection());
			userDTO.setRole(user.getRole());


			return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new UserDTO(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}