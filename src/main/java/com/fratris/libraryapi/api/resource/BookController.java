package com.fratris.libraryapi.api.resource;

import com.fratris.libraryapi.api.dto.BookDTO;
import com.fratris.libraryapi.api.exception.ApiErrors;
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
    public BookDTO get(@PathVariable Long id){
        return service.getById(id)
                .map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto){
        com.fratris.libraryapi.model.entity.Book book = modelMapper.map(dto, com.fratris.libraryapi.model.entity.Book.class);
        com.fratris.libraryapi.model.entity.Book savedBook = service.save(book);

        return modelMapper.map( savedBook, BookDTO.class);
    }
    
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        com.fratris.libraryapi.model.entity.Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);

    }

    @PutMapping("{id}")
    public BookDTO update(@PathVariable Long id, @RequestBody BookDTO updatedBook){

        return  service.getById(id).map(book ->{
            book.setAuthor(updatedBook.getAuthor());
            book.setTitle(updatedBook.getTitle());

            return modelMapper.map(service.updateById(book), BookDTO.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        return new ApiErrors(bindingResult);
    }
}
