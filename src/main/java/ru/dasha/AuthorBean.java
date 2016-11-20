package ru.dasha;

import ru.dasha.book.model.Author;
import ru.dasha.dbsettings.DBSettings;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "author")
@ApplicationScoped
public class AuthorBean implements Serializable {
    private List<Author> authors;
    private Author newAuthor = new Author();

    public Author getNewAuthor() {
        return newAuthor;
    }

    public void setNewAuthor(Author newAuthor) {
        this.newAuthor = newAuthor;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public AuthorBean() {
        DBSettings.dbInit();
    }

    @PostConstruct
    public void init() {
        authors = getAuthorList();
    }

    public List<Author> getAuthorList() {
        List<Author> list = new ArrayList<Author>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;

        try {
            con = DBSettings.getConnection();
            ps = con.prepareStatement("SELECT id, name FROM author");
            res = ps.executeQuery();

            while (res.next()) {
                Author author = new Author();

                author.setId(res.getInt("id"));
                author.setName(res.getString("name"));
                list.add(author);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBSettings.closeAll(con, ps, res);
        }
        return list;
    }

    public void createAuthor(ActionEvent event) {
        FacesMessage messageSuccess = new FacesMessage(FacesMessage.SEVERITY_INFO, "Автор успешно создан", newAuthor.getName());
        FacesMessage messageFail = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Автор почему-то не был создан", null);

        PreparedStatement ps = null;
        Connection con = null;
        ResultSet res = null;
        try {
            con = DBSettings.getConnection();
            String sql = "INSERT INTO author (name) VALUES(?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, newAuthor.getName());

            ps.executeUpdate();
            FacesContext.getCurrentInstance().addMessage(null, messageSuccess);
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, messageFail);
        } finally {
            DBSettings.closeAll(con, ps, res);
        }
        authors.add(newAuthor);
    }
}