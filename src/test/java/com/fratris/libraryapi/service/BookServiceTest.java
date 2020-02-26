package com.fratris.libraryapi.service;

import com.fratris.libraryapi.exception.BusinessException;
import com.fratris.libraryapi.model.entity.Book;
import com.fratris.libraryapi.repository.BookRepository;
import com.fratris.libraryapi.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl( repository );
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);


        Mockito.when(repository.save(book)).
                thenReturn(com.fratris.libraryapi.model.entity.Book.builder()
                        .id(1l)
                        .author("Clebinho")
                        .isbn("1234")
                        .title("As aventuras de clebinho").build());

        com.fratris.libraryapi.model.entity.Book savedBook = service.save( book );

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getAuthor()).isEqualTo("Clebinho");
        assertThat(savedBook.getIsbn()).isEqualTo("1234");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras de clebinho");
    }

    @Test
    @DisplayName("Deve lançar erro quando tentar cadastrar livro com isbn duplicado")
    public void shouldNotSaveDuplicatedBook(){
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        Throwable exception = catchThrowable(() -> service.save(book));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve retornar o livro com o id informado")
    public void getByIdTest(){
        Long id = 1l;

        com.fratris.libraryapi.model.entity.Book book = createValidBook();
        book.setId(id);
        BDDMockito.given(repository.findById(id)).willReturn(Optional.of(book));

        Optional<com.fratris.libraryapi.model.entity.Book> bookFound = service.getById(id);

        assertThat(bookFound.isPresent()).isTrue();
        assertThat(bookFound.get().getId()).isEqualTo(book.getId());
        assertThat(bookFound.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(bookFound.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(bookFound.get().getIsbn()).isEqualTo(book.getIsbn());

    }

 @Test
    @DisplayName("Deve atualizar um livro")
    public  void updateBookTest(){
        Long id = 1l;
        Book updatingBook = Book.builder().id(id).build();
        Book bookUpdated = Book.builder().id(id).author("Eduardo").title("As cronicas").isbn("555").build();

        BDDMockito.given(repository.save(updatingBook)).willReturn(bookUpdated);

        Book book = service.updateById(updatingBook);

        assertThat(book.getId()).isEqualTo(id);
        assertThat(book.getAuthor()).isEqualTo(bookUpdated.getAuthor());
        assertThat(book.getTitle()).isEqualTo(bookUpdated.getTitle());
        assertThat(book.getIsbn()).isEqualTo(bookUpdated.getIsbn());


    }

    @Test
    @DisplayName("Deve lançar erro quando tentar atualizar um livro nulo ou id nulo")
    public  void updateInvalidBookTest(){
        Book book = new Book();
        Assertions.assertThrows(IllegalArgumentException.class,() -> service.updateById(book));
        Mockito.verify(repository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){
        Long id = 1l;
        Book book = createValidBook();
        book.setId(id);
        service.delete(book);

        Mockito.verify(repository, Mockito.times(1)).delete(book);

    }

    @Test
    @DisplayName("Deve lançar um erro ao tentar deletar um livro inexistente")
    public void deleteInexistentBookTest(){
        Book book = new Book();

        Assertions.assertThrows(IllegalArgumentException.class,() -> service.delete(book));


        Mockito.verify(repository, Mockito.never()).delete(book);

    }



    public Book createValidBook(){
        return Book.builder().author("Clebinho").isbn("1234").title("As aventuras de clebinho").build();
    }

}
