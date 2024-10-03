CREATE TABLE IF NOT EXISTS produto (
    id INT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    quantidade INT NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    descricao VARCHAR(200)
);