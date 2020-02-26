package com.fratris.libraryapi.api.resource;

import com.fratris.libraryapi.api.dto.BookDTO;
import com.fratris.libraryapi.api.exception.ApiErrors;
import com.fratris.libraryapi.model.entity.Book;
import com.fratris.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    BookService service;

    ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper modelMapper){
        this.modelMapper = modelMapper;
        this.service = service;
    }

    @GetMapping("{id}")
    public BookDTO getBookDetails(@PathVariable Long id){
        return service.getById(id)
                .map(book -> modelMapper.map(book,BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO createBook(@RequestBody @Valid BookDTO dto){
        Book book = modelMapper.map(dto, Book.class);
        Book savedBook = service.save(book);

        return modelMapper.map( savedBook, BookDTO.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        return new ApiErrors(bindingResult);
    }
}
