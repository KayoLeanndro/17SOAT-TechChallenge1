"""Teste integrado com PostgreSQL vazio; nao publica portas nem usa o banco AWS."""
import pathlib
import secrets
import subprocess
import sys
import time
import uuid

image = sys.argv[1]
prefix = "mechanics-migration-test-" + uuid.uuid4().hex[:10]
network, database, api = prefix, prefix + "-db", prefix + "-api"
password = secrets.token_hex(24)


def docker(*args):
    result = subprocess.run(["docker", *args], capture_output=True, text=True)
    if result.returncode:
        raise RuntimeError(result.stdout + result.stderr)
    return result.stdout


environment = ["-e", "SPRING_DATASOURCE_URL=jdbc:postgresql://" + database + ":5432/migration_test",
               "-e", "SPRING_DATASOURCE_USERNAME=postgres", "-e", "SPRING_DATASOURCE_PASSWORD=" + password]
try:
    docker("network", "create", network)
    docker("run", "-d", "--name", database, "--network", network,
           "-e", "POSTGRES_DB=migration_test", "-e", "POSTGRES_PASSWORD=" + password, "postgres:16")
    for _ in range(30):
        ready = subprocess.run(["docker", "exec", database, "pg_isready", "-U", "postgres"], capture_output=True)
        if ready.returncode == 0:
            break
        time.sleep(1)
    else:
        raise RuntimeError("PostgreSQL de teste nao iniciou.")

    for attempt in range(2):
        docker("run", "--rm", "--network", network, *environment, image, "--migrate-only")
        count = docker("exec", database, "psql", "-U", "postgres", "-d", "migration_test", "-At", "-c",
                       "SELECT count(*) FROM flyway_schema_history WHERE success;").strip()
        expected = len(list(pathlib.Path("src/main/resources/db/migration").glob("V*__*.sql")))
        assert int(count) == expected, "Historico de migrations inesperado."
        print("PASS: migrations em banco vazio" if attempt == 0 else "PASS: segunda execucao idempotente", flush=True)

    docker("run", "-d", "--name", api, "--network", network, *environment,
           "-e", "SPRING_FLYWAY_ENABLED=false", "-e", "SPRING_JPA_HIBERNATE_DDL_AUTO=validate",
           "-e", "JWT_SECRET=" + secrets.token_hex(32), image)
    for _ in range(45):
        logs = docker("logs", api)
        if "Started MechanicsApiApplication" in logs:
            print("PASS: API inicia com Hibernate validate e Flyway desabilitado", flush=True)
            break
        if docker("inspect", "--format", "{{.State.Running}}", api).strip() != "true":
            raise RuntimeError(logs)
        time.sleep(2)
    else:
        raise RuntimeError("API nao iniciou: " + docker("logs", api))
finally:
    for name in [api, database]:
        subprocess.run(["docker", "rm", "-f", "-v", name], capture_output=True)
    subprocess.run(["docker", "network", "rm", network], capture_output=True)
