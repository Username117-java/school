SELECT student.name, student.age, faculty.name
FROM student
LEFT JOIN faculty ON student.faculty_id = faculty.id;

SELECT student.name, student.age
FROM student
INNER JOIN avatar on student.id = avatar.student_id;