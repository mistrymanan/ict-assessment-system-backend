package com.cdad.project.classroomservice.classroomservice.repository;

import com.cdad.project.classroomservice.classroomservice.entity.Classroom;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClassroomRepository extends MongoRepository<Classroom,String> {
}
