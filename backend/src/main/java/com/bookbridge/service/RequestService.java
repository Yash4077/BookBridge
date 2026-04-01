package com.bookbridge.service;

import com.bookbridge.dto.RequestDTO;
import com.bookbridge.model.*;
import com.bookbridge.model.BookRequest.RequestStatus;
import com.bookbridge.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestService {

    private final BookRequestRepository reqRepo;
    private final BookRepository bookRepo;

    public RequestService(BookRequestRepository reqRepo, BookRepository bookRepo) {
        this.reqRepo = reqRepo; this.bookRepo = bookRepo;
    }

    public BookRequest create(User requester, RequestDTO dto) {
        Book book = bookRepo.findById(dto.getBookId())
            .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getOwner().getId().equals(requester.getId()))
            throw new RuntimeException("You cannot request your own book");
        if (book.getStatus() != Book.BookStatus.AVAILABLE)
            throw new RuntimeException("Book is not available");
        if (reqRepo.existsByBookIdAndRequesterId(book.getId(), requester.getId()))
            throw new RuntimeException("Already requested");

        BookRequest r = new BookRequest();
        r.setBook(book); r.setRequester(requester);
        r.setMessage(dto.getMessage()); r.setStatus(RequestStatus.PENDING);
        book.setStatus(Book.BookStatus.REQUESTED);
        bookRepo.save(book);
        return reqRepo.save(r);
    }

    // Requests I MADE
    public List<BookRequest> mySent(Long userId) {
        return reqRepo.findByRequesterIdOrderByRequestedAtDesc(userId);
    }

    // Requests RECEIVED on my books
    public List<BookRequest> myReceived(Long userId) {
        return reqRepo.findByBookOwnerIdOrderByRequestedAtDesc(userId);
    }

    // Owner accepts a request
    public BookRequest accept(Long reqId, User owner) {
        BookRequest r = getAndVerifyOwner(reqId, owner);
        r.setStatus(RequestStatus.ACCEPTED);
        r.setUpdatedAt(LocalDateTime.now());
        r.getBook().setStatus(Book.BookStatus.LENT);
        bookRepo.save(r.getBook());
        return reqRepo.save(r);
    }

    // Owner rejects a request
    public BookRequest reject(Long reqId, User owner) {
        BookRequest r = getAndVerifyOwner(reqId, owner);
        r.setStatus(RequestStatus.REJECTED);
        r.setUpdatedAt(LocalDateTime.now());
        r.getBook().setStatus(Book.BookStatus.AVAILABLE);
        bookRepo.save(r.getBook());
        return reqRepo.save(r);
    }

    // Mark as returned
    public BookRequest markReturned(Long reqId, User owner) {
        BookRequest r = getAndVerifyOwner(reqId, owner);
        r.setStatus(RequestStatus.RETURNED);
        r.setUpdatedAt(LocalDateTime.now());
        r.getBook().setStatus(Book.BookStatus.AVAILABLE);
        bookRepo.save(r.getBook());
        return reqRepo.save(r);
    }

    private BookRequest getAndVerifyOwner(Long reqId, User owner) {
        BookRequest r = reqRepo.findById(reqId)
            .orElseThrow(() -> new RuntimeException("Request not found"));
        if (!r.getBook().getOwner().getId().equals(owner.getId()))
            throw new RuntimeException("Unauthorized");
        return r;
    }
}
