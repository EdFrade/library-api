package com.fratris.libraryapi.api.exception;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

public class ApiErrors extends RuntimeException{

    private List<String> errors;

    public ApiErrors(BindingResult bidding){
        this.errors = new ArrayList();
        bidding.getAllErrors().forEach( error -> errors.add(error.getDefaultMessage()));
    }

    public List<String> getErrors() {
        return errors;
    }

}
