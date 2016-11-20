package ru.dasha.book.model;

import java.io.Serializable;

public class Author implements Serializable {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Author) && (id != 0)
                ? id == ((Author) other).id
                : (other == this);
    }

    @Override
    public String toString() {
        return String.format("Author[%d, %s]", id, name);
    }
}
