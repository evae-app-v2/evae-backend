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


    // USED
    @GetMapping("/getAll")
    public ResponseEntity<List<RubriqueQuestionDTOO>> getAllRubriqueQuestion() {
        List<RubriqueQuestionDTOO> rubriqueQuestionDTOs = rubriqueQuestionService.getAll();
        return new ResponseEntity<>(rubriqueQuestionDTOs, HttpStatus.OK);
    }

    // USED
    @GetMapping("/delete/{rubriqueId}/{questionId}")
    public ResponseEntity<?> deleteRubriqueQuestionByIds(@PathVariable Integer rubriqueId, @PathVariable Integer questionId) {
        try {
            rubriqueQuestionService.deleteRubriqueQuestionByIds(rubriqueId, questionId);
            return ResponseEntity.ok("Deletion successful.");
        } catch (RubriqueQuestionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // USED
    @GetMapping("/delete/{rubriqueId}")
    public ResponseEntity<?> deleteRubriqueQuestionsByRubriqueId(@PathVariable Integer rubriqueId) {
        try {
            rubriqueQuestionService.deleteRubriqueQuestionsByRubriqueId(rubriqueId);
            return ResponseEntity.ok("Deletion successful.");
        } catch (RubriqueNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // USED
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

    @GetMapping("/groupedByRubrique")
    public ResponseEntity<Map<Integer, List<RubriqueQuestionDTO>>> getQuestionsGroupedByRubrique() {
        Map<Integer, List<RubriqueQuestionDTO>> groupedQuestions = rubriqueQuestionService.getQuestionsGroupedByRubrique();
        return new ResponseEntity<>(groupedQuestions, HttpStatus.OK);
    }


    @GetMapping("/getQuestions/{rubriqueId}")
    public ResponseEntity<Set<Question>> getQuestionsNotInRubrique(@PathVariable Integer rubriqueId) {
        Rubrique rubrique = rubriqueRepository.findById(rubriqueId)
                .orElseThrow(() -> new RuntimeException("Rubrique not found"));
        Set<Question> questions = rubriqueQuestionService.getQuestionsNotInRubrique(rubrique);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/update/{rubriqueId1}/{questionId1}/{rubriqueId2}/{questionId2}/swapOrdre")
    public ResponseEntity<?> swapOrdre(@PathVariable Integer rubriqueId1,
                                       @PathVariable Integer questionId1,
                                       @PathVariable Integer rubriqueId2,
                                       @PathVariable Integer questionId2) {
        try {
            if (!rubriqueId1.equals(rubriqueId2)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("idRubrique1 and idRubrique2 must be equal.");
            }

            rubriqueQuestionService.swapOrdre(rubriqueId1, questionId1, rubriqueId2, questionId2);
            return ResponseEntity.ok("Ordre values swapped successfully.");
        } catch (RubriqueQuestionNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    //aprem2
    @GetMapping("/groupedByRubriqueOrderedByOrdre")
    public ResponseEntity<Map<Integer, List<RubriqueQuestionDTO>>> getQuestionsGroupedByRubriqueOrderedByOrdre() {
        Map<Integer, List<RubriqueQuestionDTO>> groupedQuestions = rubriqueQuestionService.getQuestionsGroupedByRubriqueOrderedByOrdre();
        return new ResponseEntity<>(groupedQuestions, HttpStatus.OK);
    }


}
