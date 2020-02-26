package com.fratris.libraryapi.service.impl;

import com.fratris.libraryapi.exception.BusinessException;
import com.fratris.libraryapi.model.entity.Book;
import com.fratris.libraryapi.repository.BookRepository;
import com.fratris.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }


    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())){
            throw new BusinessException("Isbn já cadastrado");
        }

        return repository.save(book);
    }
}
