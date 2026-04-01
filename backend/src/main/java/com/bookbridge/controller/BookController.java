package com.bookbridge.controller;

import com.bookbridge.dto.BookDTO;
import com.bookbridge.model.*;
import com.bookbridge.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final UserService userService;

    public BookController(BookService bookService, UserService userService) {
        this.bookService = bookService; this.userService = userService;
    }

    // Browse available books (not owned by me)
    @GetMapping("/browse")
    public ResponseEntity<List<Book>> browse(@AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(bookService.browse(user.getId()));
    }

    // Search books
    @GetMapping("/search")
    public ResponseEntity<List<Book>> search(@RequestParam String q,
                                              @AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(bookService.search(q, user.getId()));
    }

    // My listed books
    @GetMapping("/mine")
    public ResponseEntity<List<Book>> mine(@AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(bookService.myBooks(user.getId()));
    }

    // Add new book
    @PostMapping
    public ResponseEntity<Book> add(@AuthenticationPrincipal UserDetails ud,
                                    @RequestBody BookDTO dto) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(bookService.add(user, dto));
    }

    // Update book
    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@AuthenticationPrincipal UserDetails ud,
                                       @PathVariable Long id,
                                       @RequestBody BookDTO dto) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(bookService.update(id, user, dto));
    }

    // Delete book
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@AuthenticationPrincipal UserDetails ud,
                                                       @PathVariable Long id) {
        User user = userService.getByEmail(ud.getUsername());
        bookService.delete(id, user);
        return ResponseEntity.ok(Map.of("message", "Book removed"));
    }
}
