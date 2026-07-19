FROM ubuntu:22.04
RUN sed -i 's|archive.ubuntu.com|ubuntu.mirrors.ovh.net|g' /etc/apt/sources.list && \
    sed -i 's|security.ubuntu.com|ubuntu.mirrors.ovh.net|g' /etc/apt/sources.list && \
    apt-get update -qq && \
    apt-get install -y --no-install-recommends bind9 bind9utils dnsutils && \
    mkdir -p /etc/bind/zones
COPY named.conf.options /etc/bind/
COPY named.conf.local /etc/bind/
COPY zones/ /etc/bind/zones/
RUN chown -R bind:bind /etc/bind/zones
EXPOSE 53/tcp 53/udp
CMD ["named", "-g", "-u", "bind"]
