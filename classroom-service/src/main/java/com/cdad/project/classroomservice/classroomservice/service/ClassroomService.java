package com.cdad.project.classroomservice.classroomservice.service;

import com.cdad.project.classroomservice.classroomservice.repository.ClassroomRepository;

public class ClassroomService {
    final private ClassroomRepository classroomRepository;

    public ClassroomService(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }
}
