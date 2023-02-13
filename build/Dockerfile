FROM maven:3.8.6-jdk-11

LABEL description="This image is used for quality build"

# Install System dependencies
RUN apt-get update -qq && apt-get install -y \
locales \
python3 \
python3-pip \
bc
RUN apt-get clean
# Install anybadge for coverage
# RUN pip3 install --upgrade pip
RUN pip3 install anybadge

RUN mkdir -p /var/workspace
WORKDIR /var/workspace

# Copy run script
COPY ./run.sh /run.sh
RUN chmod +x /run.sh

# Copy lombok config file
COPY ./lombok.config /lombok.config

ENTRYPOINT ["/run.sh"]
