package com.ht_cinema.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ht_cinema.model.Comment;
import com.ht_cinema.repository.CommentRepository;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getComments(Integer filmId) {
        return commentRepository.findByFilmIdOrderByCreatedAtDesc(filmId);
    }

    public Long getCount(Integer filmId) {
        return commentRepository.countByFilmId(filmId);
    }

    public void addComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }
}
