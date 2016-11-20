package ru.dasha;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.RowEditEvent;
import ru.dasha.book.model.Author;
import ru.dasha.book.model.Book;
import ru.dasha.book.model.Genre;
import ru.dasha.dbsettings.DBSettings;

@ManagedBean(name = "book")
@ViewScoped
public class BookBean implements Serializable {
    private List<Book> books;
    private Book newBook = new Book();

    public Book getNewBook() {
        return newBook;
    }

    public void setNewBook(Book newBook) {
        this.newBook = newBook;
    }

    public List<Book> getBooks() {
        return books;
    }

    public BookBean() {
        DBSettings.dbInit();
    }

    @PostConstruct
    public void init() {
        books = getBookList();
    }

    public List<Book> getBookList() {
        List<Book> list = new ArrayList<Book>();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;

        String sql = "SELECT book.id, book.name, book.description, author.name author, genre.name genre, book.author_id, book.genre_id " +
                "FROM book " +
                "JOIN author ON book.author_id = author.id " +
                "JOIN genre ON book.genre_id = genre.id";
        try {
            con = DBSettings.getConnection();
            ps = con.prepareStatement(sql);
            res = ps.executeQuery();

            while (res.next()) {
                Book book = new Book();
                Author author = new Author();
                Genre genre = new Genre();

                book.setId(res.getInt("id"));
                book.setName(res.getString("name"));
                book.setDescription(res.getString("description"));

                author.setId(res.getInt("author_id"));
                author.setName(res.getString("author"));
                book.setAuthor(author);

                genre.setId(res.getInt("genre_id"));
                genre.setName(res.getString("genre"));
                book.setGenre(genre);

                list.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBSettings.closeAll(con, ps, res);
        }
        return list;
    }

    public Book getBook(int bookId) {
        Book book = new Book();
        Author author = new Author();
        Genre genre = new Genre();

        PreparedStatement ps = null;
        Connection con = null;
        ResultSet res = null;
        String sql = "SELECT book.id, book.name, book.description, author.name author, author_id, genre.name genre, genre_id " +
                "FROM book " +
                "JOIN author ON book.author_id = author.id " +
                "JOIN genre ON book.genre_id = genre.id " +
                "WHERE book.id = ? " +
                "LIMIT 1";
        try {
            con = DBSettings.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, bookId);
            res = ps.executeQuery();

            if (res.next()) {

                book.setId(res.getInt("id"));
                book.setName(res.getString("name"));
                book.setDescription(res.getString("description"));

                author.setId(res.getInt("author_id"));
                author.setName(res.getString("author"));
                book.setAuthor(author);

                genre.setId(res.getInt("genre_id"));
                genre.setName(res.getString("genre"));
                book.setGenre(genre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBSettings.closeAll(con, ps, res);
        }
        return book;
    }

    public void createBook(ActionEvent event) {
        int newId;

        FacesMessage messageSuccess = new FacesMessage(FacesMessage.SEVERITY_INFO, "Книга успешно создана", newBook.getName());
        FacesMessage messageFail = new FacesMessage(FacesMessage.SEVERITY_INFO, "Книга почему-то не была создана", null);

        PreparedStatement ps = null;
        Connection con = null;
        ResultSet res = null;

        String sql = "INSERT INTO book(name, description, author_id, genre_id) VALUES(?,?,?,?)";
        try {
            con = DBSettings.getConnection();
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, newBook.getName());
            ps.setString(2, newBook.getDescription());
            ps.setInt(3, newBook.getAuthor().getId());
            ps.setInt(4, newBook.getGenre().getId());

            newId = ps.executeUpdate();
            if (newId == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            res = ps.getGeneratedKeys();
            if (res.next()) {
                newBook.setId(res.getInt(1));
            }

            books.add(getBook(newBook.getId()));

            FacesContext.getCurrentInstance().addMessage(null, messageSuccess);
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, messageFail);
        } finally {
            DBSettings.closeAll(con, ps, res);
        }
    }

    public void updateBook(RowEditEvent event) {
        Book editedObject = (Book) event.getObject();

        FacesMessage messageSuccess = new FacesMessage("Книга изменена", ((Book) event.getObject()).getName());

        PreparedStatement ps = null;
        Connection con = null;
        ResultSet res = null;

        String sql = "UPDATE book SET name = ?, description = ?, author_id = ?, genre_id = ? WHERE id = ?";
        try {
            con = DBSettings.getConnection();
            ps = con.prepareStatement(sql);

            ps.setString(1, editedObject.getName());
            ps.setString(2, editedObject.getDescription());
            ps.setInt(3, editedObject.getAuthor().getId());
            ps.setInt(4, editedObject.getGenre().getId());
            ps.setInt(5, editedObject.getId());

            ps.executeUpdate();

            FacesContext.getCurrentInstance().addMessage(null, messageSuccess);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBSettings.closeAll(con, ps, res);
        }
    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage messageCancel = new FacesMessage("Изменение отменено", ((Book) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage(null, messageCancel);
    }

    public void deleteBook(Book book) {
        FacesMessage messageSuccess = new FacesMessage(FacesMessage.SEVERITY_INFO, "Книга удалена", null);

        PreparedStatement ps = null;
        Connection con = null;
        ResultSet res = null;

        String sql = "DELETE FROM book WHERE id = ?";
        try {
            con = DBSettings.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, book.getId());

            ps.executeUpdate();

            books.remove(book);

            FacesContext.getCurrentInstance().addMessage(null, messageSuccess);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBSettings.closeAll(con, ps, res);
        }
    }
}
