package ru.hogwarts.school.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.hogwarts.school.DTO.FacultyDTO;
import ru.hogwarts.school.DTO.StudentDTO;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

@Service
public class FacultyService {
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        logger.debug("Was invoked method for find faculty with id {}", id);
        return facultyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Faculty not found"));
    }

    public Faculty editFaculty(Faculty faculty) {
        logger.info("Was invoked method for update faculty with id {}", faculty.getId());
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        logger.info("Was invoked method for delete faculty with id {}", id);
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> findByColorOrName(String part) {
        logger.debug("Was invoked method for find faculty by color or name with part {}", part);
        return facultyRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(part, part);
    }

    public Collection<Faculty> findAll() {
        logger.debug("Was invoked method for find all faculties");
        return Collections.unmodifiableCollection(facultyRepository.findAll());
    }

    public String getLongestFacultyName() {
        return facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse("No faculties");

    }

    public FacultyDTO convertToDto(Faculty faculty) {
        FacultyDTO facultyDTO = new FacultyDTO(
                faculty.getId(),
                faculty.getName(),
                faculty.getColor()
        );

        if (faculty.getStudents() != null) {
            List<StudentDTO> studentDTOs = faculty.getStudents().stream()
                    .map(student -> new StudentDTO(
                            student.getId(),
                            student.getName(),
                            student.getAge()
                    ))
                    .collect(Collectors.toList());
            facultyDTO.setStudents(studentDTOs);
        }

        return facultyDTO;
    }
}