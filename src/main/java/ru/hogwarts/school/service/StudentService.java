package ru.hogwarts.school.service;


import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);


    private StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        logger.info("Was invoked method for create student");
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        logger.debug("Was invoked method for find student by id = {}", id);
        return studentRepository.findById(id).orElseThrow(() -> {
            logger.error("Not found student with id = {}", id);
            return new RuntimeException("Not found student with id = " + id);
        });
    }

    public Student editStudent(Student student) {
        logger.info("Was invoked method for edit student by id = {}", student.getId());
        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        logger.info("Was invoked method for delete student by id = {}", id);
        studentRepository.deleteById(id);
    }

    public Collection<Student> findByAge(int age) {
        logger.debug("Was invoked method for find student by age {}", age);
        return studentRepository.findByAge(age);
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        logger.debug("Was invoked method for find student by age between {} and {}", min, max);
        return studentRepository.findByAgeBetween(min, max);
    }

    public int getStudentsCount() {
        logger.info("Was invoked method for get student count");
        return studentRepository.countAllStudents();
    }

    public double getAverageAge() {
        logger.info("Was invoked method for get student average age");
        return studentRepository.findAvgAge();
    }

    public Collection<Student> getLastFiveStudents() {
        logger.debug("Was invoked method for get last five students");
        return studentRepository.findLastFiveStudents();
    }


}