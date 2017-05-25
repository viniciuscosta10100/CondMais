package br.si.cond.model;

import java.util.Date;

public class ComentarioReinvidicacao {
    private Long id;
    private Long reinvidicacao_id;
    private Long user_id;
    private String mensagem;
    private User user;
    private Date created_at;
    private Boolean concluido;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReinvidicacao_id() {
        return reinvidicacao_id;
    }

    public void setReinvidicacao_id(Long reinvidicacao_id) {
        this.reinvidicacao_id = reinvidicacao_id;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Boolean getConcluido() { return concluido;  }

    public void setConcluido(Boolean concluido) {  this.concluido = concluido;  }
}

