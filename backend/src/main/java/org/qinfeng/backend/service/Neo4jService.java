package org.qinfeng.backend.service;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Neo4jService {

    private static final Logger log = LoggerFactory.getLogger(Neo4jService.class);

    @Autowired
    private Driver neo4jDriver;

    public void createNode(String label, Map<String, Object> properties) {
        try (Session session = neo4jDriver.session()) {
            String propString = buildPropertiesString(properties);
            String cypher = String.format("CREATE (n:%s {%s}) RETURN n", label, propString);
            session.run(cypher);
        }
    }

    public void createNodeWithId(String label, Long id, Long projectId, String name, String entityTypeName, String color) {
        try (Session session = neo4jDriver.session()) {
            String cypher = String.format(
                "CREATE (n:%s {id: $id, projectId: $projectId, name: $name, entityTypeName: $entityTypeName, color: $color})",
                label
            );
            log.info("Creating node: label={}, id={}, name={}, type={}", label, id, name, entityTypeName);
            session.run(cypher, 
                Values.parameters("id", id, "projectId", projectId, "name", name, 
                    "entityTypeName", entityTypeName, "color", color));
            log.info("Node created successfully: {}", name);
        } catch (Exception e) {
            log.error("Failed to create node: {}", e.getMessage(), e);
        }
    }

    public void createRelation(Long sourceId, Long targetId, String relationType, Long projectId) {
        try (Session session = neo4jDriver.session()) {
            String cypher = String.format(
                "MATCH (a {id: $sourceId, projectId: $projectId}), (b {id: $targetId, projectId: $projectId}) " +
                "CREATE (a)-[r:%s {projectId: $projectId}]->(b) RETURN r",
                relationType.toUpperCase().replace(" ", "_")
            );
            log.info("Creating relation: {} -> {} -> {}", sourceId, relationType, targetId);
            session.run(cypher, Values.parameters("sourceId", sourceId, "targetId", targetId, "projectId", projectId));
        } catch (Exception e) {
            log.error("Failed to create relation: {}", e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> getNodesByProjectId(Long projectId) {
        log.info("Getting nodes for project: {}", projectId);
        try (Session session = neo4jDriver.session()) {
            String cypher = "MATCH (n {projectId: $projectId}) RETURN n.id as id, n.name as name, " +
                    "n.entityTypeName as entityTypeName, n.color as color, labels(n) as labels";
            Result result = session.run(cypher, Values.parameters("projectId", projectId));
            List<Map<String, Object>> nodes = new ArrayList<>();
            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> node = new HashMap<>();
                node.put("id", record.get("id").asLong());
                node.put("name", record.get("name").asString());
                node.put("entityTypeName", record.get("entityTypeName").asString(""));
                node.put("color", record.get("color").asString("#409EFF"));
                List<String> labels = record.get("labels").asList(v -> v.asString());
                if (!labels.isEmpty()) {
                    node.put("label", labels.get(0));
                }
                nodes.add(node);
            }
            log.info("Found {} nodes for project {}", nodes.size(), projectId);
            return nodes;
        } catch (Exception e) {
            log.error("Failed to get nodes: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public List<Map<String, Object>> getRelationsByProjectId(Long projectId) {
        try (Session session = neo4jDriver.session()) {
            String cypher = "MATCH (a {projectId: $projectId})-[r]->(b {projectId: $projectId}) " +
                    "RETURN a.id as source, b.id as target, type(r) as relationType";
            Result result = session.run(cypher, Values.parameters("projectId", projectId));
            List<Map<String, Object>> relations = new ArrayList<>();
            while (result.hasNext()) {
                Record record = result.next();
                Map<String, Object> relation = new HashMap<>();
                relation.put("source", record.get("source").asLong());
                relation.put("target", record.get("target").asLong());
                relation.put("relationType", record.get("relationType").asString());
                relations.add(relation);
            }
            return relations;
        }
    }

    public void deleteNode(Long nodeId, Long projectId) {
        try (Session session = neo4jDriver.session()) {
            String cypher = "MATCH (n {id: $id, projectId: $projectId}) DETACH DELETE n";
            session.run(cypher, Values.parameters("id", nodeId, "projectId", projectId));
        }
    }

    public void deleteRelation(Long sourceId, Long targetId, Long projectId) {
        try (Session session = neo4jDriver.session()) {
            String cypher = "MATCH (a {id: $sourceId, projectId: $projectId})-[r]->(b {id: $targetId, projectId: $projectId}) DELETE r";
            session.run(cypher, Values.parameters("sourceId", sourceId, "targetId", targetId, "projectId", projectId));
        }
    }

    public void clearProjectData(Long projectId) {
        try (Session session = neo4jDriver.session()) {
            String cypher = "MATCH (n {projectId: $projectId}) DETACH DELETE n";
            session.run(cypher, Values.parameters("projectId", projectId));
        }
    }

    public Map<String, Object> getGraphData(Long projectId) {
        Map<String, Object> graphData = new HashMap<>();
        graphData.put("nodes", getNodesByProjectId(projectId));
        graphData.put("edges", getRelationsByProjectId(projectId));
        return graphData;
    }

    private String buildPropertiesString(Map<String, Object> properties) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(entry.getKey()).append(": $").append(entry.getKey());
            first = false;
        }
        return sb.toString();
    }
}
