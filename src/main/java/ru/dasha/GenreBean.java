package ru.dasha;

import ru.dasha.book.model.Genre;
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

@ManagedBean(name = "genre")
@ApplicationScoped
public class GenreBean implements Serializable {
    private List<Genre> genres;
    private Genre newGenre = new Genre();

    public Genre getNewGenre() {
        return newGenre;
    }

    public void setNewGenre(Genre newGenre) {
        this.newGenre = newGenre;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public GenreBean() {
        DBSettings.dbInit();
    }

    @PostConstruct
    public void init() {
        genres = getGenreList();
    }

    public List<Genre> getGenreList() {
        List<Genre> list = new ArrayList<Genre>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;

        try {
            con = DBSettings.getConnection();
            ps = con.prepareStatement("SELECT id, name FROM genre");
            res = ps.executeQuery();

            while (res.next()) {
                Genre genre = new Genre();

                genre.setId(res.getInt("id"));
                genre.setName(res.getString("name"));
                list.add(genre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBSettings.closeAll(con, ps, res);
        }
        return list;
    }

    public void createGenre(ActionEvent event) {
        FacesMessage messageSuccess = new FacesMessage(FacesMessage.SEVERITY_INFO, "Жанр успешно создан", newGenre.getName());
        FacesMessage messageFail = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Жанр почему-то не был создан", null);

        PreparedStatement ps = null;
        Connection con = null;
        ResultSet res = null;
        try {
            con = DBSettings.getConnection();
            String sql = "INSERT INTO genre (name) VALUES(?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, newGenre.getName());

            ps.executeUpdate();
            FacesContext.getCurrentInstance().addMessage(null, messageSuccess);
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, messageFail);
        } finally {
            DBSettings.closeAll(con, ps, res);
        }
        genres.add(newGenre);
    }
}
