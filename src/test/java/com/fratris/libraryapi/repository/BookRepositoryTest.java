package com.fratris.libraryapi.repository;

import com.fratris.libraryapi.model.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    BookRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    @DisplayName("Deve retornar verdadeiro quando houver livro na base de dados com o isbn informado")
    public void returnTrueWhenIsbnExist(){
        String isbn = "123";
        Book book = Book.builder().title("Batman").author("Joao batista").isbn(isbn).build();
        entityManager.persist(book);

        boolean result = repository.existsByIsbn(isbn);

        Assertions.assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não houver livro na base de dados com o isbn informado")
    public void returnTrueWhenIsbnDoesntExist(){
        String isbn = "123";

        boolean result = repository.existsByIsbn(isbn);

        Assertions.assertThat(result).isFalse();
    }

}
