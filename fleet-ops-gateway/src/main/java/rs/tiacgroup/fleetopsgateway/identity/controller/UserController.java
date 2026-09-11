package rs.tiacgroup.fleetopsgateway.identity.controller;


import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.tiacgroup.fleetopsgateway.identity.dto.request.CreateUserRequest;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.UserResponse;
import rs.tiacgroup.fleetopsgateway.identity.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<UserResponse> listUsers(
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return userService.listUsers(pageable);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}