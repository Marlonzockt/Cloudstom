package de.mrln.cloudstom;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.velocity.VelocityProxy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;

public class Cloudstom {

    private static final Path VELOCITY_SECRET_PATH = Path.of("./velocity_secret.txt");

    public static void main(String[] args) {
        String velocitySecret = null;
        try {
            velocitySecret = Files.readString(VELOCITY_SECRET_PATH);
        } catch (NoSuchFileException e) {
            MinecraftServer.LOGGER.error("File velocity_secret.txt is missing. This file must be in the same directory as the server jar file!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (velocitySecret == null) {
            System.exit(0);
            return;
        }
        MinecraftServer server = MinecraftServer.init();

        VelocityProxy.enable(velocitySecret);

        CloudNetDriver driver = CloudNetDriver.getInstance();
        String name = driver.getComponentName();
        ServiceInfoSnapshot service = Objects.requireNonNull(driver.getCloudServiceProvider().getCloudServiceByName(name));

        MinecraftServer.getSchedulerManager().buildShutdownTask(() -> service.provider().stop());

        server.start("0.0.0.0", service.getConfiguration().getPort());
    }
}