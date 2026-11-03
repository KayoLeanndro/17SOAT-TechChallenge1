-- Atualiza somente a credencial do usuario tecnico, preservando as migrations aplicadas.
UPDATE usuario
SET senha_hash = '$2a$12$0xx//30eokeHSpYa4pQ9iekFAV/8YhwQM88CsNvVwk/5MXhHp1dTm'
WHERE login = 'sistema-estoque';
