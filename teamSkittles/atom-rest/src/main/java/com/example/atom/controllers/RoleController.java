package com.example.atom.controllers;

import com.example.atom.dto.RoleDto;
import com.example.atom.entities.Process;
import com.example.atom.readers.ProcessReader;
import com.example.atom.services.ProcessService;
import com.example.atom.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("role")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/get-data/id/{id}")
    public RoleDto getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    @GetMapping("/get-data/name/{name}")
    public RoleDto getRoleByName(@PathVariable String name) {
        return roleService.getRoleByName(name);
    }

    @GetMapping("/get-data")
    public List<RoleDto> getListRole() {
        return roleService.getListRole();
    }

    @GetMapping("/get-data-as-map/id")
    public Map<Long, RoleDto> getMapRoleToId() {
        return roleService.getMapRoleToId();
    }

    @GetMapping("/get-data-as-map/name")
    public Map<String, RoleDto> getMapRoleToName() {
        return roleService.getMapRoleToName();
    }
}
