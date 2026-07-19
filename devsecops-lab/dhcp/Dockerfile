FROM ubuntu:22.04
RUN sed -i 's|archive.ubuntu.com|ubuntu.mirrors.ovh.net|g' /etc/apt/sources.list && \
    sed -i 's|security.ubuntu.com|ubuntu.mirrors.ovh.net|g' /etc/apt/sources.list && \
    apt-get update -qq && \
    apt-get install -y --no-install-recommends isc-dhcp-server && \
    mkdir -p /var/lib/dhcp && \
    touch /var/lib/dhcp/dhcpd.leases
COPY dhcpd.conf /etc/dhcp/dhcpd.conf
RUN echo 'INTERFACESv4="eth0"' > /etc/default/isc-dhcp-server
EXPOSE 67/udp 68/udp
CMD ["/usr/sbin/dhcpd","-4","-f","-d","--no-pid","-cf","/etc/dhcp/dhcpd.conf"]
