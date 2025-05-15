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
import ru.hogwarts.school.DTO.StudentDTO;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;

    @SpyBean
    private FacultyService facultyService;

    @InjectMocks
    private FacultyController facultyController;

    @Test
    void createFacultyTest() throws Exception {
        Faculty faculty = new Faculty(1L, "Лизерин", "Жёлтый");
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        JSONObject facultyJson = new JSONObject();
        facultyJson.put("id", 1);
        facultyJson.put("name", "Лизерин");
        facultyJson.put("color", "Жёлтый");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyJson.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Лизерин"))
                .andExpect(jsonPath("$.color").value("Жёлтый"));
    }

    @Test
    void updateFacultyTest() throws Exception {
        Faculty updatedFaculty = new Faculty(1L, "New Лизерин", "Жёлтый");
        when(facultyRepository.save(any(Faculty.class))).thenReturn(updatedFaculty);

        JSONObject facultyJson = new JSONObject();
        facultyJson.put("id", 1);
        facultyJson.put("name", "New Лизерин");
        facultyJson.put("color", "Жёлтый");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyJson.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Лизерин"));
    }

    @Test
    void getFacultyInfoByIdTest() throws Exception {
        Faculty faculty = new Faculty(1L, "Лизерин", "Жёлтый");
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Лизерин"))
                .andExpect(jsonPath("$.color").value("Жёлтый"));
    }

    @Test
    void getFacultyInfoByInvalidIdTest() throws Exception {
        when(facultyRepository.findById(9999L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteFacultyTest() throws Exception {
        Faculty faculty = new Faculty(1L, "Лизерин", "Жёлтый");
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFacultyNotFoundTest() throws Exception {
        when(facultyRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFacultiesByColorOrNameTest() throws Exception {
        Faculty faculty1 = new Faculty(1L, "Лизерин", "Жёлтый");
        Faculty faculty2 = new Faculty(2L, "Гиффиндуй", "Жёлтый");
        when(facultyRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase("жёлтый", "жёлтый"))
                .thenReturn(Arrays.asList(faculty1, faculty2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/colororname/жёлтый"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Лизерин"))
                .andExpect(jsonPath("$[1].name").value("Гиффиндуй"));
    }

    @Test
    void getFacultyStudentsTest() throws Exception {
        Faculty faculty = new Faculty(1L, "Лизерин", "Жёлтый");
        Student student1 = new Student(1L, "Портер", 21);
        Student student2 = new Student(2L, "Вислый", 21);
        faculty.setStudents(Arrays.asList(student1, student2));

        FacultyDTO facultyDTO = new FacultyDTO(1L, "Лизерин", "Жёлтый");
        facultyDTO.setStudents(Arrays.asList(
                new StudentDTO(1L, "Портер", 21),
                new StudentDTO(2L, "Вислый", 21)
        ));

        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(facultyService.convertToDto(faculty)).thenReturn(facultyDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Портер"))
                .andExpect(jsonPath("$[1].name").value("Вислый"));
    }

    @Test
    void getLongestFacultyNameTest() throws Exception {
        when(facultyRepository.findAll()).thenReturn(Arrays.asList(
                new Faculty(1L, "Короткое", "Красный"),
                new Faculty(2L, "ОченьДлинноеНазваниеФакультета", "Синий")
        ));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/longest-faculty-name"))
                .andExpect(status().isOk())
                .andExpect(content().string("ОченьДлинноеНазваниеФакультета"));
    }
}