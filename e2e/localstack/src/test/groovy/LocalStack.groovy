import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait

import static org.testcontainers.containers.Network.newNetwork

class LocalStack {
    final GenericContainer container
    final accessKey = 'foo'
    final secretKey = 'bar'
    final region = 'eu-west-1'
    final network = newNetwork()

    LocalStack() {
        container = new GenericContainer('localstack/localstack:0.11.3')
                .withEnv(
                        SERVICES: 'kms,s3,lambda,iam',
                        DEFAULT_REGION: region,
                        LAMBDA_EXECUTOR: 'docker',
                        LAMBDA_DOCKER_NETWORK: network.id)
                .withNetwork(network)
                .withNetworkAliases('localstack')
                .withExposedPorts(4566)
                .waitingFor(Wait.forLogMessage(/.*Ready[.].*/, 1))
        container.withFileSystemBind('//var/run/docker.sock', '/var/run/docker.sock')
    }

    def start() {
        container.start()
    }

    def stop() {
        container.stop()
        network.close()
    }

    URI getEndpoint() {
        "http://localhost:${container.getMappedPort(4566)}".toURI()
    }
}