package br.si.cond.model;

import java.util.ArrayList;
import java.util.Date;

public class Reinvidicacao {
    private Long id;
    private Long user_id;
    private Long condominio_id;
    private String mensagem;
    private String foto;
    private String status;
    private Date created_at;
    private ArrayList<ComentarioReinvidicacao> comentarios;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public ArrayList<ComentarioReinvidicacao> getComentarios() {
        return comentarios;
    }

    public Long getCondominio_id() {
        return condominio_id;
    }

    public void setCondominio_id(Long condominio_id) {
        this.condominio_id = condominio_id;
    }

    public void setComentarios(ArrayList<ComentarioReinvidicacao> comentarios) {
        this.comentarios = comentarios;
    }
}

