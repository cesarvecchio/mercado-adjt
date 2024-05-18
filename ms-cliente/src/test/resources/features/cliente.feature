# language: pt
Funcionalidade: Cliente
  Cenario: Cadastrar um cliente
    Quando cadastar um novo cliente
    Entao o cliente e cadastrado com sucesso
    E deve ser apresentado

  Cenario: Buscar Todos Cliente
    Dado que um cliente foi cadastrado
    Quando eu realizar uma busca de todos os clientes
    Entao a lista de clientes deve ser apresentado com sucesso

  Cenario: Buscar um cliente
    Dado que um cliente foi cadastrado
    Quando eu realizar uma busca desse cliente
    Entao o cliente deve ser apresentado com sucesso

  Cenario: Atualizar um cliente
    Dado que um cliente foi cadastrado
    Quando eu atualizar esse cliente
    Entao o cliente deve ser atualizado com sucesso
    E deve ser apresentado atualizado

  Cenario: Deletar um cliente
    Dado que um cliente foi cadastrado
    Quando deletar esse cliente
    Entao o cliente deve ser deletado com sucesso


