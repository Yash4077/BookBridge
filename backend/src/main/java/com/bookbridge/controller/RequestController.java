package com.bookbridge.controller;

import com.bookbridge.dto.RequestDTO;
import com.bookbridge.model.*;
import com.bookbridge.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService requestService;
    private final UserService userService;

    public RequestController(RequestService requestService, UserService userService) {
        this.requestService = requestService; this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<BookRequest> create(@AuthenticationPrincipal UserDetails ud,
                                               @RequestBody RequestDTO dto) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(requestService.create(user, dto));
    }

    @GetMapping("/sent")
    public ResponseEntity<List<BookRequest>> sent(@AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(requestService.mySent(user.getId()));
    }

    @GetMapping("/received")
    public ResponseEntity<List<BookRequest>> received(@AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(requestService.myReceived(user.getId()));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<BookRequest> accept(@AuthenticationPrincipal UserDetails ud,
                                               @PathVariable Long id) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(requestService.accept(id, user));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<BookRequest> reject(@AuthenticationPrincipal UserDetails ud,
                                               @PathVariable Long id) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(requestService.reject(id, user));
    }

    @PatchMapping("/{id}/returned")
    public ResponseEntity<BookRequest> returned(@AuthenticationPrincipal UserDetails ud,
                                                 @PathVariable Long id) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(requestService.markReturned(id, user));
    }
}
