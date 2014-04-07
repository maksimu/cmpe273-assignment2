package edu.sjsu.cmpe.procurementv2.dto;

import lombok.Data;

import java.util.List;

/**
 * User: maksim
 * Date: 4/5/14 - 11:48 AM
 */
@Data
public class ShippedBooks {
     List<Book> shipped_books;
}
