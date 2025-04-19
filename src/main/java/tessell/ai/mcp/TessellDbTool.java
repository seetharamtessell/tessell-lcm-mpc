package tessell.ai.mcp;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tessell.mcp.model.BackupModel;
import tessell.mcp.model.DatabaseServiceRequest;
import tessell.mcp.model.DeleteDbServiceModel;
import tessell.mcp.model.SnapshotModel;
import tessell.mcp.service.TessellDbAdminService;
import tessell.mcp.service.TessellSnapshotAdminService;
import tessell.utils.DbUtils;

import java.util.UUID;

@ApplicationScoped
@Slf4j
public class TessellDbTool {
    @Inject TessellDbAdminService apiService;
    @Inject TessellSnapshotAdminService snapshotService;
    @Inject DbUtils dbUtils;
    ObjectMapper objectMapper = new ObjectMapper();


    @SneakyThrows
    @Tool(name="provision-new-mysql", description = "You can provision or create a new MySQL database only in Tessell " +
            "and expect the database name to be passed as an argument. This function is specifically for Tessell database " +
            "provisioning - if the user is requesting an AWS RDS MySQL instance or a GCP Cloud SQL instance, " +
            "you should use the appropriate tool for those services instead. Only use this function when the user explicitly " +
            "requests a Tessell MySQL database or when Tessell is the context of the conversation.")
    public String provisionNewMysql(
            @ToolArg(description = "The name of the database to provision or create in tessell, " +
                    "if it does not exist give a random name") String databaseName,
            @ToolArg(description = "Set this to true to confirm and execute provisioning. If false or omitted, the tool " +
                    "will only return the configuration for review.")
            boolean confirmProvisioning)  {
//        DatabaseServiceRequest request = objectMapper.readValue(dbUtils.readConfigFile(), DatabaseServiceRequest.class);
//        request.setName(databaseName);
//        return apiService.createService(request).toString();
        try {
            String finalName = (databaseName == null || databaseName.isBlank())
                    ? "tessell_mysql_" + UUID.randomUUID().toString().substring(0, 8)
                    : databaseName;

            // Load config and set name
            DatabaseServiceRequest request = objectMapper.readValue(dbUtils.returnMqlConfig(), DatabaseServiceRequest.class);
            request.setName(finalName);
            String summary = "🛠 Tessell MySQL Provisioning Request:\n" +
                    "- Database Name: " + finalName;

            if (!confirmProvisioning) {
                return Json.createObjectBuilder().add("summary",summary + "\n\n✅ Configuration is ready.\n" +
                        "❗ No database has been created.\n" +
                        "👉 To proceed, please re-run with `confirmProvisioning=true`.").build().toString();
            }

            // Confirmed — proceed with provisioning
            Object response = apiService.createService(request);
            return Json.createObjectBuilder().add("summary",    summary + "" +
                    "\n\n✅ Tessell MySQL database has been successfully provisioned:\n" + response).build().toString();

        } catch (Exception e) {
            return Json.createObjectBuilder().add("summary",
                    "🚫 (Tessell)❌ Failed to process the provisioning request: " + e.getMessage()).build().toString();
        }
    }
    @Tool(name="get-all-databases", description = "You can get all databases in tessell. Only call this function when " +
            "the user explicitly asks about Tessell databases or when Tessell is clearly the context of the conversation.")
    public String getAllDatabases() {
        return apiService.fetchAllServices().toString();
    }
    @Tool(name="delete-database", description="You can delete a database in tessell by id however this is very dangerous " +
            "and requires extreme caution. Only call this function when the user explicitly requests to delete a Tessell database." +
            " Before executing, always confirm with the user by clearly stating which database will be deleted (name and ID)," +
            " warn about the permanent consequences, and ask for explicit confirmation. Double check that the user understands " +
            "what they're deleting and has provided the correct database ID.")
    public String deleteDatabase(@ToolArg(description = "extract id from get-all-databases output from name") String databaseId,
                                 @ToolArg(description = "Does availability machine have to be deleted, please ask if user doesn't specify") boolean retainAvailabilityMachine) {
        DeleteDbServiceModel deleteDbServiceModel = new DeleteDbServiceModel();
        DeleteDbServiceModel.DeletionConfig deletionConfig = new DeleteDbServiceModel.DeletionConfig();
        deletionConfig.setRetainAvailabilityMachine(retainAvailabilityMachine);
        deleteDbServiceModel.setDeletionConfig(deletionConfig);
        deleteDbServiceModel.setPublishEventLog(false);
        return apiService.deleteService(deleteDbServiceModel,databaseId).toString();

    }
    @Tool(name="get-database-id", description="You can get detailed information about a specific database in Tessell using its ID. " +
            "Only call this function when a user explicitly asks for information about a particular Tessell database and " +
            "has provided a database ID, or when you need to retrieve detailed information about a specific " +
            "Tessell database in the conversation context.")
    public String getDatabase(@ToolArg(description = "get database id") String dbId) {
        try {
            JsonObject result = apiService.fetchServerById(dbId);
            return result != null ? result.toString() : Json.createObjectBuilder().add("error", "Database not found").build().toString();
        } catch (Exception e) {
            log.error("Error fetching database details", e);
            return Json.createObjectBuilder().add("error", e.getMessage()).build().toString();
        }
    }
    @Tool(name="create-snapshot", description="You can create a snapshot of a database in Tessell. " +
            "Only call this function when a user explicitly requests to create a snapshot of a specific Tessell database. " +
            "You need both the availability machine ID of the database and a name for the new snapshot. " +
            "Always confirm the database name with the user before proceeding with snapshot creation.")
    public String createSnapshot(
            @ToolArg(description = "extract availability-machine id " +
            "from get-all-databases output from name") String availabilityMachineId,
                                 @ToolArg(description = "Name of the snapshot") String snapshotName) {
        SnapshotModel snapshotModel = new SnapshotModel();
        snapshotModel.setName(snapshotName);
        snapshotModel.setDescription("Genarated by Agent");
        return snapshotService.createSnapshot(availabilityMachineId, snapshotModel).toString();
    }
    //backup
    @Tool(name="create-backup", description="You can create a backup of a database in Tessell from an existing snapshot. " +
            "Only call this function when a user explicitly requests to create a backup from a snapshot of a specific " +
            "Tessell database. You need both the availability machine ID of the database and the " +
            "snapshot ID to be used as the source. Verify with the user that they have the correct " +
            "snapshot ID before proceeding.")
    public String createBackup(
            @ToolArg(description = "extract availability-machine id from get-all-databases output from name") String availabilityMachineId,
            @ToolArg(description = "Expect snapshot id") String snapshotId) {
        BackupModel backupModel = new BackupModel();
        backupModel.setName("Agent_"+ System.nanoTime());
        backupModel.setSnapshotId(snapshotId);
        return snapshotService.createBackup(availabilityMachineId, backupModel).toString();
    }
    //delete backup with backup id and availability machine
    @Tool(name="delete-backup", description="You can delete a backup of a snapshot of a database in Tessell. " +
            "Only call this function when a user explicitly requests to delete a specific backup of a Tessell database. " +
            "This is a permanent operation that cannot be undone. You need both the backup ID and the availability machine ID " +
            "of the database. Always confirm with the user by clearly stating which backup will be deleted before proceeding, " +
            "and verify they understand this action is permanent.")
    public String deleteBackup(@ToolArg(description = "backup id") String backupId,
                               @ToolArg(description = "extract availability-machine id from get-all-databases output from name") String availabilityMachineId) {
        return snapshotService.deleteBackup(availabilityMachineId, backupId).toString();
    }
    //delete snapshot with snapshot id and availability machine
    @Tool(name="delete-snapshot", description="You can delete a snapshot of a database in Tessell by snapshot ID. " +
            "This is a dangerous operation that permanently removes the snapshot and cannot be undone. " +
            "Only call this function when a user explicitly requests to delete a specific " +
            "Tessell snapshot. Before executing, always confirm with the user by clearly " +
            "stating which snapshot will be deleted (name and ID), warn that this cannot be reversed, " +
            "and ask for explicit confirmation. You need both the snapshot ID and the availability machine ID of the database. " +
            "Double check that the user understands what they're deleting.")
    public String deleteSnapshot(@ToolArg(description = "extract snapshot id from get-all-databases output from name") String snapshotId,
                                 @ToolArg(description = "extract availability-machine id from get-all-databases output from name") String availabilityMachineId) {
        return snapshotService.deleteSnapshot(availabilityMachineId, snapshotId).toString();
    }
    //list of snapshots
    @Tool(name="list-snapshots", description="You can list all snapshots of a database in Tessell. " +
            "Only call this function when a user explicitly asks for snapshots of a specific Tessell database. " +
            "You need the availability machine ID of the database to retrieve its snapshots. " +
            "This provides information about all available snapshots for the specified database, " +
            "including their IDs, creation dates, and status.")
    public String listSnapshots(@ToolArg(description = "extract availability-machine id from get-all-databases output from name") String availabilityMachineId) {
        return snapshotService.getSnapshots(availabilityMachineId).toString();
    }
    //lis of backups
    @Tool(name="list-backups", description="You can list all backups of a database in Tessell. " +
            "Only call this function when a user explicitly asks for backups of a specific Tessell database. " +
            "You need the availability machine ID of the database to retrieve its backups. " +
            "This provides information about all available backups for the specified database, including their IDs, creation dates, and status.")

    public String listBackups(@ToolArg(description = "extract availability-machine id from get-all-databases output from name") String availabilityMachineId) {
        return snapshotService.getBackups(availabilityMachineId).toString();
    }
    // stop service by id
    @Tool(name="stop-service", description="You can stop a database service in Tessell. " +
            "Only call this function when a user explicitly requests to stop a specific Tessell database service. " +
            "You need the service ID of the database to stop it. Before executing, confirm with the user " +
            "which database service will be stopped, and inform them that this will temporarily make the database unavailable for connections.")
    public String stopService(@ToolArg(description = "extract id from get-all-databases output from name") String serviceId) {
        return apiService.stopService(serviceId).toString();
    }
    // start service by id
    @Tool(name="start-service", description="You can start a database service in Tessell. " +
            "Only call this function when a user explicitly requests to start a specific Tessell database service " +
            "that is currently stopped. You need the service ID of the database to start it. " +
            "Before executing, confirm with the user which database service will be started, " +
            "and inform them that it may take a few minutes for the service to become available for connections.")
    public String startService(@ToolArg(description = "extract id from get-all-databases output from name") String serviceId) {
        return apiService.startService(serviceId).toString();
    }
}
