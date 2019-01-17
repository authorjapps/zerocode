FROM confluentinc/cp-kafka

VOLUME ["/var/lib/${COMPONENT}/data", "/etc/${COMPONENT}/secrets"]

COPY include/etc/confluent/docker /etc/confluent/docker

CMD ["/etc/confluent/docker/run"]

ENTRYPOINT ["/docker_entrypoint.sh"]
