#!/bin/sh
set -e

echo "Running SQL scripts on container start..."

# путь внутри контейнера к папке с SQL-файлами
SQL_DIR=/docker-entrypoint-initdb.d

for f in "$SQL_DIR"/*.sql; do
  if [ -f "$f" ]; then
    echo "Executing $f"
    psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f "$f"
  fi
done
