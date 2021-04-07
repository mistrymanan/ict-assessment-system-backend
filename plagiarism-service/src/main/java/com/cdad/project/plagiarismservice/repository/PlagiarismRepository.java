package com.cdad.project.plagiarismservice.repository;

import com.cdad.project.plagiarismservice.entity.Plagiarism;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlagiarismRepository extends MongoRepository<Plagiarism,String> {
}
