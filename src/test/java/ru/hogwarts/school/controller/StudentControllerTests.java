package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.DTO.FacultyDTO;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StudentControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AvatarRepository avatarRepository;

    @BeforeEach
    void clean() {
        avatarRepository.deleteAll();
        studentRepository.deleteAll();
    }

    private String getUrl(){
        return "http://localhost:" + port + "/student";
    }

    @Test
    void contextLoads() throws Exception {
        Assertions.assertNotNull(studentController);
    }

    @Test
    void createStudentTest() throws Exception {
        Student student = new Student(0L, "Портер", 21);
        Student response = restTemplate.postForObject(getUrl(), student, Student.class);
        Assertions.assertNotNull(response);
        Assertions.assertNotEquals(0L, response.getId());
        Assertions.assertEquals("Портер", response.getName());
        Assertions.assertEquals(21, response.getAge());
    }

    @Test
    void getStudentInfoByIdTest() throws Exception {
        Student student = new Student(0L, "Портер", 21);
        Student created = restTemplate.postForObject(
                getUrl(),
                student,
                Student.class);

        ResponseEntity<Student> response = restTemplate.getForEntity(
                getUrl() + "/" + created.getId(),
                Student.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Портер", response.getBody().getName());
    }

    @Test
    void getStudentInfoByInvalidIdTest() throws Exception {
        ResponseEntity<Student> response = restTemplate.getForEntity(
                getUrl() + "/99999",
                Student.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateStudentTest() throws Exception {
        Student student = new Student(0L, "Портер", 21);
        Student created = restTemplate.postForObject(
                getUrl(),
                student,
                Student.class);

        created.setName("New Портер");
        restTemplate.put(getUrl(), created);

        ResponseEntity<Student> response = restTemplate.getForEntity(
                getUrl() + "/" + created.getId(),
                Student.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("New Портер", response.getBody().getName());
    }

    @Test
    void deleteStudentTest() throws Exception {
        Student student = new Student(0L, "Портер", 21);
        Student created = restTemplate.postForObject(
                getUrl(),
                student,
                Student.class);

        restTemplate.delete(getUrl() + "/" + created.getId());

        ResponseEntity<Student> response = restTemplate.getForEntity(
                getUrl() + "/" + created.getId(),
                Student.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getStudentsByAgeBetweenTest() throws Exception {
        restTemplate.postForObject(getUrl(), new Student(0L, "Портер", 21), Student.class);
        restTemplate.postForObject(getUrl(), new Student(0L, "Вислый", 25), Student.class);
        restTemplate.postForObject(getUrl(), new Student(0L, "Грыжер", 27), Student.class);

        ResponseEntity<Collection> response = restTemplate.getForEntity(
                getUrl() + "/age?minAge=20&maxAge=25",
                Collection.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(2, response.getBody().size());
    }

    @Test
    void getAllStudentsTest() throws Exception {
        restTemplate.postForObject(getUrl(), new Student(0L, "Портер", 21), Student.class);
        restTemplate.postForObject(getUrl(), new Student(0L, "Вислый", 25), Student.class);
        restTemplate.postForObject(getUrl(), new Student(0L, "Грыжер", 27), Student.class);

        ResponseEntity<Collection> response = restTemplate.getForEntity(
                getUrl() + "/age?minAge=20&maxAge=25",
                Collection.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(2, response.getBody().size());
    }

    @Test
    void GetStudentFacultyTest() {
        Faculty faculty = new Faculty(0L, "Лизерин", "Желтый");
        Faculty savedFaculty = restTemplate.postForObject(
                "http://localhost:" + port + "/faculty",
                faculty,
                Faculty.class);

        Student student = new Student(0L, "Портер", 21);
        student.setFaculty(savedFaculty);

        Student created = restTemplate.postForObject(getUrl(), student, Student.class);

        ResponseEntity<FacultyDTO> response = restTemplate.getForEntity(
                getUrl() + "/" + created.getId() + "/faculty",
                FacultyDTO.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Лизерин", response.getBody().getName());
        Assertions.assertEquals("Желтый", response.getBody().getColor());

    }








}
