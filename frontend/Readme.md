# 📘 Documentação: Projeto Atendimento Apae (Frontend)

  Esta documentação aborda quais tecnologias são usadas no projeto frontend **Atendimento Apae,** bem como a forma de configurá-lo e executá-lo localmente. Além disso, especifica como instalar as dependências do projeto. Assim, depois da leitura deste documento, você acessará a aplicação via localhost.  

---

# 🧩 Tecnologias Usadas

O projeto frontend **Atendimento Apae** é feito nas seguintes tecnologias:

- Typescript(TS): Linguagem que adiciona tipagem estática ao Javascript. Torna o código mais seguro e fácil de manter.
- Next.js: Framework React que facilita a criação de aplicações web modernas.
- Pnpm: Gerenciador de pacotes alternativo ao npm.
- Node.js: Ambiente de execução Javascript, responsável por rodar o código localmente.

---

# ⚙️ Configurações do Ambiente

## Pré-requisitos ##

Antes de iniciar a aplicação localmente, é necessário que o ambiente esteja preparado. Para isso, verifique se os seguintes itens estão instalados e configurados corretamente:

- [ ]  Algum ambiente de desenvolvimento integrado (IDE), recomenda-se o [VSCode](https://code.visualstudio.com/);
- [ ]  O ambiente de execução Javascript, o [Node.js](https://nodejs.org/);
- [ ]  Sistema de controle de versão, o [Git](https://git-scm.com/book/pt-pt/v2/Come%C3%A7ando-Instalar-o-Git);


>**💡Observação:**
>Caso não tenha feito pelo menos um dos passos acima, recomenda-se que os faça antes de prosseguir. 
Ao baixar o Node, prefira a versão LTS mais recente para evitar bugs ao executar a aplicação.

## Acesso ao Repositório git ## 
1. Abra o VSCode
   
2. Clone o repositório

```bash
git clone https://github.com/IFPBEsp/APAE-atendimento.git
```

3. Entre na pasta `APAE-atendimento` contendo o projeto
   
```bash   
cd APAE-atendimento
```

4. Acesse a branch dev

```bash
git checkout dev
```

5. Acesse o diretório do frontend

```bash
cd frontend/atendimento-app 
```
---

# �� Instalação das Dependências

 O projeto frontend **Atendimento Apae** utiliza o **pnpm**, gerenciador de pacotes do Node.js, moderno, eficiente em processamento computacional e em ocupação de menos espaço em disco. Então, antes de instalar todas as dependências e iniciar o projeto, há a necessidade de instalar globalmente na máquina com o comando:

```bash
npm i -g pnpm -y 
```

- **npm:**  (node package manager) Indica que se deseja executar o comando através do gerenciador de pacotes do Javascript;
- **i:**  É a abreviação da palavra inglesa **install.** Indica ao npm a instalação de um pacote;
- **-g:** Instala o pacote globalmente, o que permite usar em qualquer outro projeto;
- **-y:** Aceita automaticamente todas as confirmações de instalação;

     Após o processo anterior, pode-se instalar todas as dependências com o seguinte comando:

```bash
pnpm i 
```


>*💡Observação:*
> As adições das dependências futuras ocorrem a partir de agora com o comando bash `pnpm add` < dependencia > ou  `pnpm i` < dependencia >. Em que, dependecia é o nome da dependência.

---


## ⚠️ Atenção: Por que é importante instalar o pnpm antes de instalar todas as dependências e iniciar o projeto? ⚠️

 É importante instalar o pnpm antes de instalar as dependências do projeto, pois o projeto foi criado usando esse gerenciador de dependências, garantindo que as versões das dependências sejam consistentes e evitando problemas de build.


---


# 🚀 Inicialização do Projeto

Com todos os requisitos anteriores atendidos, pode-se iniciar o projeto conforme os comandos presentes no arquivo `package.json`:

### Desenvolvimento ###
```bash
pnpm dev # inicialização da aplicação em fase de desenvolvimento
```

### Produção ###
```bash
pnpm build # Gera o build da aplicação para produção
```
​
---

# 💻 Acesso à Aplicação Frontend

Por fim, pode-se acessá-la localmente pelo localhost na porta 3000. [Acessar Aplicação](http://localhost:3000/)


> 💡 **Observação (relacionada à seção anterior):**  
> O acesso acima somente funcionará caso o projeto tenha sido inicializado conforme descrito na seção [🚀 Inicialização do Projeto](#-inicialização-do-projeto).
