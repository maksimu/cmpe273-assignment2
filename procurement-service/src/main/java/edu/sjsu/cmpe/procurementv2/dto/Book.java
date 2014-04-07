package edu.sjsu.cmpe.procurementv2.dto;


import lombok.Data;

@Data
public class Book {
    private long isbn;
    private String title;
    private String category;
    private String coverimage;

    // add more fields here
}
