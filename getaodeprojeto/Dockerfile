# Usar a imagem oficial do MySql
FROM mysql:8.0

# Definir a variável de ambiente para a senha root
ENV MYSQL_ROOT_PASSWORD=rootpassword

# Definir o nome do banco de dados que será criado
ENV MYSQL_DATABASE=meu_banco

# Copiar o arquivo de criação de tabela SQL para o container
COPY ./init.sql /docker-entrypoint-initdb.d/

# Expor a porta padrão do MySql
EXPOSE 3306