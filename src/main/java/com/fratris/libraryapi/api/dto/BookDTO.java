package com.fratris.libraryapi.api.dto;

import com.fratris.libraryapi.model.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
    private Long id;
    @NotEmpty
    private String author;
    @NotEmpty
    private String title;
    @NotEmpty
    private String isbn;


}
