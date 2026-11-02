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
migration_containers = [prefix + "-migrate-0", prefix + "-migrate-1"]
password = secrets.token_hex(24)


def docker(*args, timeout=120):
    try:
        result = subprocess.run(["docker", *args], capture_output=True, text=True, timeout=timeout)
    except subprocess.TimeoutExpired as error:
        logs = error.stdout or ""
        if isinstance(logs, bytes):
            logs = logs.decode("utf-8", errors="replace")
        hint = ("O modo --migrate-only deve encerrar apos o Flyway; confira se "
                "MechanicsApiApplication.java e DatabaseMigrations.java foram commitados."
                if "--migrate-only" in args else "Confira a disponibilidade do Docker e os containers de teste.")
        raise RuntimeError(f"Docker excedeu {timeout}s. {hint}\n" + logs) from None
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
        print(f"Executando migrations: tentativa {attempt + 1}/2 (limite de 60s)", flush=True)
        docker("run", "--rm", "--name", migration_containers[attempt], "--network", network,
               *environment, image, "--migrate-only", timeout=60)
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
    for name in [*migration_containers, api, database]:
        subprocess.run(["docker", "rm", "-f", "-v", name], capture_output=True, timeout=30)
    subprocess.run(["docker", "network", "rm", network], capture_output=True, timeout=30)
