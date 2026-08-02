#!/bin/sh
set -e
# Start a Celery worker for the Jarvis app
celery -A app.celery_app.celery_app worker --loglevel=info
