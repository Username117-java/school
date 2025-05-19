package ru.hogwarts.school.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.DTO.FacultyDTO;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private AvatarRepository avatarRepository;

    @MockBean
    private AvatarService avatarService;

    @MockBean
    private FacultyService facultyService;

    @SpyBean
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    @Test
    void createStudentTest() throws Exception {
        Student student = new Student(1L, "Портер", 21);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        JSONObject studentJson = new JSONObject();
        studentJson.put("name", "Портер");
        studentJson.put("age", 21);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Портер"))
                .andExpect(jsonPath("$.age").value(21));
    }

    @Test
    void getStudentInfoByIdTest() throws Exception {
        Student student = new Student(1L, "Портер", 21);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Портер"))
                .andExpect(jsonPath("$.age").value(21));
    }

    @Test
    void getStudentInfoByInvalidIdTest() throws Exception {
        when(studentRepository.findById(9999L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStudentTest() throws Exception {
        Student updatedStudent = new Student(1L, "New Портер", 22);
        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);
        when(studentRepository.existsById(1L)).thenReturn(true);

        JSONObject studentJson = new JSONObject();
        studentJson.put("id", 1);
        studentJson.put("name", "New Портер");
        studentJson.put("age", 22);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student")
                        .content(studentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Портер"));
    }

    @Test
    void deleteStudentTest() throws Exception {
        Student student = new Student(1L, "Портер", 21);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getStudentsByAgeBetweenTest() throws Exception {
        Student student1 = new Student(1L, "Портер", 21);
        Student student2 = new Student(2L, "Вислый", 25);
        when(studentRepository.findByAgeBetween(20, 25))
                .thenReturn(Arrays.asList(student1, student2));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/age?minAge=20&maxAge=25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Портер"))
                .andExpect(jsonPath("$[1].name").value("Вислый"));
    }

    @Test
    void getStudentFacultyTest() throws Exception {
        Faculty faculty = new Faculty(1L, "Лизерин", "Желтый");
        Student student = new Student(1L, "Портер", 21);
        student.setFaculty(faculty);

        FacultyDTO facultyDTO = new FacultyDTO(1L, "Лизерин", "Желтый");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(facultyService.convertToDto(faculty)).thenReturn(facultyDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Лизерин"))
                .andExpect(jsonPath("$.color").value("Желтый"));
    }
}