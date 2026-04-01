package com.bookbridge.service;

import com.bookbridge.dto.BookDTO;
import com.bookbridge.model.*;
import com.bookbridge.model.Book.BookStatus;
import com.bookbridge.repository.BookRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {

    private final BookRepository repo;
    public BookService(BookRepository repo) { this.repo = repo; }

    public Book add(User owner, BookDTO dto) {
        Book b = new Book();
        b.setOwner(owner); b.setTitle(dto.getTitle()); b.setAuthor(dto.getAuthor());
        b.setSubject(dto.getSubject()); b.setEdition(dto.getEdition());
        b.setCondition(dto.getCondition() != null ? dto.getCondition() : Book.Condition.GOOD);
        b.setDescription(dto.getDescription()); b.setStatus(BookStatus.AVAILABLE);
        return repo.save(b);
    }

    public List<Book> browse(Long userId)  { return repo.findByStatusAndOwnerIdNot(BookStatus.AVAILABLE, userId); }
    public List<Book> myBooks(Long userId) { return repo.findByOwnerIdOrderByCreatedAtDesc(userId); }
    public List<Book> search(String q, Long userId) { return repo.search(q, userId); }

    public Book update(Long id, User owner, BookDTO dto) {
        Book b = repo.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        if (!b.getOwner().getId().equals(owner.getId())) throw new RuntimeException("Unauthorized");
        b.setTitle(dto.getTitle()); b.setAuthor(dto.getAuthor());
        b.setSubject(dto.getSubject()); b.setEdition(dto.getEdition());
        if (dto.getCondition() != null) b.setCondition(dto.getCondition());
        b.setDescription(dto.getDescription());
        return repo.save(b);
    }

    public void delete(Long id, User owner) {
        Book b = repo.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        if (!b.getOwner().getId().equals(owner.getId())) throw new RuntimeException("Unauthorized");
        repo.delete(b);
    }
}
