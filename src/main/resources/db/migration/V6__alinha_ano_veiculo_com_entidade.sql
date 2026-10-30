-- A entidade Java usa Integer. Bancos antigos podiam ter este ajuste pelo
-- Hibernate ddl-auto=update; a migration torna o schema reproduzivel do zero.
ALTER TABLE veiculo ALTER COLUMN ano TYPE INTEGER USING ano::INTEGER;
