package br.com.cadastrocliente.mscadastrocliente.domain.entity;

import br.com.cadastrocliente.mscadastrocliente.domain.valueObject.Endereco;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "TB_CLIENTE")
public class Cliente {

    @Id
    // TENTAR ENTENDER COMO UTILIZAR ISSO SEM IMPACTAR MEUS TESTES
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String cpf;
    private String rg;

    @Embedded
    private Endereco endereco;

    public Cliente(String nome, String email, String senha, String telefone, String cpf, String rg, Endereco endereco) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.cpf = cpf;
        this.rg = rg;
        this.endereco = endereco;
    }
}
