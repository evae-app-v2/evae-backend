package com.example.demo.controllers;

import com.example.demo.DTO.RubriqueQuestionDTO;
import com.example.demo.DTO.RubriqueQuestionDTOO;
import com.example.demo.exception.RubriqueNotFoundException;
import com.example.demo.exception.RubriqueQuestionNotFoundException;
import com.example.demo.models.Question;
import com.example.demo.models.Rubrique;
import com.example.demo.models.RubriqueQuestion;
import com.example.demo.repositories.RubriqueRepository;
import com.example.demo.services.RubriqueQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/rubriqueQuestions")
public class RubriqueQuestionController {

    @Autowired
    private RubriqueQuestionService rubriqueQuestionService;
    @Autowired
    private RubriqueRepository rubriqueRepository;


    // USED LIST ALL
    @GetMapping("/getAll")
    public ResponseEntity<List<RubriqueQuestionDTOO>> getAllRubriqueQuestion() {
        List<RubriqueQuestionDTOO> rubriqueQuestionDTOs = rubriqueQuestionService.getAll();
        return new ResponseEntity<>(rubriqueQuestionDTOs, HttpStatus.OK);
    }

    // USED DELETE QUESTION
    @GetMapping("/delete/{rubriqueId}/{questionId}")
    public ResponseEntity<?> deleteRubriqueQuestionByIds(@PathVariable Integer rubriqueId, @PathVariable Integer questionId) {
        try {
            rubriqueQuestionService.deleteRubriqueQuestionByIds(rubriqueId, questionId);
            return ResponseEntity.ok("Deletion successful.");
        } catch (RubriqueQuestionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // USED DELETE RUBRIQUE
    @GetMapping("/delete/{rubriqueId}")
    public ResponseEntity<?> deleteRubriqueQuestionsByRubriqueId(@PathVariable Integer rubriqueId) {
        try {
            rubriqueQuestionService.deleteRubriqueQuestionsByRubriqueId(rubriqueId);
            return ResponseEntity.ok("Deletion successful.");
        } catch (RubriqueNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // USED CREATE ONE
    @PostMapping("/add")
    public ResponseEntity<String> createRubriqueQuestion(@RequestBody RubriqueQuestionDTO rubriqueQuestionDTO) {
        RubriqueQuestion rubriqueQuestion = rubriqueQuestionService.createRubriqueQuestion(rubriqueQuestionDTO);
        return ResponseEntity.ok("enregistrement reussie");
    }

    //USED ADD-MULTIPLE
    @PostMapping("/add-multiple/{idRubrique}")
    public ResponseEntity<List<RubriqueQuestion>> createMultipleRubriqueQuestions(
            @PathVariable Integer idRubrique,
            @RequestBody Map<String, Object> requestBody
    ) {
        try {
            List<Integer> idQuestions = (List<Integer>) requestBody.get("idQuestions");

            List<RubriqueQuestion> createdRubriqueQuestions = rubriqueQuestionService.createRubriqueQuestionsForRubrique(idRubrique, idQuestions);

            return new ResponseEntity<>(createdRubriqueQuestions, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // USED UPDATE ORDER QUESTION
    @PostMapping("/update-ordre-question")
    public ResponseEntity<List<RubriqueQuestion>> updateOrdreRubriqueQuestions(@RequestBody List<RubriqueQuestionDTO> rubriqueQuestionDTOs) {
        List<RubriqueQuestion> updatedRubriqueQuestions = rubriqueQuestionService.updateOrdreRubriqueQuestions(rubriqueQuestionDTOs);
        return new ResponseEntity<>(updatedRubriqueQuestions, HttpStatus.OK);
    }

    // USED QUESIONS NOT IN RUBRIQUE
    @GetMapping("/getQuestions/{rubriqueId}")
    public ResponseEntity<Set<Question>> getQuestionsNotInRubrique(@PathVariable Integer rubriqueId) {
        Rubrique rubrique = rubriqueRepository.findById(rubriqueId)
                .orElseThrow(() -> new RuntimeException("Rubrique not found"));
        Set<Question> questions = rubriqueQuestionService.getQuestionsNotInRubrique(rubrique);
        return ResponseEntity.ok(questions);
    }


}
