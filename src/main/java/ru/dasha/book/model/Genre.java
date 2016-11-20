package ru.dasha.book.model;

public class Genre {
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
        return (other instanceof Genre) && (id != 0)
                ? id == ((Genre) other).id
                : (other == this);
    }

    @Override
    public String toString() {
        return String.format("Genre[%d, %s]", id, name);
    }
}
