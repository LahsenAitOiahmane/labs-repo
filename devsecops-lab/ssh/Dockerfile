FROM ubuntu:22.04
RUN sed -i 's|archive.ubuntu.com|ubuntu.mirrors.ovh.net|g' /etc/apt/sources.list && \
    sed -i 's|security.ubuntu.com|ubuntu.mirrors.ovh.net|g' /etc/apt/sources.list && \
    apt-get update && \
    apt-get install -y --no-install-recommends openssh-server && \
    mkdir /var/run/sshd && \
    useradd -m -s /bin/bash etudiant && \
    echo "etudiant:etudiant" | chpasswd && \
    mkdir -p /home/etudiant/.ssh && \
    chown -R etudiant:etudiant /home/etudiant
COPY --chown=etudiant:etudiant sshd_config /etc/ssh/sshd_config
COPY --chown=etudiant:etudiant authorized_keys /home/etudiant/.ssh/authorized_keys
RUN chmod 700 /home/etudiant/.ssh && \
    chmod 600 /home/etudiant/.ssh/authorized_keys
EXPOSE 22
CMD ["/usr/sbin/sshd", "-D"]
