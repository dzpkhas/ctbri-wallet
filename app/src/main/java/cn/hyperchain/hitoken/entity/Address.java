package cn.hyperchain.hitoken.entity;

import java.util.List;

/**
 * Created by chen on 2017/11/12.
 */

public class Address {

    List<AddressBook> books;
    List<AddressRecent> recent;

    public List<AddressBook> getBooks() {
        return books;
    }

    public void setBooks(List<AddressBook> books) {
        this.books = books;
    }

    public List<AddressRecent> getRecent() {
        return recent;
    }

    public void setRecent(List<AddressRecent> recent) {
        this.recent = recent;
    }
}
