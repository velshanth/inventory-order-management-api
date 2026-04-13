package com.guvi.error;

public class DuplicateEmailException extends RuntimeException{
    public DuplicateEmailException(String email){
        super("Email alread exists: " + email);
    }
}
