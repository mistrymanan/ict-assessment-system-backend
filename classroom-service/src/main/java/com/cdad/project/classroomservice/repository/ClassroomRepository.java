package com.cdad.project.classroomservice.repository;

import com.cdad.project.classroomservice.entity.Classroom;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ClassroomRepository extends MongoRepository<Classroom, String> {
    boolean existsBySlug(String slug);

    Optional<Classroom> getClassroomBySlug(String slug);

    void deleteById(ObjectId id);
}
