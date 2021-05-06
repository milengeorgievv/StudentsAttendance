package org.vaadin.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.vaadin.example.dao.PlatformRepository;
import org.vaadin.example.entity.PlatformEntity;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/platforms")
public class PlatformController {

    @Autowired
    private PlatformRepository platformRepository;

    @GetMapping
    public List<PlatformEntity> getAllPlatforms() {
        List<PlatformEntity> platforms = platformRepository.findAll();
        return platforms;
    }

    @GetMapping("/{id}")
    public PlatformEntity getPlatformById(@PathVariable Long id) {
        Optional<PlatformEntity> platform = platformRepository.findById(id);
        return platform.get();
    }

    @PostMapping
    public PlatformEntity createNewPlatform(@RequestBody PlatformEntity newPlatform) {
        PlatformEntity createdPlatform = platformRepository.save(newPlatform);
        return createdPlatform;
    }

    @DeleteMapping
    public void deleteAllPlatforms() {
        platformRepository.deleteAll();
    }
}

