package com.example.demo.services;


import com.example.demo.DTO.RubriqueQuestionDTO;
import com.example.demo.exception.RubriqueNotFoundException;
import com.example.demo.exception.RubriqueQuestionNotFoundException;
import com.example.demo.models.*;
import com.example.demo.repositories.QuestionRepository;
import com.example.demo.repositories.RubriqueQuestionRepository;
import com.example.demo.repositories.RubriqueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RubriqueQuestionServiceImplTest {

    @Mock
    private RubriqueQuestionRepository rubriqueQuestionRepository;

    @Mock
    private RubriqueRepository rubriqueRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private RubriqueQuestionServiceImpl rubriqueQuestionService;


    private RubriqueQuestion createMockRubriqueQuestion(int idRubrique, int idQuestion, int ordre) {
        RubriqueQuestion rubriqueQuestion = new RubriqueQuestion();
        Rubrique rubrique = new Rubrique();
        rubrique.setId(idRubrique);

        // Création et initialisation de la Question
        Question question = new Question();
        question.setId(idQuestion);

        // Initialisation de RubriqueQuestion avec Rubrique et Question
        rubriqueQuestion.setIdRubrique(rubrique);
        rubriqueQuestion.setIdQuestion(question);
        rubriqueQuestion.setOrdre((long) ordre);

        // Création des entités liées (Rubrique, Question, Qualificatif)
        rubrique.setId(idRubrique);
        rubrique.setType("RBS");
        rubrique.setDesignation("COURS");
        rubrique.setOrdre(1L);

        question.setId(idQuestion);
        question.setType("QUS");
        question.setIntitule("Contenu");

        Qualificatif qualificatif = new Qualificatif();
        qualificatif.setId(1);
        qualificatif.setMaximal("Pauvre");

        question.setIdQualificatif(qualificatif);

        rubriqueQuestion.setIdRubrique(rubrique);
        rubriqueQuestion.setIdQuestion(question);

        return rubriqueQuestion;
    }


    @Test
    void testGetQuestionsByRubrique() {
        // Création d'une rubrique fictive pour le test
        Rubrique rubrique = new Rubrique();
        rubrique.setId(1);
        rubrique.setType("RBS");
        rubrique.setDesignation("COURS");
        rubrique.setOrdre(1L);

        // Création de données fictives pour le test
        Set<Question> questions = new HashSet<>();
        Question question1 = new Question();
        question1.setId(1);
        question1.setType("QUS");
        question1.setIntitule("Contenu");
        questions.add(question1);

        Question question2 = new Question();
        question2.setId(2);
        question2.setType("QUS");
        question2.setIntitule("Intérêt");
        questions.add(question2);

        // Simulation du comportement du repository
        when(rubriqueQuestionRepository.findQuestionsByRubrique(rubrique)).thenReturn(questions);

        // Appel de la méthode à tester
        Set<Question> result = rubriqueQuestionService.getQuestionsByRubrique(rubrique);

        // Vérifications
        assertEquals(2, result.size());
        assertEquals("Contenu", result.stream().filter(q -> q.getId() == 1).findFirst().get().getIntitule());
        assertEquals("Intérêt", result.stream().filter(q -> q.getId() == 2).findFirst().get().getIntitule());
    }




    @Test
    void testCreateRubriqueQuestion_WhenNotExists() {
        // Création de données de test
        RubriqueQuestionDTO rubriqueQuestionDTO = new RubriqueQuestionDTO();
        rubriqueQuestionDTO.setIdRubrique(1);
        rubriqueQuestionDTO.setIdQuestion(2);
        rubriqueQuestionDTO.setOrdre(1L);

        RubriqueQuestion rubriqueQuestion = new RubriqueQuestion();
        rubriqueQuestion.setId(new RubriqueQuestionId(1, 2));
        rubriqueQuestion.setOrdre(1L); // Ajout de l'ordre à la rubrique de question

        Rubrique rubrique = new Rubrique();
        rubrique.setId(1);

        Question question = new Question();
        question.setId(2);

        // Définir le comportement simulé des repositories
        when(rubriqueQuestionRepository.existsById_IdRubriqueAndId_IdQuestion(1, 2)).thenReturn(false);
        when(rubriqueRepository.findById(1)).thenReturn(Optional.of(rubrique));
        when(questionRepository.findById(2)).thenReturn(Optional.of(question));
        when(rubriqueQuestionRepository.save(any())).thenReturn(rubriqueQuestion);

        // Appeler la méthode à tester
        RubriqueQuestion createdRubriqueQuestion = rubriqueQuestionService.createRubriqueQuestion(rubriqueQuestionDTO);

        // Vérifier les résultats
        assertNotNull(createdRubriqueQuestion);
        assertEquals(1, createdRubriqueQuestion.getId().getIdRubrique());
        assertEquals(2, createdRubriqueQuestion.getId().getIdQuestion());
        assertEquals(1L, createdRubriqueQuestion.getOrdre()); // Correction : vérifier l'ordre correctement initialisé
    }


    @Test
    void testDeleteRubriqueQuestionsByRubriqueId() throws RubriqueNotFoundException {
        // Données de test
        Integer rubriqueId = 1;

        // Simuler la présence de la rubrique dans le repository
        when(rubriqueRepository.findById(rubriqueId)).thenReturn(Optional.of(new Rubrique()));

        // Appeler la méthode à tester
        String result = rubriqueQuestionService.deleteRubriqueQuestionsByRubriqueId(rubriqueId);

        // Vérifier que la méthode deleteByRubriqueId du repository est appelée
        verify(rubriqueQuestionRepository, times(1)).deleteByRubriqueId(rubriqueId);
        // Vérifier que la méthode retourne la chaîne attendue
        assertEquals("Deletion successful.", result);
    }

    @Test
    void testDeleteRubriqueQuestionsByRubriqueId_RubriqueNotFoundException() {
        // Données de test
        Integer rubriqueId = 1;

        // Simuler l'absence de la rubrique dans le repository
        when(rubriqueRepository.findById(rubriqueId)).thenReturn(Optional.empty());

        // Vérifier que l'appel lance une exception RubriqueNotFoundException
        assertThrows(RubriqueNotFoundException.class, () -> rubriqueQuestionService.deleteRubriqueQuestionsByRubriqueId(rubriqueId));
        // Vérifier que la méthode deleteByRubriqueId du repository n'est pas appelée
        verify(rubriqueQuestionRepository, never()).deleteByRubriqueId(rubriqueId);
    }

    @Test
    void testDeleteRubriqueQuestionByIds() throws RubriqueQuestionNotFoundException {
        // Données de test
        Integer rubriqueId = 1;
        Integer questionId = 1;

        // Simuler la présence de la RubriqueQuestion dans le repository
        when(rubriqueQuestionRepository.findById(new RubriqueQuestionId(rubriqueId, questionId))).thenReturn(Optional.of(new RubriqueQuestion()));

        // Appeler la méthode à tester
        String result = rubriqueQuestionService.deleteRubriqueQuestionByIds(rubriqueId, questionId);

        // Vérifier que la méthode deleteById du repository est appelée
        verify(rubriqueQuestionRepository, times(1)).deleteById(new RubriqueQuestionId(rubriqueId, questionId));
        // Vérifier que la méthode retourne la chaîne attendue
        assertEquals("Deletion successful.", result);
    }

    @Test
    void testDeleteRubriqueQuestionByIds_RubriqueQuestionNotFoundException() {
        Integer rubriqueId = 1;
        Integer questionId = 1;
        when(rubriqueQuestionRepository.findById(new RubriqueQuestionId(rubriqueId, questionId))).thenReturn(Optional.empty());

        assertThrows(RubriqueQuestionNotFoundException.class, () -> rubriqueQuestionService.deleteRubriqueQuestionByIds(rubriqueId, questionId));
        verify(rubriqueQuestionRepository, never()).deleteById(new RubriqueQuestionId(rubriqueId, questionId));
    }

}





