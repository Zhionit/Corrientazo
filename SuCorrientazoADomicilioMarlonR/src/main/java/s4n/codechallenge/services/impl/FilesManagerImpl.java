package s4n.codechallenge.services.impl;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;
import s4n.codechallenge.actorsdtos.FilesManagerDtoCmd;
import s4n.codechallenge.actorsdtos.RoutePlanningDtoCmd;
import s4n.codechallenge.actorsdtos.communication.FileManagerToRoutePlanningCmd;
import s4n.codechallenge.actorsdtos.communication.MainToFileManagerCmd;
import s4n.codechallenge.actorsdtos.communication.RoutesPlanningToFileManagerDto;
import s4n.codechallenge.services.FilesManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.HashSet;

public class FilesManagerImpl extends AbstractBehavior<FilesManagerDtoCmd> implements FilesManager {

    public static final String IN_COME_ROUTE = "input";
    public static final String OUT_COME_ROUTE = "output";
    public static final String FILE_ENDING = ".txt";
    public static final String OUTPUT_FILE_NAME = "out";

    private final ActorRef<RoutePlanningDtoCmd> childRoutePlanningDtoCmdActorRef;

    public FilesManagerImpl(ActorContext<FilesManagerDtoCmd> context) {
        super(context);
        childRoutePlanningDtoCmdActorRef = context.spawn(RoutePlanningImpl.create(), "RoutePlanningActuator");
        createReceive();
    }

    @Override
    public Receive<FilesManagerDtoCmd> createReceive() {

        ReceiveBuilder<FilesManagerDtoCmd> receiveBuilder = newReceiveBuilder();

        receiveBuilder.onMessage(RoutesPlanningToFileManagerDto.class, this::listenChildCall);
        receiveBuilder.onMessage(MainToFileManagerCmd.class, this::listenMainCall);

        return receiveBuilder.build();
    }

    private void createDirectoryWatcher() {
        try {
            Path faxFolder = Paths.get(buildDirectoryRoute(IN_COME_ROUTE));
            WatchService watchService = FileSystems.getDefault().newWatchService();
                faxFolder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            boolean valid = true;
            do {
                WatchKey watchKey = watchService.take();

                for (WatchEvent event : watchKey.pollEvents()) {
                    WatchEvent.Kind kind = event.kind();
                    if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
                        String fileName = event.context().toString();
                        getContext().getLog().info("New file arrived: " + fileName);
                        readFiles(fileName);
                    }
                }
                valid = watchKey.reset();

            } while (valid);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String buildDirectoryRoute(String inComeRoute) {
        return new StringBuilder()
                .append(".")
                .append(File.separator)
                .append("src")
                .append(File.separator)
                .append("main")
                .append(File.separator)
                .append("resources")
                .append(File.separator)
                .append(inComeRoute)
                .append(File.separator)
                .toString();
    }

    private void checkDirectories(String folderName) {
        String directoryRoute = buildDirectoryRoute(folderName);
        File directory = new File(directoryRoute);
        if (!directory.exists()){
            directory.mkdir();

            getContext().getLog().info("The directory {} has been created", directoryRoute);
        }
    }

    public static Behavior<FilesManagerDtoCmd> create() {
        return Behaviors.setup(FilesManagerImpl::new);
    }

    @Override
    public void readFiles(String fileName) {
        String data = "";

        try {
            data = new String(Files.readAllBytes(Paths.get(buildDirectoryRoute(IN_COME_ROUTE) + fileName)));
        } catch (IOException e) {
            getContext().getLog().error("Can not read some file");
        }

        processFile(data, fileName.substring(0, fileName.length() - 4));
    }

    private void processFile(String data, String name) {
        callChildActuator(data, name);
    }

    @Override
    public void callChildActuator(String data, String fileName) {

        FileManagerToRoutePlanningCmd parameter = FileManagerToRoutePlanningCmd.builder()
                .encodedOrders(new HashSet<String>(Collections.singleton("DDI")))
                .fileName(fileName)
                .replyTo(getContext().getSelf())
                .build();

        childRoutePlanningDtoCmdActorRef.tell(parameter);
    }

    @Override
    public Behavior<FilesManagerDtoCmd> listenChildCall(RoutesPlanningToFileManagerDto routesPlanningToFileManagerDto) {

        String fileContent = buildFileContent(routesPlanningToFileManagerDto.getDeliveryOrderReport());
        byte[] dataBytes = fileContent.getBytes();

        Path path = Paths.get(buildOutputNameFile(routesPlanningToFileManagerDto.getDroneId()));
        writeFiles(path, dataBytes);

        return this;
    }

    private String buildFileContent(String deliveryOrderReport) {

        String headerString = "== Reporte de entregas ==";
        StringBuilder stringBuilder = new StringBuilder(headerString);
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(deliveryOrderReport);

        return stringBuilder.toString();
    }

    private String buildOutputNameFile(int droneId) {
        return buildDirectoryRoute(OUT_COME_ROUTE + OUTPUT_FILE_NAME + droneId + FILE_ENDING);
    }

    @Override
    public void writeFiles(Path path, byte[] dataBytes) {
        try {
            Files.write(path, dataBytes);
        } catch (IOException e) {
            getContext().getLog().error("Error writing file");
        }
    }

    @Override
    public Behavior<FilesManagerDtoCmd> listenMainCall(MainToFileManagerCmd filesManagerCmd) {

        checkDirectories(IN_COME_ROUTE);
        checkDirectories(OUT_COME_ROUTE);
        createDirectoryWatcher();

        return this;
    }
}
