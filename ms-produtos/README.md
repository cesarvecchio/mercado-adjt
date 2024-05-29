## Execução dos Testes
**Requisitos para executar os comandos a seguir:**
 - Ter acesso ao comando make (Necessário para execução de todos os comandos a seguir);
 - Ter o Gradle instalado (Necessário para execução de todos os comandos a seguir);
 - Ter o Node/NPM instalado + o pacote allure-commandline globalmente (Necessário para visualização dos relatórios dos testes integrados);

### Para executar os testes unitários:
```sh
make unit-test
```

### Para executar os testes integrados:
```sh
make integration-test
```

### Para executar os testes unitários + testes integrados:
```sh
make test
```