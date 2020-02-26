package com.fratris.libraryapi.service;


import com.fratris.libraryapi.model.entity.Book;

import java.util.Optional;

public interface BookService {
    public Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book book);

    Book updateById(Book book);
}
