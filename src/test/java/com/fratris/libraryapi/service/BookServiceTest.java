package com.fratris.libraryapi.service;

import com.fratris.libraryapi.exception.BusinessException;
import com.fratris.libraryapi.model.entity.Book;
import com.fratris.libraryapi.repository.BookRepository;
import com.fratris.libraryapi.service.impl.BookServiceImpl;
import com.jayway.jsonpath.JsonPath;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
                thenReturn(Book.builder()
                        .id(1l)
                        .author("Clebinho")
                        .isbn("1234")
                        .title("As aventuras de clebinho").build());

        Book savedBook = service.save( book );

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

        Throwable exception = Assertions.catchThrowable(() -> service.save(book));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    public Book createValidBook(){
        return Book.builder().author("Clebinho").isbn("1234").title("As aventuras de clebinho").build();
    }

}
