package com.fratris.libraryapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_book")
public class Book {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    @Column(name = "id_book")
    private Long id;
    @Column(name = "nm_autor")
    private String author;
    @Column(name = "ds_title")
    private String title;
    @Column(name = "ds_isbn")
    private String isbn;

}
