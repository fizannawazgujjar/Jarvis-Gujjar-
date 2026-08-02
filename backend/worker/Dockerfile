FROM python:3.11-slim
WORKDIR /app
COPY backend/requirements.txt ./
RUN pip install --no-cache-dir -r backend/requirements.txt
COPY backend/app ./app
COPY backend/plugins ./plugins
ENV PYTHONUNBUFFERED=1
CMD ["celery", "-A", "app.celery_app.celery_app", "worker", "--loglevel=info"]
