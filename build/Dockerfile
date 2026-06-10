FROM maven:3.9.12-eclipse-temurin-21

LABEL description="This image is used for quality build"

# Install system dependencies
RUN apt-get update \
    && apt-get install -y --no-install-recommends bc \
      python3-pip python3-venv pipx \
    && apt-get install -y --only-upgrade libssl3 openssl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Install anybadge via pipx (dans un venv dédié)
RUN pipx install anybadge

# Assurer que le binaire pipx (~/.local/bin) est dans le PATH
ENV PATH="/root/.local/bin:${PATH}"


RUN mkdir -p /var/workspace
WORKDIR /var/workspace

# Copy run script
COPY ./run.sh /run.sh
RUN chmod +x /run.sh

# Copy lombok config file
COPY ./lombok.config /lombok.config

ENTRYPOINT ["/run.sh"]
