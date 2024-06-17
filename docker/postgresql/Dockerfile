# Base image
FROM postgres:latest

# Install required packages
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    python3-venv \
    vim \
    postgresql-plpython3-16

# Create a Python virtual environment
RUN python3 -m venv /opt/venv

# Install bcrypt in the virtual environment
RUN /opt/venv/bin/pip install bcrypt

# Set environment variables
ENV PATH="/opt/venv/bin:$PATH"

# Create a profile script to activate the virtual environment
RUN echo "source /opt/venv/bin/activate" >> /etc/profile.d/activate_venv.sh && \
    chmod +x /etc/profile.d/activate_venv.sh

# Set the working directory
WORKDIR /var/lib/postgresql

# Copy initialization scripts
COPY src/main/resources/1_schema.sql /docker-entrypoint-initdb.d/
COPY src/main/resources/2_data.sql /docker-entrypoint-initdb.d/
COPY src/main/resources/3_posts.sql /docker-entrypoint-initdb.d/

# Expose the PostgreSQL port
EXPOSE 5432

# Set the default command to run PostgreSQL
CMD ["postgres"]