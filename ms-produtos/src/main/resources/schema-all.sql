DROP TABLE IF EXISTS produto;

create table produto
(
    id               serial,
    descricao             varchar(240),
    quantidade_estoque      int,
    valor           float
)