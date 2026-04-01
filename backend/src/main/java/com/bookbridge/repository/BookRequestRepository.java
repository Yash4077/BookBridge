package com.bookbridge.repository;
import com.bookbridge.model.BookRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {
    // Requests made by this user
    List<BookRequest> findByRequesterIdOrderByRequestedAtDesc(Long requesterId);

    // Requests received by this user (requests on their books)
    List<BookRequest> findByBookOwnerIdOrderByRequestedAtDesc(Long ownerId);

    // Check if user already requested this book
    boolean existsByBookIdAndRequesterId(Long bookId, Long requesterId);
}
