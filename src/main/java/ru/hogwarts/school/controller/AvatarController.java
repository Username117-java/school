package ru.hogwarts.school.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.StudentService;

@RestController
@RequestMapping("/avatars")
public class AvatarController {

    StudentService studentService;

    public AvatarController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<Page<Avatar>> getAllAvatars(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Avatar> avatars = studentService.getAllAvatars(pageable);
        return ResponseEntity.ok(avatars);
    }
}
