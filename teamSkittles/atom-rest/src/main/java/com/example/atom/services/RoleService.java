package com.example.atom.services;

import com.example.atom.dto.RoleDto;
import com.example.atom.entities.Role;
import com.example.atom.repositories.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleDto getRoleById(Long id) {
        Role role = roleRepository.findById(id).orElse(null);
        if(role == null) {
            throw new RuntimeException("В базе не найдена запись с данным id");
        }
        return new RoleDto(role);
    }

    public RoleDto getRoleByName(String name) {
        Role role = roleRepository.findByName(name);
        if(role == null) {
            throw new RuntimeException("В базе не найдена запись с данным id");
        }
        return new RoleDto(role);
    }

    public List<RoleDto> getListRole() {
        List<Role> roleList = roleRepository.findAll();
        return roleList
                .stream()
                .sorted(Comparator.comparing(Role::getId))
                .map(RoleDto::new)
                .collect(Collectors.toList());
    }

    public Map<Long, RoleDto> getMapRoleToId() {
        List<Role> roleList = roleRepository.findAll();
        return roleList
                .stream()
                .map(RoleDto::new)
                .collect(Collectors.toMap(RoleDto::getId, Function.identity()));
    }

    public Map<String, RoleDto> getMapRoleToName() {
        List<Role> roleList = roleRepository.findAll();
        return roleList
                .stream()
                .map(RoleDto::new)
                .collect(Collectors.toMap(RoleDto::getName, Function.identity()));
    }

    @PostConstruct
    public void initializeRoles() {
        List<Role> roles = roleRepository.findAll();
        if (roles.isEmpty()) {
            this.generateRoles();
        }
    }

    private void generateRoles() {
        List<Role> roles = new ArrayList<>();

        Role role1 = new Role("admin", "Роль администратора");
        roles.add(role1);

        Role role2 = new Role("user", "Роль дефолтного юзера");
        roles.add(role2);

        Role role3 = new Role("chief", "Роль начальника");
        roles.add(role3);

        Role role4 = new Role("contragent", "Роль заказчика");
        roles.add(role4);

        roleRepository.saveAll(roles);
    }
}
