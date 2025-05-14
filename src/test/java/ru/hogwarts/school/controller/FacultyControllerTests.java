package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import ru.hogwarts.school.DTO.FacultyDTO;
import ru.hogwarts.school.DTO.StudentDTO;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FacultyControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;

    private String getUrl() {
        return "http://localhost:" + port + "/faculty";
    }

    @BeforeEach
    void clean() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    void createFacultyTest() {
        Faculty faculty = new Faculty(0L, "Лизерин", "Жёлтый");
        Faculty response = restTemplate.postForObject(getUrl(), faculty, Faculty.class);

        Assertions.assertNotNull(response);
        Assertions.assertNotEquals(0L, response.getId());
        Assertions.assertEquals("Лизерин", response.getName());
        Assertions.assertEquals("Жёлтый", response.getColor());
    }

    @Test
    void getFacultyInfoByIdTest() {
        Faculty faculty = facultyRepository.save(new Faculty(0L, "Лизерин", "Жёлтый"));

        ResponseEntity<Faculty> response = restTemplate.getForEntity(getUrl() + "/" + faculty.getId(), Faculty.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Лизерин", response.getBody().getName());
        Assertions.assertEquals("Жёлтый", response.getBody().getColor());
    }

    @Test
    void getFacultyInfoByInvalidIdTest() {
        ResponseEntity<Faculty> response = restTemplate.getForEntity(getUrl() + "/9999", Faculty.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateFacultyTest() {
        Faculty faculty = new Faculty(0L, "Лизерин", "Жёлтый");
        faculty = facultyRepository.save(faculty);

        Faculty updatedFaculty = new Faculty(faculty.getId(), "New Лизерин", "Жёлтый");

        ResponseEntity<Faculty> response = restTemplate.postForEntity(getUrl(), updatedFaculty, Faculty.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("New Лизерин", response.getBody().getName());
    }

    @Test
    void deleteFacultyTest() {
        Faculty faculty = new Faculty(0L, "Лизерин", "Жёлтый");
        faculty = facultyRepository.save(faculty);

        restTemplate.delete(getUrl() + "/" + faculty.getId());

        ResponseEntity<Faculty> response = restTemplate.getForEntity(getUrl() + "/" + faculty.getId(), Faculty.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getFacultiesByColorOrNameTest() {
        Faculty faculty = new Faculty(0L, "Лизерин", "Жёлтый");
        Faculty faculty1 = new Faculty(0L, "Гиффиндуй", "Жёлтый");
        Faculty faculty2 = new Faculty(0L, "Пуффиндор", "Айвори");

        facultyRepository.saveAll(Arrays.asList(faculty, faculty1, faculty2));

        ResponseEntity<Collection> response = restTemplate.getForEntity(getUrl() + "/colororname/жёлтый", Collection.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2, response.getBody().size());
    }

    @Test
    void getFacultyStudentsTest() {
        Faculty faculty = new Faculty(0L, "Лизерин", "Жёлтый");
        facultyRepository.save(faculty);

        Student student = new Student(0L, "Портер", 21);
        Student student2 = new Student(0L, "Вислый", 21);

        student.setFaculty(faculty);
        student2.setFaculty(faculty);

        studentRepository.save(student);
        studentRepository.save(student2);


        ResponseEntity<StudentDTO[]> response = restTemplate.getForEntity(
                getUrl() + "/" + faculty.getId() + "/students",
                StudentDTO[].class
        );

        List<StudentDTO> students = Arrays.asList(response.getBody());

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Портер", students.get(0).getName());
        Assertions.assertEquals("Вислый", students.get(1).getName());
        Assertions.assertEquals(2, students.size());
    }

    @Test
    void getLongestFacultyNameTest() {
        facultyRepository.save(new Faculty(0L, "А", "Жёлтый"));
        facultyRepository.save(new Faculty(0L, "Лизерин", "Жёлтый"));
        facultyRepository.save(new Faculty(0L, "ОченьДлинноеНазваниеФакультета", "Жёлтый"));

        ResponseEntity<String> response = restTemplate.getForEntity(getUrl() + "/longest-faculty-name", String.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("ОченьДлинноеНазваниеФакультета", response.getBody());
    }
}
