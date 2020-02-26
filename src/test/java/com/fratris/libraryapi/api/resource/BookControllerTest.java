package com.fratris.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fratris.libraryapi.api.dto.BookDTO;
import com.fratris.libraryapi.service.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    private static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBookTest() throws Exception {

        BookDTO dto = createNewDtoBook();
        com.fratris.libraryapi.model.entity.Book savedBook = createValidBook();

        BDDMockito.given(service.save(Mockito.any(com.fratris.libraryapi.model.entity.Book.class))).willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc
                .perform( request )
                .andExpect( status().isCreated() )
                .andExpect( MockMvcResultMatchers.jsonPath("id").value(101L) )
                .andExpect( MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()) )
                .andExpect( MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()) )
                .andExpect( MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()) );
    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criação do livro")
    public void createInvalidBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new com.fratris.libraryapi.model.entity.Book());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("Deve retornar informações do livro com o id informado")
    public void getDetailsBookTest() throws Exception {
        Long id = 1L;
        com.fratris.libraryapi.model.entity.Book book = com.fratris.libraryapi.model.entity.Book.builder().id(id).author("Eduardo").title("O Batman").isbn("123").build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API + "/"+id)
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect( MockMvcResultMatchers.jsonPath("id").value(1))
                .andExpect( MockMvcResultMatchers.jsonPath("author").value("Eduardo"))
                .andExpect( MockMvcResultMatchers.jsonPath("title").value("O Batman"))
                .andExpect( MockMvcResultMatchers.jsonPath("isbn").value("123"));

    }

    @Test
    @DisplayName("Deve retornar resource not a found quando livro não for encontrado")
    public void BookNotFound() throws Exception {
        Long id = 1L;

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API+"/"+id)
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar o livro com o id informado")
    public void deleteExistentBookTest() throws Exception {
        Long id = 1l;
        com.fratris.libraryapi.model.entity.Book book = com.fratris.libraryapi.model.entity.Book.builder().id(id).build();
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API + "/" + id);

        mvc
                .perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar resource not found quando não encontrar o livro para deletar")
    public void deleteInexistentBookTest() throws Exception {

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API + "/" + 1);

        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void  updateBookTest() throws Exception {
        Long id = 101l;
        BookDTO dtoBook = BookDTO.builder().author("Fulano").title("Cruzadas").isbn("888").build();
        String json = new ObjectMapper().writeValueAsString(dtoBook);
        com.fratris.libraryapi.model.entity.Book updatedBook = com.fratris.libraryapi.model.entity.Book.builder().id(id).author("Fulano").title("Cruzadas").isbn("888").build();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(createValidBook()));
        BDDMockito.given(service.updateById(Mockito.any())).willReturn(updatedBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("author").value("Fulano"))
                .andExpect(jsonPath("title").value("Cruzadas"))
                .andExpect(jsonPath("isbn").value("888"));

    }


    @Test
    @DisplayName("Deve retornar resource not found quando tentar atualizar um livro não cadastrado")
    public void  updateInexistentBookTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(createNewDtoBook());

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API + "/" + Mockito.anyLong())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isNotFound());

    }

    public com.fratris.libraryapi.model.entity.Book createValidBook(){
        return  com.fratris.libraryapi.model.entity.Book.builder().id(101L).author("Eduardo").title("My Life").isbn("101").build();
    }

    public BookDTO createNewDtoBook(){
        return  BookDTO.builder().author("Eduardo").title("My Life").isbn("101").build();

    }

}
